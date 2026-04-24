package com.graduation.crm.modules.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.modules.ai.config.AiProperties;
import com.graduation.crm.modules.ai.dto.AiAudioInputDTO;
import com.graduation.crm.modules.ai.dto.AiChatDTO;
import com.graduation.crm.modules.ai.service.AiLogService;
import com.graduation.crm.modules.ai.vo.AiChatVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class QwenAiClient {

    private final AiProperties aiProperties;
    private final RestTemplate aiRestTemplate;
    private final ObjectMapper objectMapper;
    private final AiLogService aiLogService;

    /**
     * 非流式调用，适合后端任务场景；前端对话优先使用 chatStream。
     */
    public AiChatVO chat(AiChatDTO dto) {
        validateApiKey();

        Map<String, Object> request = buildRequest(dto);
        if (hasAudioInput(dto)) {
            request.put("model", resolveAudioModelName());
            request.put("stream", true);
            return chatByCollectingStream(dto, request);
        }
        String requestJson = toJson(request);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiProperties.getApiKey());

            String url = aiProperties.getBaseUrl() + "/chat/completions";
            String responseJson = aiRestTemplate.postForObject(url, new HttpEntity<>(request, headers), String.class);
            AiChatVO vo = parseResponse(responseJson);
            aiLogService.saveSuccess(resolveBizType(dto), dto.getBizId(), aiProperties.getModelName(), requestJson, responseJson, vo.getTokenUsage());
            return vo;
        } catch (Exception e) {
            aiLogService.saveFailed(resolveBizType(dto), dto.getBizId(), aiProperties.getModelName(), requestJson, e.getMessage());
            throw new BusinessException("AI调用失败：" + e.getMessage());
        }
    }

    /**
     * SSE 流式调用，逐段向前端推送模型输出。
     */
    public SseEmitter chatStream(AiChatDTO dto) {
        validateApiKey();
        SseEmitter emitter = new SseEmitter(0L);
        Thread worker = new Thread(() -> streamChat(dto, emitter), "qwen-chat-stream");
        worker.setDaemon(true);
        worker.start();
        return emitter;
    }

    private AiChatVO chatByCollectingStream(AiChatDTO dto, Map<String, Object> request) {
        String requestJson = toJson(request);
        String modelName = String.valueOf(request.get("model"));
        StringBuilder fullContent = new StringBuilder();
        Integer tokenUsage = null;
        try {
            HttpURLConnection connection = openStreamConnection(requestJson);
            int status = connection.getResponseCode();
            InputStream responseStream = status >= 200 && status < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            if (status < 200 || status >= 300) {
                String errorBody = readAll(responseStream);
                aiLogService.saveFailed(resolveBizType(dto), dto.getBizId(), modelName, requestJson, errorBody);
                throw new BusinessException("AI调用失败：" + errorBody);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("data:")) {
                        continue;
                    }
                    String payload = line.substring(5).trim();
                    if ("[DONE]".equals(payload)) {
                        break;
                    }
                    StreamChunk chunk = parseStreamChunk(payload);
                    if (chunk.content != null) {
                        fullContent.append(chunk.content);
                    }
                    if (chunk.tokenUsage != null) {
                        tokenUsage = chunk.tokenUsage;
                    }
                }
            }

            AiChatVO vo = new AiChatVO();
            vo.setContent(fullContent.toString());
            vo.setModelName(modelName);
            vo.setTokenUsage(tokenUsage);
            aiLogService.saveSuccess(resolveBizType(dto), dto.getBizId(), modelName, requestJson, toJson(vo), tokenUsage);
            return vo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            aiLogService.saveFailed(resolveBizType(dto), dto.getBizId(), modelName, requestJson, e.getMessage());
            throw new BusinessException("AI调用失败：" + e.getMessage());
        }
    }

    private void streamChat(AiChatDTO dto, SseEmitter emitter) {
        Map<String, Object> request = buildStreamRequest(dto);
        String requestJson = toJson(request);
        StringBuilder fullContent = new StringBuilder();
        Integer tokenUsage = null;

        try {
            HttpURLConnection connection = openStreamConnection(requestJson);
            int status = connection.getResponseCode();
            InputStream responseStream = status >= 200 && status < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            if (status < 200 || status >= 300) {
                String errorBody = readAll(responseStream);
                aiLogService.saveFailed(resolveBizType(dto), dto.getBizId(), aiProperties.getModelName(), requestJson, errorBody);
                sendError(emitter, "AI调用失败：" + errorBody);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("data:")) {
                        continue;
                    }
                    String payload = line.substring(5).trim();
                    if ("[DONE]".equals(payload)) {
                        emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                        break;
                    }
                    StreamChunk chunk = parseStreamChunk(payload);
                    if (chunk.content != null && !chunk.content.isEmpty()) {
                        fullContent.append(chunk.content);
                        emitter.send(SseEmitter.event().name("message").data(chunk.content));
                    }
                    if (chunk.tokenUsage != null) {
                        tokenUsage = chunk.tokenUsage;
                    }
                }
            }

            AiChatVO response = new AiChatVO();
            response.setContent(fullContent.toString());
            response.setModelName(aiProperties.getModelName());
            response.setTokenUsage(tokenUsage);
            aiLogService.saveSuccess(resolveBizType(dto), dto.getBizId(), aiProperties.getModelName(), requestJson, toJson(response), tokenUsage);
            emitter.complete();
        } catch (Exception e) {
            log.error("Qwen stream chat failed", e);
            aiLogService.saveFailed(resolveBizType(dto), dto.getBizId(), aiProperties.getModelName(), requestJson, e.getMessage());
            sendError(emitter, "AI调用失败：" + e.getMessage());
        }
    }

    private HttpURLConnection openStreamConnection(String requestJson) throws Exception {
        URL url = new URL(aiProperties.getBaseUrl() + "/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(aiProperties.getTimeoutSeconds() * 1000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        connection.setRequestProperty("Accept", MediaType.TEXT_EVENT_STREAM_VALUE);
        connection.setRequestProperty("Authorization", "Bearer " + aiProperties.getApiKey());

        byte[] bytes = requestJson.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(bytes);
        }
        return connection;
    }

    private Map<String, Object> buildRequest(AiChatDTO dto) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", aiProperties.getModelName());
        request.put("temperature", aiProperties.getTemperature());
        request.put("max_tokens", aiProperties.getMaxTokens());
        request.put("top_p", aiProperties.getTopP());

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", buildContent(dto));
        messages.add(userMessage);
        request.put("messages", messages);
        return request;
    }

    private Map<String, Object> buildStreamRequest(AiChatDTO dto) {
        Map<String, Object> request = buildRequest(dto);
        request.put("stream", true);
        return request;
    }

    private List<Map<String, Object>> buildContent(AiChatDTO dto) {
        List<Map<String, Object>> content = new ArrayList<>();

        Map<String, Object> text = new HashMap<>();
        text.put("type", "text");
        text.put("text", dto.getPrompt());
        content.add(text);

        if (dto.getImageUrls() != null) {
            for (String imageUrl : dto.getImageUrls()) {
                Map<String, Object> image = new HashMap<>();
                Map<String, Object> imageUrlNode = new HashMap<>();
                imageUrlNode.put("url", imageUrl);
                image.put("type", "image_url");
                image.put("image_url", imageUrlNode);
                content.add(image);
            }
        }

        if (dto.getAudios() != null) {
            for (AiAudioInputDTO audio : dto.getAudios()) {
                Map<String, Object> audioNode = new HashMap<>();
                Map<String, Object> inputAudio = new HashMap<>();
                inputAudio.put("data", audio.getData());
                inputAudio.put("format", audio.getFormat() == null ? "wav" : audio.getFormat());
                audioNode.put("type", "input_audio");
                audioNode.put("input_audio", inputAudio);
                content.add(audioNode);
            }
        }
        return content;
    }

    private AiChatVO parseResponse(String responseJson) throws Exception {
        JsonNode root = objectMapper.readTree(responseJson);
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        AiChatVO vo = new AiChatVO();
        vo.setContent(contentNode.asText());
        vo.setModelName(aiProperties.getModelName());
        if (root.has("usage") && root.path("usage").has("total_tokens")) {
            vo.setTokenUsage(root.path("usage").path("total_tokens").asInt());
        }
        return vo;
    }

    private StreamChunk parseStreamChunk(String payload) throws Exception {
        JsonNode root = objectMapper.readTree(payload);
        JsonNode delta = root.path("choices").path(0).path("delta");
        String content = delta.path("content").asText("");
        Integer tokenUsage = null;
        if (root.has("usage") && root.path("usage").has("total_tokens")) {
            tokenUsage = root.path("usage").path("total_tokens").asInt();
        }
        return new StreamChunk(content, tokenUsage);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String readAll(InputStream inputStream) throws Exception {
        if (inputStream == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    private void sendError(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event().name("error").data(message));
        } catch (Exception sendException) {
            log.warn("Failed to send SSE error message", sendException);
        } finally {
            emitter.complete();
        }
    }

    private String resolveBizType(AiChatDTO dto) {
        return dto.getBizType() == null ? "AI_CHAT" : dto.getBizType();
    }

    private boolean hasAudioInput(AiChatDTO dto) {
        return dto.getAudios() != null && !dto.getAudios().isEmpty();
    }

    private String resolveAudioModelName() {
        String audioModelName = aiProperties.getAudioModelName();
        return audioModelName == null || audioModelName.trim().isEmpty()
                ? aiProperties.getModelName()
                : audioModelName;
    }

    private void validateApiKey() {
        if (aiProperties.getApiKey() == null || aiProperties.getApiKey().trim().isEmpty()) {
            throw new BusinessException("未配置 QWEN_API_KEY 环境变量");
        }
    }

    private static class StreamChunk {
        private final String content;
        private final Integer tokenUsage;

        private StreamChunk(String content, Integer tokenUsage) {
            this.content = content;
            this.tokenUsage = tokenUsage;
        }
    }
}
