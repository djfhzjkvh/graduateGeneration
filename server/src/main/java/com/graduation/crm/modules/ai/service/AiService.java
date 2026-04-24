package com.graduation.crm.modules.ai.service;

import com.graduation.crm.modules.ai.dto.AiChatDTO;
import com.graduation.crm.modules.ai.dto.LeadConfirmDTO;
import com.graduation.crm.modules.ai.dto.LeadExtractDTO;
import com.graduation.crm.modules.ai.dto.ScriptGenerateDTO;
import com.graduation.crm.modules.ai.vo.AiChatVO;
import com.graduation.crm.modules.ai.vo.LeadExtractVO;
import com.graduation.crm.modules.ai.vo.ScriptGenerateVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiService {

    AiChatVO chat(AiChatDTO dto);

    SseEmitter chatStream(AiChatDTO dto);

    ScriptGenerateVO generateScript(ScriptGenerateDTO dto);

    LeadExtractVO extractLead(LeadExtractDTO dto);

    Long confirmLead(LeadConfirmDTO dto);
}
