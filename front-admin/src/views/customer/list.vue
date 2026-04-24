<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">客户列表</h1>
      <p class="page-desc">管理全部客户信息，支持客户分配、状态流转和详情追踪</p>
    </div>

    <div class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item>
          <el-input v-model="filters.keyword" placeholder="客户姓名或手机号" clearable style="width:200px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.status" placeholder="客户状态" clearable style="width:120px">
            <el-option v-for="s in CUSTOMER_STATUS" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="filters.deptId" placeholder="所属部门" clearable style="width:120px">
            <el-option v-for="d in flatDepts" :key="d.id" :label="d.deptName" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="热度">
          <el-input v-model.number="filters.minHeatScore" placeholder="最低" style="width:64px" />
          <span style="margin:0 4px; color:var(--text-400)">~</span>
          <el-input v-model.number="filters.maxHeatScore" placeholder="最高" style="width:64px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div style="margin-top:8px; font-size:12px; color:var(--text-400)">
        意向等级、客户来源筛选待后端 CustomerQueryDTO 扩展后接入
      </div>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <span class="table-title">客户列表</span>
        <span class="table-count">共 {{ total }} 条</span>
        <div class="toolbar-actions">
          <el-button size="small" type="primary" @click="openImport">导入客户</el-button>
          <el-button size="small" :loading="templateDownloading" @click="downloadImportTemplate">下载 Excel 模板</el-button>
          <el-button size="small" disabled title="导出接口待接入">↓ 导出</el-button>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" row-key="id">
        <el-table-column label="客户姓名" min-width="130" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="cell-primary cell-ellipsis">{{ row.customerName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="155">
          <template #default="{ row }"><span class="cell-mono">{{ row.mobile }}</span></template>
        </el-table-column>
        <el-table-column label="客户状态" width="115">
          <template #default="{ row }">
            <span :class="['badge', getBadge(CUSTOMER_STATUS, row.status)]">{{ getLabel(CUSTOMER_STATUS, row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="意向等级" width="115">
          <template #default="{ row }">
            <span :class="['badge', getBadge(INTENT_LEVEL, row.intentLevel)]">{{ getLabel(INTENT_LEVEL, row.intentLevel) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="热度分" width="155">
          <template #default="{ row }">
            <div class="heat-bar">
              <div class="heat-dots">
                <span v-for="i in 5" :key="i" :class="['heat-dot', i <= heatLevel(row.heatScore) ? 'fill' : 'empty']"></span>
              </div>
              <span class="heat-score">{{ row.heatScore }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="advisorName" label="所属顾问" width="115" />
        <el-table-column label="下次跟进" width="145">
          <template #default="{ row }">
            <span :style="nextFollowStyle(row.nextFollowTime)">{{ formatDate(row.nextFollowTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDetail(row)">详情</el-button>
            <el-button size="small" link type="warning" @click="openAssign(row)">分配</el-button>
            <el-button size="small" link type="warning" @click="openStatus(row)">状态修改</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @change="fetchData"
        />
      </div>
    </div>

    <el-drawer v-model="drawerVisible" title="客户详情" size="820px" direction="rtl">
      <div v-loading="detailLoading">
        <div v-if="currentCustomer" class="drawer-head">
          <div class="recent-avatar" style="width:44px; height:44px; font-size:16px">{{ currentCustomer.customerName?.[0] }}</div>
          <div class="drawer-title">
            <div class="name">{{ currentCustomer.customerName }}</div>
            <div class="mobile">{{ currentCustomer.mobile }}</div>
            <div class="badges">
              <span :class="['badge', getBadge(CUSTOMER_STATUS, currentCustomer.status)]">{{ getLabel(CUSTOMER_STATUS, currentCustomer.status) }}</span>
              <span :class="['badge', getBadge(INTENT_LEVEL, currentCustomer.intentLevel)]">{{ getLabel(INTENT_LEVEL, currentCustomer.intentLevel) }}</span>
            </div>
          </div>
          <div class="drawer-actions">
            <el-button size="small" type="primary" @click="openAssign(currentCustomer)">分配</el-button>
            <el-dropdown trigger="click" @command="handleMoreAction">
              <el-button size="small">更多操作</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="status">状态流转</el-dropdown-item>
                  <el-dropdown-item command="tags">编辑标签</el-dropdown-item>
                  <el-dropdown-item command="ai">AI助手</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <el-tabs v-model="detailTab">
          <el-tab-pane label="客户画像" name="profile">
            <div class="profile-summary">
              <div class="profile-score">
                <span>{{ customerProfile.heat?.heatScore ?? currentCustomer?.heatScore ?? 0 }}</span>
                <b>热度分</b>
              </div>
              <div class="profile-metrics">
                <div><strong>{{ customerProfile.recentFollows?.length || 0 }}</strong><span>近期跟进</span></div>
                <div><strong>{{ customerProfile.recentTasks?.length || 0 }}</strong><span>近期任务</span></div>
                <div><strong>{{ customerProfile.recentNotes?.length || 0 }}</strong><span>AI纪要</span></div>
                <div><strong>{{ customerProfile.competitorFocuses?.length || 0 }}</strong><span>竞品关注</span></div>
              </div>
            </div>
            <div class="profile-block" v-if="customerProfile.heat">
              <div class="profile-block-title">热度画像</div>
              <div class="detail-row"><span class="detail-label">热度等级</span><span class="detail-value">{{ customerProfile.heat.heatLevel || '—' }}</span></div>
              <div class="detail-row"><span class="detail-label">计算原因</span><span class="detail-value">{{ customerProfile.heat.reason || customerProfile.heat.scoreReason || '—' }}</span></div>
            </div>
            <div class="profile-block">
              <div class="profile-block-title">近期任务</div>
              <el-table :data="customerProfile.recentTasks || []" size="small" empty-text="暂无近期任务">
                <el-table-column label="任务" prop="title" min-width="160" show-overflow-tooltip />
                <el-table-column label="负责人" prop="ownerName" width="90" />
                <el-table-column label="状态" width="90">
                  <template #default="{ row }">{{ getLabel(TASK_STATUS, row.status) }}</template>
                </el-table-column>
                <el-table-column label="日期" width="110" prop="taskDate" />
              </el-table>
            </div>
          </el-tab-pane>
          <el-tab-pane label="基础信息" name="base">
            <div v-for="row in detailRows" :key="row.label" class="detail-row">
              <span class="detail-label">{{ row.label }}</span>
              <span class="detail-value">{{ row.value || '—' }}</span>
            </div>
            <div class="tag-line" v-if="currentCustomer?.tagNames?.length">
              <el-tag v-for="tag in currentCustomer.tagNames" :key="tag" size="small" effect="plain">{{ tag }}</el-tag>
            </div>
            <el-empty v-else description="暂无客户标签" :image-size="72" />
          </el-tab-pane>
          <el-tab-pane label="跟进记录" name="follows">
            <div class="drawer-tab-actions">
              <el-button size="small" type="primary" @click="openFollowCreate">新增跟进</el-button>
            </div>
            <el-timeline v-if="followLogs.length">
              <el-timeline-item v-for="item in followLogs" :key="item.id" :timestamp="formatDate(item.createdAt)">
                <div class="timeline-title">{{ item.userName || '—' }} · {{ item.followType || '跟进' }}</div>
                <div class="timeline-content">{{ item.content || item.summary || '暂无内容' }}</div>
                <div class="timeline-meta" v-if="item.nextFollowTime">下次跟进：{{ formatDate(item.nextFollowTime) }}</div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无跟进记录" :image-size="72" />
          </el-tab-pane>
          <el-tab-pane label="分配记录" name="assign">
            <el-table :data="assignLogs" size="small" empty-text="暂无分配记录">
              <el-table-column label="类型" prop="actionType" width="95" />
              <el-table-column label="原负责人" prop="fromUserId" width="95" />
              <el-table-column label="新负责人" prop="toUserId" width="95" />
              <el-table-column label="备注" prop="remark" min-width="140" show-overflow-tooltip />
              <el-table-column label="时间" width="145">
                <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="AI纪要" name="notes">
            <div class="drawer-tab-actions">
              <el-button size="small" type="primary" @click="openNoteCreate">新增纪要</el-button>
              <el-button size="small" :loading="noteGenerating" :disabled="!followLogs.length" @click="generateNoteFromLatestFollow">AI生成</el-button>
            </div>
            <div v-if="noteLogs.length" class="note-list">
              <div v-for="note in noteLogs" :key="note.id" class="note-item">
                <div class="note-head">
                  <span class="cell-primary">{{ note.title || '客户纪要' }}</span>
                  <span class="cell-muted">{{ formatDate(note.createdAt) }}</span>
                </div>
                <div class="note-content">{{ note.summary || '暂无摘要' }}</div>
                <div class="note-meta" v-if="note.objectionTop3">异议：{{ note.objectionTop3 }}</div>
                <div class="note-meta" v-if="note.nextTopic">下次话题：{{ note.nextTopic }}</div>
                <div class="note-actions">
                  <el-button size="small" link type="primary" @click="openNoteEdit(note)">编辑</el-button>
                  <el-button size="small" link type="danger" @click="deleteNote(note)">删除</el-button>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无 AI 纪要" :image-size="72" />
          </el-tab-pane>
          <el-tab-pane label="竞品历史" name="competitors">
            <div class="drawer-tab-actions">
              <el-button size="small" type="primary" @click="openCompetitorCompare">新增对比</el-button>
            </div>
            <el-table :data="competitorLogs" size="small" empty-text="暂无竞品关注历史">
              <el-table-column label="竞品" prop="competitorName" width="130" show-overflow-tooltip />
              <el-table-column label="关注点" prop="focusContent" min-width="180" show-overflow-tooltip />
              <el-table-column label="记录人" prop="createdByName" width="90" />
              <el-table-column label="时间" width="145">
                <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>

    <el-dialog v-model="aiDialog.visible" title="客户 AI 助手" width="760px">
      <div class="ai-dialog-head" v-if="currentCustomer">
        <div class="recent-avatar">{{ currentCustomer.customerName?.[0] }}</div>
        <div>
          <div class="cell-primary">{{ currentCustomer.customerName }}</div>
          <div class="cell-muted">{{ currentCustomer.mobile }}</div>
        </div>
      </div>
      <el-tabs v-model="aiDialog.tab" @tab-change="handleAiTabChange">
        <el-tab-pane label="跟进话术" name="script">
          <div class="ai-tool">
            <div class="ai-tool-title">跟进话术生成</div>
            <el-form :model="scriptForm" label-width="78px">
              <el-form-item label="场景">
                <el-select v-model="scriptForm.sceneType" style="width:100%">
                  <el-option label="首次联系" value="FIRST_CONTACT" />
                  <el-option label="到访邀约" value="VISIT_INVITE" />
                  <el-option label="价格异议" value="PRICE_OBJECTION" />
                  <el-option label="竞品应对" value="COMPETITOR_RESPONSE" />
                </el-select>
              </el-form-item>
              <el-form-item label="渠道">
                <el-select v-model="scriptForm.channelType" style="width:100%">
                  <el-option label="电话" value="PHONE" />
                  <el-option label="微信" value="WECHAT" />
                  <el-option label="短信" value="SMS" />
                </el-select>
              </el-form-item>
              <el-form-item label="补充">
                <el-input v-model="scriptForm.customPrompt" type="textarea" :rows="2" placeholder="补充客户关注点或本次沟通目标" />
              </el-form-item>
            </el-form>
            <el-button type="primary" size="small" :loading="scriptGenerating" @click="generateScript">生成话术</el-button>
            <div v-if="scriptResult.scriptText" class="ai-result">{{ scriptResult.scriptText }}</div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="线索抽取" name="lead">
          <div class="ai-tool">
            <div class="ai-tool-title">线索抽取</div>
            <div class="lead-import-actions">
              <el-upload
                ref="leadImageUploadRef"
                :auto-upload="false"
                :show-file-list="false"
                accept="image/*"
                :on-change="(file) => handleLeadFileSelect(file, 'WECHAT_SCREENSHOT')"
              >
                <el-button size="small">导入微信聊天截图</el-button>
              </el-upload>
              <el-upload
                ref="leadAudioUploadRef"
                :auto-upload="false"
                :show-file-list="false"
                accept="audio/*"
                :on-change="(file) => handleLeadFileSelect(file, 'AUDIO')"
              >
                <el-button size="small">导入语音/音频</el-button>
              </el-upload>
              <el-upload
                ref="leadExcelUploadRef"
                :auto-upload="false"
                :show-file-list="false"
                accept=".xlsx,.xls,.csv"
                :on-change="(file) => handleLeadFileSelect(file, 'EXCEL')"
              >
                <el-button size="small">导入 Excel</el-button>
              </el-upload>
            </div>
            <div v-if="leadSourceFile" class="lead-source-file">
              <span class="cell-primary cell-ellipsis">{{ leadSourceFile.name }}</span>
              <span class="cell-muted">{{ leadSourceLabel }} · {{ formatFileSize(leadSourceFile.size) }}</span>
              <el-button size="small" link type="danger" @click="clearLeadFile">移除</el-button>
            </div>
            <el-input v-model="leadText" type="textarea" :rows="5" placeholder="粘贴聊天记录或线索文本，AI 会抽取客户姓名、手机号、预算、区域等字段" />
            <div class="ai-actions">
              <el-button size="small" type="primary" :loading="leadExtracting" @click="extractLead">抽取线索</el-button>
              <el-button size="small" :disabled="!leadResult.extractId" :loading="leadConfirming" @click="confirmLead">确认入库</el-button>
            </div>
            <div v-if="leadResult.extractId" class="lead-card">
              <el-form :model="leadForm" label-width="82px" class="lead-form">
                <el-form-item label="客户姓名">
                  <el-input v-model="leadForm.customerName" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="leadForm.mobile" />
                </el-form-item>
                <el-form-item label="性别">
                  <el-select v-model="leadForm.gender" clearable style="width:100%">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                  </el-select>
                </el-form-item>
                <el-form-item label="意向等级">
                  <el-select v-model="leadForm.intentLevel" clearable style="width:100%">
                    <el-option v-for="item in INTENT_LEVEL" :key="item.value" :label="item.label" :value="item.value" />
                  </el-select>
                </el-form-item>
                <el-form-item label="最低预算">
                  <el-input-number v-model="leadForm.budgetMin" :min="0" style="width:100%" />
                </el-form-item>
                <el-form-item label="最高预算">
                  <el-input-number v-model="leadForm.budgetMax" :min="0" style="width:100%" />
                </el-form-item>
                <el-form-item label="关注区域">
                  <el-input v-model="leadForm.region" />
                </el-form-item>
                <el-form-item label="户型需求">
                  <el-input v-model="leadForm.houseType" />
                </el-form-item>
                <el-form-item label="购房用途">
                  <el-input v-model="leadForm.purpose" />
                </el-form-item>
                <el-form-item label="备注" class="lead-form-wide">
                  <el-input v-model="leadForm.remark" type="textarea" :rows="2" />
                </el-form-item>
              </el-form>
              <div class="lead-suggestion" v-if="leadResult.suggestion">{{ leadResult.suggestion }}</div>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="抽取历史" name="records">
          <div class="lead-record-filters">
            <el-select v-model="leadRecordFilters.sourceType" clearable placeholder="来源类型" style="width:150px" @change="handleLeadRecordFilterChange">
              <el-option label="文本" value="TEXT" />
              <el-option label="微信截图" value="WECHAT_SCREENSHOT" />
              <el-option label="音频" value="AUDIO" />
              <el-option label="Excel" value="EXCEL" />
            </el-select>
            <el-select v-model="leadRecordFilters.confirmStatus" clearable placeholder="确认状态" style="width:130px" @change="handleLeadRecordFilterChange">
              <el-option label="待确认" value="PENDING" />
              <el-option label="已确认" value="CONFIRMED" />
            </el-select>
            <el-button size="small" @click="fetchLeadRecords">刷新</el-button>
          </div>
          <el-table :data="leadRecords" size="small" v-loading="leadRecordsLoading" height="320" empty-text="暂无抽取记录">
            <el-table-column label="来源" width="105">
              <template #default="{ row }">{{ sourceTypeLabel(row.sourceType) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <span :class="['badge', row.confirmStatus === 'CONFIRMED' ? 'badge-success' : 'badge-warning']">{{ row.confirmStatus === 'CONFIRMED' ? '已确认' : '待确认' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="客户ID" prop="customerId" width="90" />
            <el-table-column label="原始文本" prop="rawText" min-width="180" show-overflow-tooltip />
            <el-table-column label="时间" width="145">
              <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
            </el-table-column>
          </el-table>
          <div class="table-pagination compact-pagination">
            <el-pagination
              v-model:current-page="leadRecordPage"
              v-model:page-size="leadRecordPageSize"
              :total="leadRecordTotal"
              layout="total, prev, pager, next"
              @change="fetchLeadRecords"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="aiDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="assignDialog.visible" title="客户分配" width="460px">
      <el-form :model="assignForm" label-width="90px">
        <el-form-item label="客户">
          <el-input :model-value="assignDialog.customer?.customerName" disabled />
        </el-form-item>
        <el-form-item label="分配顾问">
          <el-select v-model="assignForm.toUserId" filterable clearable placeholder="选择新的负责人" style="width:100%">
            <el-option v-for="u in users" :key="u.id" :label="displayUser(u)" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assignForm.remark" type="textarea" :rows="3" maxlength="120" show-word-limit placeholder="说明分配原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="assignDialog.saving" @click="submitAssign">确认分配</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statusDialog.visible" title="客户状态流转" width="460px">
      <el-form :model="statusForm" label-width="90px">
        <el-form-item label="客户">
          <el-input :model-value="statusDialog.customer?.customerName" disabled />
        </el-form-item>
        <el-form-item label="客户状态">
          <el-select v-model="statusForm.status" placeholder="选择状态" style="width:100%">
            <el-option v-for="s in CUSTOMER_STATUS" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="statusForm.remark" type="textarea" :rows="3" maxlength="120" show-word-limit placeholder="说明流转原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="statusDialog.saving" @click="submitStatus">保存状态</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="tagDialog.visible" title="编辑客户标签" width="460px">
      <el-select v-model="tagForm.tagIds" multiple filterable placeholder="选择客户标签" style="width:100%">
        <el-option v-for="tag in allTags" :key="tag.id" :label="tag.tagName" :value="tag.id" />
      </el-select>
      <template #footer>
        <el-button @click="tagDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="tagDialog.saving" @click="submitTags">保存标签</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="noteDialog.visible" :title="noteDialog.isEdit ? '编辑客户纪要' : '新增客户纪要'" width="560px">
      <el-form :model="noteForm" label-width="86px">
        <el-form-item label="标题">
          <el-input v-model="noteForm.title" placeholder="例如：到访后复盘" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="noteForm.summary" type="textarea" :rows="4" placeholder="记录客户关注点、沟通结论" />
        </el-form-item>
        <el-form-item label="主要异议">
          <el-input v-model="noteForm.objectionTop3" type="textarea" :rows="2" placeholder="价格、位置、交付等异议" />
        </el-form-item>
        <el-form-item label="下次话题">
          <el-input v-model="noteForm.nextTopic" placeholder="下次沟通建议切入点" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="noteDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="noteDialog.saving" @click="submitNote">保存纪要</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="followDialog.visible" title="新增跟进记录" width="560px">
      <el-form :model="followForm" label-width="90px">
        <el-form-item label="跟进方式">
          <el-select v-model="followForm.followType" style="width:100%">
            <el-option label="电话" value="PHONE" />
            <el-option label="微信" value="WECHAT" />
            <el-option label="到访" value="VISIT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="跟进结果">
          <el-input v-model="followForm.followResult" placeholder="例如：已邀约到访、继续考虑、暂无回应" />
        </el-form-item>
        <el-form-item label="跟进内容">
          <el-input v-model="followForm.content" type="textarea" :rows="4" placeholder="记录本次沟通重点" />
        </el-form-item>
        <el-form-item label="沟通摘要">
          <el-input v-model="followForm.summary" type="textarea" :rows="2" placeholder="可选，便于后续 AI 纪要复盘" />
        </el-form-item>
        <el-form-item label="下次跟进">
          <el-date-picker
            v-model="followForm.nextFollowTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择下次跟进时间"
            style="width:100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="followDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="followDialog.saving" @click="submitFollow">保存跟进</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="competitorDialog.visible" title="竞品对比" width="760px">
      <el-form :model="competitorForm" label-width="90px">
        <el-form-item label="本楼盘">
          <el-select v-model="competitorForm.projectId" filterable placeholder="选择本楼盘" style="width:100%">
            <el-option v-for="p in projects" :key="p.id" :label="p.projectName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="竞品楼盘">
          <el-select v-model="competitorForm.competitorId" filterable placeholder="选择竞品楼盘" style="width:100%">
            <el-option v-for="c in competitors" :key="c.id" :label="c.projectName || c.competitorName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户关注">
          <el-input v-model="competitorForm.customerConcern" type="textarea" :rows="3" placeholder="例如：客户觉得竞品总价低、离地铁近" />
        </el-form-item>
      </el-form>
      <div v-if="competitorCompareResult.compareSummary" class="compare-result">
        <div class="compare-grid">
          <div class="compare-card">
            <div class="compare-name">{{ competitorCompareResult.projectName }}</div>
            <div class="compare-row"><span>均价</span><b>{{ formatPrice(competitorCompareResult.projectAvgPrice) }}</b></div>
            <div class="compare-row"><span>亮点</span><b>{{ competitorCompareResult.projectHighlights || '—' }}</b></div>
            <div class="compare-row"><span>优惠</span><b>{{ competitorCompareResult.projectDiscountInfo || '—' }}</b></div>
            <div class="compare-row"><span>交付</span><b>{{ competitorCompareResult.projectHandoverDate || '—' }}</b></div>
          </div>
          <div class="compare-card">
            <div class="compare-name">{{ competitorCompareResult.competitorName }}</div>
            <div class="compare-row"><span>均价</span><b>{{ formatPrice(competitorCompareResult.competitorAvgPrice) }}</b></div>
            <div class="compare-row"><span>亮点</span><b>{{ competitorCompareResult.competitorHighlights || '—' }}</b></div>
            <div class="compare-row"><span>短板</span><b>{{ competitorCompareResult.competitorWeakness || '—' }}</b></div>
            <div class="compare-row"><span>优惠</span><b>{{ competitorCompareResult.competitorDiscountInfo || '—' }}</b></div>
          </div>
        </div>
        <div class="ai-result">{{ competitorCompareResult.compareSummary }}</div>
      </div>
      <div v-if="competitorAiResult.responseText" class="ai-result">{{ competitorAiResult.responseText }}</div>
      <template #footer>
        <el-button @click="competitorDialog.visible = false">关闭</el-button>
        <el-button :loading="competitorDialog.comparing" @click="submitCompetitorCompare">资料对比</el-button>
        <el-button type="primary" :loading="competitorDialog.aiing" @click="submitCompetitorAi">生成话术</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importDialog.visible" title="客户 Excel 导入" width="860px" class="import-dialog">
      <div class="import-layout">
        <div class="import-panel">
          <div class="import-step">1</div>
          <div class="import-title">选择客户 Excel</div>
          <div class="import-desc">支持 .xlsx / .xls 文件，上传后先预览校验结果，不会立即写入客户库。</div>
          <el-button size="small" :loading="templateDownloading" @click="downloadImportTemplate">下载导入模板</el-button>
          <el-upload
            ref="importUploadRef"
            :auto-upload="false"
            :limit="1"
            accept=".xlsx,.xls"
            :on-change="handleImportFileChange"
            :on-remove="handleImportFileRemove"
          >
            <el-button>选择文件</el-button>
          </el-upload>
          <div v-if="importFile" class="import-file">
            <div class="cell-primary cell-ellipsis">{{ importFile.name }}</div>
            <div class="cell-muted">{{ formatFileSize(importFile.size) }}</div>
          </div>
          <el-button type="primary" :loading="importDialog.previewing" :disabled="!importFile" @click="previewImport">
            上传并预览
          </el-button>
        </div>

        <div class="import-panel">
          <div class="import-step">2</div>
          <div class="import-title">预览结果</div>
          <div class="import-stats">
            <div><strong>{{ importPreview.totalCount ?? 0 }}</strong><span>总行数</span></div>
            <div><strong>{{ importPreview.validCount ?? 0 }}</strong><span>可导入</span></div>
            <div><strong>{{ importPreview.invalidCount ?? 0 }}</strong><span>异常行</span></div>
          </div>
          <el-button
            type="primary"
            :loading="importDialog.confirming"
            :disabled="!importPreview.sourceFileId || !importPreview.validCount"
            @click="confirmImport"
          >
            确认导入
          </el-button>
        </div>
      </div>

      <div class="import-preview-table">
        <el-table :data="importPreview.rows || []" height="320" empty-text="请先上传文件并预览">
          <el-table-column label="行号" prop="rowIndex" width="70" />
          <el-table-column label="校验" width="90">
            <template #default="{ row }">
              <span :class="['badge', row.valid ? 'badge-success' : 'badge-danger']">{{ row.valid ? '通过' : '异常' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="客户姓名" min-width="110" show-overflow-tooltip>
            <template #default="{ row }">{{ row.customer?.customerName || '—' }}</template>
          </el-table-column>
          <el-table-column label="手机号" min-width="120" show-overflow-tooltip>
            <template #default="{ row }">{{ row.customer?.mobile || '—' }}</template>
          </el-table-column>
          <el-table-column label="意向等级" width="100">
            <template #default="{ row }">{{ getLabel(INTENT_LEVEL, row.customer?.intentLevel) || '—' }}</template>
          </el-table-column>
          <el-table-column label="预算" min-width="130" show-overflow-tooltip>
            <template #default="{ row }">
              {{ formatBudget(row.customer) }}
            </template>
          </el-table-column>
          <el-table-column label="错误信息" min-width="220" show-overflow-tooltip>
            <template #default="{ row }">{{ row.errors?.join('；') || '—' }}</template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="importDialog.visible = false">关闭</el-button>
        <el-button
          type="primary"
          :loading="importDialog.confirming"
          :disabled="!importPreview.sourceFileId || !importPreview.validCount"
          @click="confirmImport"
        >
          确认导入 {{ importPreview.validCount || 0 }} 条
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { customerApi } from '@/api/customer'
import { deptApi } from '@/api/dept'
import { userApi } from '@/api/user'
import { projectApi } from '@/api/project'
import { competitorApi } from '@/api/competitor'
import { useAuthStore } from '@/stores/auth'
import { CUSTOMER_STATUS, INTENT_LEVEL, TASK_STATUS, getBadgeClass, getLabel } from '@/constants/dictionary'
import { formatDate as formatDateTime, formatAmount, formatFileSize, formatPrice } from '@/utils/format'
import { cleanPayload } from '@/utils/payload'
import { logBusiness, errorBusiness } from '@/utils/logger'
import { uploadBusinessFile } from '@/utils/upload'

const getBadge = getBadgeClass
const authStore = useAuthStore()
const route = useRoute()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const flatDepts = ref([])
const users = ref([])
const allTags = ref([])
const projects = ref([])
const competitors = ref([])

const drawerVisible = ref(false)
const detailLoading = ref(false)
const detailTab = ref('base')
const currentCustomer = ref(null)
const customerProfile = reactive({
  customer: null,
  recentFollows: [],
  recentTasks: [],
  recentNotes: [],
  competitorFocuses: [],
  assignLogs: [],
  heat: null,
})
const followLogs = ref([])
const assignLogs = ref([])
const noteLogs = ref([])
const competitorLogs = ref([])
const importUploadRef = ref()
const importFile = ref(null)
const noteGenerating = ref(false)
const scriptGenerating = ref(false)
const leadExtracting = ref(false)
const leadConfirming = ref(false)
const templateDownloading = ref(false)
const leadRecordsLoading = ref(false)
const leadRecords = ref([])
const leadRecordTotal = ref(0)
const leadRecordPage = ref(1)
const leadRecordPageSize = ref(10)

const filters = reactive({
  keyword: '', status: '', deptId: null, minHeatScore: null, maxHeatScore: null,
})
const assignDialog = reactive({ visible: false, saving: false, customer: null })
const assignForm = reactive({ toUserId: null, remark: '' })
const statusDialog = reactive({ visible: false, saving: false, customer: null })
const statusForm = reactive({ status: '', remark: '' })
const tagDialog = reactive({ visible: false, saving: false })
const tagForm = reactive({ tagIds: [] })
const noteDialog = reactive({ visible: false, saving: false, isEdit: false, noteId: null })
const noteForm = reactive({ title: '', summary: '', objectionTop3: '', nextTopic: '' })
const competitorDialog = reactive({ visible: false, comparing: false, aiing: false })
const competitorForm = reactive({ projectId: null, competitorId: null, customerConcern: '' })
const competitorCompareResult = reactive({ compareSummary: '' })
const competitorAiResult = reactive({ responseText: '' })
const aiDialog = reactive({ visible: false, tab: 'script' })
const scriptForm = reactive({ sceneType: 'FIRST_CONTACT', channelType: 'PHONE', customPrompt: '' })
const scriptResult = reactive({ scriptText: '', modelName: '', tokenUsage: 0 })
const leadText = ref('')
const leadSourceFile = ref(null)
const leadSourceType = ref('TEXT')
const leadImageUploadRef = ref()
const leadAudioUploadRef = ref()
const leadExcelUploadRef = ref()
const leadResult = reactive({})
const leadForm = reactive({
  customerName: '',
  mobile: '',
  gender: '',
  source: '',
  intentLevel: '',
  budgetMin: null,
  budgetMax: null,
  region: '',
  houseType: '',
  purpose: '',
  remark: '',
})
const importDialog = reactive({ visible: false, previewing: false, confirming: false })
const importPreview = reactive({ sourceFileId: null, totalCount: 0, validCount: 0, invalidCount: 0, rows: [] })
const leadRecordFilters = reactive({ sourceType: '', confirmStatus: '' })
const followDialog = reactive({ visible: false, saving: false })
const followForm = reactive({
  followType: 'PHONE',
  followResult: '',
  content: '',
  summary: '',
  nextFollowTime: '',
})

const operatorId = computed(() => authStore.userInfo?.id || authStore.userInfo?.userId || null)

const detailRows = computed(() => {
  const c = currentCustomer.value
  if (!c) return []
  return [
    { label: '客户来源', value: c.source },
    { label: '性别年龄', value: [c.gender, c.age ? `${c.age}岁` : ''].filter(Boolean).join(' / ') },
    { label: '热度分', value: c.heatScore },
    { label: '预算范围', value: c.budgetMin != null ? `${formatAmount(c.budgetMin)} ~ ${formatAmount(c.budgetMax)}` : null },
    { label: '关注区域', value: c.region },
    { label: '户型需求', value: c.houseType },
    { label: '购房用途', value: c.purpose },
    { label: '所属顾问', value: c.advisorName },
    { label: '销售经理', value: c.managerName },
    { label: '最近跟进', value: formatDate(c.latestFollowTime) },
    { label: '下次跟进', value: formatDate(c.nextFollowTime) },
    { label: '备注', value: c.remark },
  ]
})
const leadSourceLabel = computed(() => {
  const map = {
    TEXT: '文本',
    WECHAT_SCREENSHOT: '微信聊天截图',
    AUDIO: '语音/音频',
    EXCEL: 'Excel',
  }
  return map[leadSourceType.value] || '线索文件'
})

function heatLevel(score) {
  if (!score) return 0
  return Math.min(5, Math.round(score / 20))
}
function formatDate(t) {
  return formatDateTime(t)
}
function nextFollowStyle(t) {
  if (!t) return {}
  const d = dayjs(t)
  if (d.isBefore(dayjs(), 'day')) return { color: 'var(--danger)', fontWeight: 500 }
  if (d.isSame(dayjs(), 'day')) return { color: 'var(--warning)', fontWeight: 500 }
  return { color: 'var(--text-500)', fontSize: '12px' }
}
function displayUser(user) {
  return `${user.nickname || user.username}${user.deptName ? ` / ${user.deptName}` : ''}`
}
function formatBudget(customer) {
  if (!customer || customer.budgetMin == null) return '—'
  return `${formatAmount(customer.budgetMin)} ~ ${formatAmount(customer.budgetMax)}`
}
function sourceTypeLabel(value) {
  const map = {
    TEXT: '文本',
    WECHAT_SCREENSHOT: '微信截图',
    AUDIO: '音频',
    EXCEL: 'Excel',
  }
  return map[value] || value || '—'
}

async function fetchData() {
  loading.value = true
  try {
    const params = { pageNum: currentPage.value, pageSize: pageSize.value, ...filters }
    const data = await customerApi.page(params)
    tableData.value = data?.list || []
    total.value = data?.total || 0
  } finally {
    loading.value = false
  }
}

async function fetchDepts() {
  try {
    const tree = await deptApi.tree()
    flatDepts.value = flattenTree(tree || [])
  } catch { /* non-critical */ }
}

async function fetchUsers() {
  try {
    const data = await userApi.page({ pageNum: 1, pageSize: 200, status: 1 })
    users.value = data?.list || []
  } catch { /* non-critical */ }
}

async function fetchTags() {
  try {
    allTags.value = (await customerApi.tagList()) || []
  } catch { /* non-critical */ }
}

async function fetchProjectOptions() {
  try {
    const [projectData, competitorData] = await Promise.all([
      projectApi.page({ pageNum: 1, pageSize: 200, status: 1 }),
      competitorApi.page({ pageNum: 1, pageSize: 200, status: 1 }),
    ])
    projects.value = projectData?.list || []
    competitors.value = competitorData?.list || []
  } catch { /* non-critical */ }
}

function flattenTree(nodes) {
  return nodes.reduce((acc, n) => {
    acc.push(n)
    if (n.children?.length) acc.push(...flattenTree(n.children))
    return acc
  }, [])
}

function handleSearch() { currentPage.value = 1; fetchData() }
function handleReset() {
  Object.assign(filters, { keyword: '', status: '', deptId: null, minHeatScore: null, maxHeatScore: null })
  handleSearch()
}

async function openDetail(row) {
  currentCustomer.value = row
  drawerVisible.value = true
  detailTab.value = 'profile'
  detailLoading.value = true
  try {
    // 详情聚合复用 app 端成熟接口，避免后台列表字段不足。
    const [detail, profile, follows, logs, notes, competitors] = await Promise.all([
      customerApi.detail(row.id),
      customerApi.profile(row.id).catch(() => null),
      customerApi.follows(row.id).catch(() => []),
      customerApi.assignLogsByCustomer(row.id, { pageNum: 1, pageSize: 20 }).catch(() => ({ list: [] })),
      customerApi.notes(row.id).catch(() => []),
      customerApi.competitorHistory(row.id).catch(() => []),
    ])
    currentCustomer.value = detail || row
    Object.assign(customerProfile, {
      customer: profile?.customer || detail || row,
      recentFollows: profile?.recentFollows || follows || [],
      recentTasks: profile?.recentTasks || [],
      recentNotes: profile?.recentNotes || notes || [],
      competitorFocuses: profile?.competitorFocuses || competitors || [],
      assignLogs: profile?.assignLogs || logs?.list || [],
      heat: profile?.heat || null,
    })
    followLogs.value = follows || []
    assignLogs.value = logs?.list || []
    noteLogs.value = notes || []
    competitorLogs.value = competitors || []
  } finally {
    detailLoading.value = false
  }
}

function openAssign(row) {
  assignDialog.customer = row
  assignDialog.visible = true
  Object.assign(assignForm, { toUserId: row.advisorId || null, remark: '' })
  if (!users.value.length) fetchUsers()
}

function handleMoreAction(command) {
  const actions = {
    status: () => openStatus(currentCustomer.value),
    tags: openTagEdit,
    ai: openAiDialog,
  }
  actions[command]?.()
}

function openAiDialog() {
  aiDialog.visible = true
  aiDialog.tab = 'script'
}

function openLeadDialog() {
  aiDialog.visible = true
  aiDialog.tab = 'lead'
}

function handleAiTabChange(name) {
  if (name === 'records') fetchLeadRecords()
}

async function submitAssign() {
  if (!assignForm.toUserId) return ElMessage.warning('请选择分配顾问')
  assignDialog.saving = true
  try {
    const customer = assignDialog.customer
    const payload = cleanPayload({
      toUserId: assignForm.toUserId,
      managerId: customer?.managerId,
      deptId: customer?.deptId,
      actionType: 'ASSIGN',
      remark: assignForm.remark,
      createdBy: operatorId.value,
    }, ['toUserId', 'managerId', 'deptId', 'actionType', 'remark', 'createdBy'])
    await customerApi.assign(customer.id, payload)
    logBusiness('customer', 'assign:save', payload)
    ElMessage.success('客户分配成功')
    assignDialog.visible = false
    await fetchData()
    if (drawerVisible.value && currentCustomer.value?.id === customer.id) await openDetail(customer)
  } catch (error) {
    errorBusiness('customer', 'assign:failed', error)
  } finally {
    assignDialog.saving = false
  }
}

function openStatus(row) {
  statusDialog.customer = row
  statusDialog.visible = true
  Object.assign(statusForm, { status: row.status || '', remark: '' })
}

async function submitStatus() {
  if (!statusForm.status) return ElMessage.warning('请选择客户状态')
  statusDialog.saving = true
  try {
    const customer = statusDialog.customer
    const payload = cleanPayload({
      status: statusForm.status,
      remark: statusForm.remark,
      operatorId: operatorId.value,
    }, ['status', 'remark', 'operatorId'])
    await customerApi.updateStatus(customer.id, payload)
    logBusiness('customer', 'status:save', payload)
    ElMessage.success('客户状态已更新')
    statusDialog.visible = false
    await fetchData()
    if (drawerVisible.value && currentCustomer.value?.id === customer.id) await openDetail(customer)
  } catch (error) {
    errorBusiness('customer', 'status:failed', error)
  } finally {
    statusDialog.saving = false
  }
}

function openTagEdit() {
  tagDialog.visible = true
  const tagNameSet = new Set(currentCustomer.value?.tagNames || [])
  tagForm.tagIds = allTags.value.filter(tag => tagNameSet.has(tag.tagName)).map(tag => tag.id)
  if (!allTags.value.length) fetchTags()
}

async function submitTags() {
  if (!currentCustomer.value?.id) return
  tagDialog.saving = true
  try {
    await customerApi.updateTags(currentCustomer.value.id, tagForm.tagIds)
    logBusiness('customer', 'tags:update', { customerId: currentCustomer.value.id, tagIds: tagForm.tagIds })
    ElMessage.success('客户标签已更新')
    tagDialog.visible = false
    await openDetail(currentCustomer.value)
  } catch (error) {
    errorBusiness('customer', 'tags:update:failed', error)
  } finally {
    tagDialog.saving = false
  }
}

function resetNoteForm() {
  Object.assign(noteForm, { title: '', summary: '', objectionTop3: '', nextTopic: '' })
  Object.assign(noteDialog, { isEdit: false, noteId: null })
}

function openNoteCreate() {
  resetNoteForm()
  noteDialog.visible = true
}

function openNoteEdit(note) {
  Object.assign(noteDialog, { visible: true, isEdit: true, noteId: note.id })
  Object.assign(noteForm, {
    title: note.title || '',
    summary: note.summary || '',
    objectionTop3: note.objectionTop3 || '',
    nextTopic: note.nextTopic || '',
  })
}

function openFollowCreate() {
  followDialog.visible = true
  Object.assign(followForm, {
    followType: 'PHONE',
    followResult: '',
    content: '',
    summary: '',
    nextFollowTime: '',
  })
}

async function submitFollow() {
  if (!followForm.followType) return ElMessage.warning('请选择跟进方式')
  if (!followForm.followResult) return ElMessage.warning('请填写跟进结果')
  if (!followForm.content) return ElMessage.warning('请填写跟进内容')
  followDialog.saving = true
  try {
    const payload = cleanPayload({
      customerId: currentCustomer.value?.id,
      followType: followForm.followType,
      followResult: followForm.followResult,
      content: followForm.content,
      summary: followForm.summary,
      nextFollowTime: followForm.nextFollowTime,
      userId: operatorId.value,
    }, ['customerId', 'followType', 'followResult', 'content', 'summary', 'nextFollowTime', 'userId'])
    await customerApi.createFollow(payload)
    logBusiness('customer-follow', 'create', payload)
    ElMessage.success('跟进记录已保存')
    followDialog.visible = false
    await openDetail(currentCustomer.value)
    detailTab.value = 'follows'
    await fetchData()
  } catch (error) {
    errorBusiness('customer-follow', 'create:failed', error)
  } finally {
    followDialog.saving = false
  }
}

async function submitNote() {
  if (!noteForm.title && !noteForm.summary) return ElMessage.warning('请填写纪要标题或摘要')
  noteDialog.saving = true
  try {
    const payload = cleanPayload({
      customerId: currentCustomer.value?.id,
      noteType: 'MANUAL',
      title: noteForm.title,
      summary: noteForm.summary,
      objectionTop3: noteForm.objectionTop3,
      nextTopic: noteForm.nextTopic,
      createdBy: operatorId.value,
    }, ['customerId', 'noteType', 'title', 'summary', 'objectionTop3', 'nextTopic', 'createdBy'])
    if (noteDialog.isEdit) {
      await customerApi.updateNote(noteDialog.noteId, payload)
    } else {
      await customerApi.createNote(payload)
    }
    logBusiness('customer-note', noteDialog.isEdit ? 'update' : 'create', payload)
    ElMessage.success('客户纪要已保存')
    noteDialog.visible = false
    await openDetail(currentCustomer.value)
    detailTab.value = 'notes'
  } catch (error) {
    errorBusiness('customer-note', 'save:failed', error)
  } finally {
    noteDialog.saving = false
  }
}

async function deleteNote(note) {
  noteDialog.saving = true
  try {
    await customerApi.deleteNote(note.id)
    logBusiness('customer-note', 'delete', { id: note.id })
    ElMessage.success('客户纪要已删除')
    await openDetail(currentCustomer.value)
    detailTab.value = 'notes'
  } catch (error) {
    errorBusiness('customer-note', 'delete:failed', error)
  } finally {
    noteDialog.saving = false
  }
}

async function generateNoteFromLatestFollow() {
  const latestFollow = followLogs.value[0]
  if (!latestFollow) return ElMessage.warning('暂无可生成纪要的跟进记录')
  noteGenerating.value = true
  try {
    await customerApi.autoGenerateNote({
      customerId: currentCustomer.value.id,
      followId: latestFollow.id,
      createdBy: operatorId.value,
    })
    logBusiness('customer-note', 'ai-generate', { customerId: currentCustomer.value.id, followId: latestFollow.id })
    ElMessage.success('AI纪要已生成')
    await openDetail(currentCustomer.value)
    detailTab.value = 'notes'
  } catch (error) {
    errorBusiness('customer-note', 'ai-generate:failed', error)
  } finally {
    noteGenerating.value = false
  }
}

function openCompetitorCompare() {
  competitorDialog.visible = true
  Object.assign(competitorForm, { projectId: null, competitorId: null, customerConcern: '' })
  Object.assign(competitorCompareResult, { compareSummary: '' })
  Object.assign(competitorAiResult, { responseText: '' })
  if (!projects.value.length || !competitors.value.length) fetchProjectOptions()
}

function buildCompetitorPayload() {
  return cleanPayload({
    customerId: currentCustomer.value?.id,
    projectId: competitorForm.projectId,
    competitorId: competitorForm.competitorId,
    customerConcern: competitorForm.customerConcern,
    createdBy: operatorId.value,
  }, ['customerId', 'projectId', 'competitorId', 'customerConcern', 'createdBy'])
}

function validateCompetitorForm() {
  if (!competitorForm.projectId) return ElMessage.warning('请选择本楼盘')
  if (!competitorForm.competitorId) return ElMessage.warning('请选择竞品楼盘')
  return true
}

async function submitCompetitorCompare() {
  if (!validateCompetitorForm()) return
  competitorDialog.comparing = true
  try {
    const result = await customerApi.compareCompetitor(buildCompetitorPayload())
    Object.assign(competitorCompareResult, result || {})
    logBusiness('customer-competitor', 'compare', result)
  } catch (error) {
    errorBusiness('customer-competitor', 'compare:failed', error)
  } finally {
    competitorDialog.comparing = false
  }
}

async function submitCompetitorAi() {
  if (!validateCompetitorForm()) return
  competitorDialog.aiing = true
  try {
    const result = await customerApi.competitorAiResponse(buildCompetitorPayload())
    Object.assign(competitorAiResult, result || {})
    logBusiness('customer-competitor', 'ai-response', result)
    ElMessage.success('竞品话术已生成')
    await openDetail(currentCustomer.value)
    detailTab.value = 'competitors'
  } catch (error) {
    errorBusiness('customer-competitor', 'ai-response:failed', error)
  } finally {
    competitorDialog.aiing = false
  }
}

async function generateScript() {
  if (!currentCustomer.value?.id) return
  scriptGenerating.value = true
  try {
    const result = await customerApi.generateScript({
      customerId: currentCustomer.value.id,
      sceneType: scriptForm.sceneType,
      channelType: scriptForm.channelType,
      customPrompt: scriptForm.customPrompt,
    })
    Object.assign(scriptResult, result || {})
    logBusiness('customer-ai', 'script:generate', result)
  } catch (error) {
    errorBusiness('customer-ai', 'script:failed', error)
  } finally {
    scriptGenerating.value = false
  }
}

async function extractLead() {
  if (!leadText.value.trim() && !leadSourceFile.value) return ElMessage.warning('请先粘贴线索文本或导入线索文件')
  leadExtracting.value = true
  try {
    let sourceFileId = null
    if (leadSourceFile.value) {
      const uploaded = await uploadBusinessFile({
        file: leadSourceFile.value,
        bizType: 'LEAD_EXTRACT',
        createdBy: operatorId.value,
      })
      sourceFileId = uploaded.id
      logBusiness('customer-ai', 'lead:file-upload', {
        sourceType: leadSourceType.value,
        fileName: leadSourceFile.value.name,
        sourceFileId,
      })
    }
    const result = await customerApi.extractLead({
      sourceType: leadSourceType.value,
      sourceFileId,
      rawText: leadText.value || null,
      createdBy: operatorId.value,
    })
    Object.assign(leadResult, result || {})
    fillLeadForm(result || {})
    logBusiness('customer-ai', 'lead:extract', result)
  } catch (error) {
    errorBusiness('customer-ai', 'lead:extract:failed', error)
  } finally {
    leadExtracting.value = false
  }
}

async function confirmLead() {
  if (!leadResult.extractId) return
  if (!leadForm.customerName) return ElMessage.warning('请补充客户姓名后再入库')
  leadConfirming.value = true
  try {
    const customerForm = cleanPayload({
      customerName: leadForm.customerName,
      mobile: leadForm.mobile,
      gender: leadForm.gender,
      source: leadForm.source || 'AI_EXTRACT',
      intentLevel: leadForm.intentLevel,
      budgetMin: leadForm.budgetMin,
      budgetMax: leadForm.budgetMax,
      region: leadForm.region,
      houseType: leadForm.houseType,
      purpose: leadForm.purpose,
      remark: leadForm.remark,
      createdBy: operatorId.value,
    }, ['customerName', 'mobile', 'gender', 'source', 'intentLevel', 'budgetMin', 'budgetMax', 'region', 'houseType', 'purpose', 'remark', 'createdBy'])
    await customerApi.confirmLead({ extractId: leadResult.extractId, customerForm })
    logBusiness('customer-ai', 'lead:confirm', customerForm)
    ElMessage.success('AI线索已入库')
    Object.keys(leadResult).forEach(key => delete leadResult[key])
    resetLeadForm()
    leadText.value = ''
    clearLeadFile()
    await fetchData()
  } catch (error) {
    errorBusiness('customer-ai', 'lead:confirm:failed', error)
  } finally {
    leadConfirming.value = false
  }
}

function fillLeadForm(result) {
  Object.assign(leadForm, {
    customerName: result.customerName || '',
    mobile: result.mobile || '',
    gender: result.gender || '',
    source: result.source || 'AI_EXTRACT',
    intentLevel: result.intentLevel || '',
    budgetMin: result.budgetMin ?? null,
    budgetMax: result.budgetMax ?? null,
    region: result.region || '',
    houseType: result.houseType || '',
    purpose: result.purpose || '',
    remark: result.remark || '',
  })
}

function resetLeadForm() {
  Object.assign(leadForm, {
    customerName: '',
    mobile: '',
    gender: '',
    source: '',
    intentLevel: '',
    budgetMin: null,
    budgetMax: null,
    region: '',
    houseType: '',
    purpose: '',
    remark: '',
  })
}

function handleLeadFileSelect(file, sourceType) {
  const raw = file.raw
  if (!raw) return
  if (sourceType === 'WECHAT_SCREENSHOT' && !raw.type.startsWith('image/')) {
    clearLeadFile()
    return ElMessage.warning('请上传微信聊天截图图片')
  }
  if (sourceType === 'AUDIO' && !raw.type.startsWith('audio/')) {
    clearLeadFile()
    return ElMessage.warning('请上传音频文件')
  }
  if (sourceType === 'EXCEL' && !/\.(xlsx|xls|csv)$/i.test(raw.name)) {
    clearLeadFile()
    return ElMessage.warning('请上传 .xlsx、.xls 或 .csv 文件')
  }
  Object.keys(leadResult).forEach(key => delete leadResult[key])
  resetLeadForm()
  leadSourceFile.value = raw
  leadSourceType.value = sourceType
  logBusiness('customer-ai', 'lead:file-select', { sourceType, fileName: raw.name, size: raw.size })
}

function clearLeadFile() {
  leadSourceFile.value = null
  leadSourceType.value = 'TEXT'
  leadImageUploadRef.value?.clearFiles()
  leadAudioUploadRef.value?.clearFiles()
  leadExcelUploadRef.value?.clearFiles()
}

function resetImportState() {
  importFile.value = null
  Object.assign(importPreview, { sourceFileId: null, totalCount: 0, validCount: 0, invalidCount: 0, rows: [] })
  importUploadRef.value?.clearFiles()
}

function openImport() {
  importDialog.visible = true
  resetImportState()
}

async function downloadImportTemplate() {
  templateDownloading.value = true
  try {
    const response = await customerApi.downloadExcelTemplate()
    const blob = new Blob([response.data], {
      type: response.headers['content-type'] || 'application/octet-stream',
    })
    const link = document.createElement('a')
    const objectUrl = URL.createObjectURL(blob)
    link.href = objectUrl
    link.download = '客户导入模板.xlsx'
    document.body.appendChild(link)
    link.click()
    link.remove()
    URL.revokeObjectURL(objectUrl)
    logBusiness('customer-import', 'template:download')
  } catch (error) {
    errorBusiness('customer-import', 'template:download:failed', error)
  } finally {
    templateDownloading.value = false
  }
}

function handleImportFileChange(file) {
  const raw = file.raw
  const fileName = raw?.name || file.name || ''
  if (!/\.(xlsx|xls)$/i.test(fileName)) {
    ElMessage.warning('请上传 .xlsx 或 .xls 文件')
    resetImportState()
    return
  }
  importFile.value = raw
  Object.assign(importPreview, { sourceFileId: null, totalCount: 0, validCount: 0, invalidCount: 0, rows: [] })
  logBusiness('customer-import', 'file:select', { name: raw.name, size: raw.size })
}

function handleImportFileRemove() {
  importFile.value = null
  Object.assign(importPreview, { sourceFileId: null, totalCount: 0, validCount: 0, invalidCount: 0, rows: [] })
}

async function previewImport() {
  if (!importFile.value) return ElMessage.warning('请先选择 Excel 文件')
  importDialog.previewing = true
  try {
    const uploaded = await uploadBusinessFile({
      file: importFile.value,
      bizType: 'CUSTOMER_IMPORT',
      createdBy: operatorId.value,
    })
    const preview = await customerApi.previewExcelImport({ sourceFileId: uploaded.id })
    Object.assign(importPreview, {
      sourceFileId: preview?.sourceFileId || uploaded.id,
      totalCount: preview?.totalCount || 0,
      validCount: preview?.validCount || 0,
      invalidCount: preview?.invalidCount || 0,
      rows: preview?.rows || [],
    })
    logBusiness('customer-import', 'preview:success', importPreview)
    ElMessage.success('导入预览已生成')
  } catch (error) {
    errorBusiness('customer-import', 'preview:failed', error)
  } finally {
    importDialog.previewing = false
  }
}

async function confirmImport() {
  if (!importPreview.sourceFileId) return ElMessage.warning('请先完成导入预览')
  importDialog.confirming = true
  try {
    const result = await customerApi.confirmExcelImport({
      sourceFileId: importPreview.sourceFileId,
      createdBy: operatorId.value,
    })
    logBusiness('customer-import', 'confirm:success', result)
    ElMessage.success(`导入完成：成功 ${result?.successCount || 0} 条，失败 ${result?.failedCount || 0} 条`)
    if (result?.failedRows?.length) {
      Object.assign(importPreview, {
        rows: result.failedRows,
        totalCount: result.failedRows.length,
        validCount: 0,
        invalidCount: result.failedRows.length,
      })
    } else {
      importDialog.visible = false
      resetImportState()
    }
    await fetchData()
  } catch (error) {
    errorBusiness('customer-import', 'confirm:failed', error)
  } finally {
    importDialog.confirming = false
  }
}

async function fetchLeadRecords() {
  leadRecordsLoading.value = true
  try {
    const params = cleanPayload({
      sourceType: leadRecordFilters.sourceType,
      confirmStatus: leadRecordFilters.confirmStatus,
      pageNum: leadRecordPage.value,
      pageSize: leadRecordPageSize.value,
    }, ['sourceType', 'confirmStatus', 'pageNum', 'pageSize'])
    const data = await customerApi.leadExtractRecords(params)
    leadRecords.value = data?.list || []
    leadRecordTotal.value = data?.total || 0
  } finally {
    leadRecordsLoading.value = false
  }
}

function handleLeadRecordFilterChange() {
  leadRecordPage.value = 1
  fetchLeadRecords()
}

onMounted(() => {
  fetchData()
  fetchDepts()
  fetchUsers()
  fetchTags()
  fetchProjectOptions()
  if (route.query.quick === 'import') openImport()
  if (route.query.quick === 'ai-lead') openLeadDialog()
})
</script>

<style scoped>
.drawer-head {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-light);
}
.drawer-title { flex: 1; min-width: 0; }
.drawer-title .name { font-size: 16px; font-weight: 600; color: var(--text-900); }
.drawer-title .mobile { font-size: 12px; color: var(--text-400); margin-top: 2px; }
.drawer-title .badges { display: flex; gap: 6px; margin-top: 6px; }
.drawer-actions { display: flex; gap: 8px; }
.tag-line { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 14px; }
.profile-summary {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 12px;
  margin-bottom: 14px;
}
.profile-score {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 16px 12px;
  background: rgba(201,168,76,.08);
  text-align: center;
}
.profile-score span {
  display: block;
  font-size: 32px;
  line-height: 1;
  font-weight: 700;
  color: var(--text-900);
}
.profile-score b {
  display: block;
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-500);
}
.profile-metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}
.profile-metrics div {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 14px 12px;
  background: var(--surface-50);
}
.profile-metrics strong {
  display: block;
  font-size: 22px;
  line-height: 1;
  color: var(--text-900);
}
.profile-metrics span {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-400);
}
.profile-block {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 14px;
  background: var(--white);
  margin-bottom: 14px;
}
.profile-block-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-900);
  margin-bottom: 10px;
}
.ai-dialog-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 12px;
  margin-bottom: 10px;
  border-bottom: 1px solid var(--border-light);
}
.timeline-title { font-size: 13px; font-weight: 600; color: var(--text-700); }
.timeline-content { margin-top: 4px; color: var(--text-700); white-space: pre-wrap; }
.timeline-meta { margin-top: 4px; font-size: 12px; color: var(--text-400); }
.drawer-tab-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-bottom: 12px;
}
.note-list { display: flex; flex-direction: column; gap: 10px; }
.note-item {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 12px;
  background: var(--surface-50);
}
.note-head {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;
}
.note-content { color: var(--text-700); white-space: pre-wrap; }
.note-meta { margin-top: 6px; font-size: 12px; color: var(--text-500); }
.note-actions { margin-top: 8px; display: flex; justify-content: flex-end; gap: 8px; }
.ai-tool {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 14px;
  margin-bottom: 14px;
  background: var(--surface-50);
}
.ai-tool-title { font-size: 13px; font-weight: 600; color: var(--text-900); margin-bottom: 10px; }
.lead-import-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}
.lead-record-filters {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}
.compact-pagination {
  padding: 12px 0 0;
  border-top: none;
}
.lead-source-file {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  margin-bottom: 10px;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  background: var(--white);
}
.ai-actions { margin-top: 10px; display: flex; gap: 8px; }
.ai-result {
  margin-top: 12px;
  padding: 12px;
  border-radius: var(--radius-sm);
  background: var(--white);
  border: 1px solid var(--border-light);
  color: var(--text-700);
  white-space: pre-wrap;
}
.compare-result { margin-top: 12px; }
.compare-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.compare-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 12px;
  background: var(--surface-50);
}
.compare-name { font-size: 14px; font-weight: 600; color: var(--text-900); margin-bottom: 8px; }
.compare-row {
  display: grid;
  grid-template-columns: 54px minmax(0, 1fr);
  gap: 8px;
  padding: 6px 0;
  border-top: 1px solid var(--border-light);
  font-size: 12px;
}
.compare-row span { color: var(--text-400); }
.compare-row b { color: var(--text-700); font-weight: 500; }
.lead-card {
  margin-top: 12px;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  background: var(--white);
  padding: 14px;
}
.lead-form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 12px;
}
.lead-form-wide {
  grid-column: 1 / -1;
}
.lead-suggestion {
  margin: 8px 0;
  padding: 8px 10px;
  border-radius: var(--radius-sm);
  background: rgba(201,168,76,.1);
  color: var(--text-700);
  font-size: 12px;
}
.import-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 14px;
}
.import-panel {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 14px;
  background: var(--surface-50);
}
.import-step {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  background: var(--navy-800);
  color: var(--gold-400);
  font-size: 12px;
  font-weight: 700;
}
.import-title { font-size: 14px; font-weight: 600; color: var(--text-900); }
.import-desc { margin: 4px 0 12px; color: var(--text-500); font-size: 12px; }
.import-file {
  margin: 10px 0;
  padding: 9px 10px;
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  background: var(--white);
}
.import-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin: 12px 0;
}
.import-stats div {
  padding: 10px;
  border-radius: var(--radius-sm);
  background: var(--white);
  border: 1px solid var(--border-light);
}
.import-stats strong {
  display: block;
  font-size: 20px;
  line-height: 1;
  color: var(--text-900);
}
.import-stats span {
  display: block;
  margin-top: 6px;
  font-size: 11px;
  color: var(--text-400);
}
.import-preview-table {
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  overflow: hidden;
}
</style>
