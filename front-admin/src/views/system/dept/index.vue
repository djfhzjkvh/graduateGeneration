<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">部门管理</h1>
      <p class="page-desc">组织架构树形展示（当前为只读模式，MVP 阶段暂不支持新增/编辑）</p>
    </div>

    <div class="dept-layout">
      <!-- Tree -->
      <div class="tree-card" v-loading="loading">
        <div class="tree-card-header">组织架构</div>
        <div class="tree-card-body">
          <el-tree
            :data="treeData"
            :props="treeProps"
            node-key="id"
            default-expand-all
            highlight-current
            @node-click="handleNodeClick"
          >
            <template #default="{ node }">
              <span style="font-size:13.5px">
                {{ node.level === 1 ? '🏢' : node.isLeaf ? '📄' : '📁' }} {{ node.label }}
              </span>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- Detail -->
      <div class="detail-card" v-if="selected">
        <div class="detail-card-header">部门详情</div>
        <div class="detail-card-body">
          <div class="detail-row">
            <span class="detail-label">部门名称</span>
            <span class="detail-value" style="font-weight:500">{{ selected.deptName }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">上级部门</span>
            <span class="detail-value">{{ parentName || '—' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">负责人</span>
            <span class="detail-value">{{ selected.leaderName || '—' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">子部门数</span>
            <span class="detail-value">{{ selected.children?.length || 0 }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">状态</span>
            <span class="detail-value">
              <span :class="['badge', selected.status === 1 ? 'badge-success' : 'badge-danger']">
                {{ selected.status === 1 ? '启用' : '禁用' }}
              </span>
            </span>
          </div>
          <div style="margin-top:16px; padding:10px 12px; background:var(--surface-100); border-radius:var(--radius-sm); font-size:12px; color:var(--text-400)">
            部门新增/编辑功能待后端接口补充后接入
          </div>
        </div>
      </div>
      <div class="detail-card" v-else>
        <div class="detail-card-body" style="padding:40px; text-align:center; color:var(--text-400); font-size:13px">
          点击左侧部门节点查看详情
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { deptApi } from '@/api/dept'

const loading = ref(false)
const treeData = ref([])
const selected = ref(null)
const allNodes = ref([])
const treeProps = { label: 'deptName', children: 'children' }

const parentName = computed(() => {
  if (!selected.value?.parentId) return null
  return allNodes.value.find(n => n.id === selected.value.parentId)?.deptName
})

function flattenTree(nodes) {
  return nodes.reduce((a, n) => { a.push(n); if (n.children?.length) a.push(...flattenTree(n.children)); return a }, [])
}

async function fetchData() {
  loading.value = true
  try {
    treeData.value = await deptApi.tree() || []
    allNodes.value = flattenTree(treeData.value)
    if (treeData.value.length) selected.value = treeData.value[0]
  } finally {
    loading.value = false
  }
}

function handleNodeClick(node) { selected.value = node }

onMounted(fetchData)
</script>
