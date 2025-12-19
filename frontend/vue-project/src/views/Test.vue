<template>
  <div class="container m-4">
    <!-- 第一层选框：一级分类（Guide/Component/Resource） -->
    <div class="mb-4">
      <label class="block mb-2">一级分类</label>
      <el-select
        v-model="selectedLevel1"
        placeholder="请选择一级分类"
        @change="handleLevel1Change"
        class="w-60"
      >
        <el-option
          v-for="item in level1Options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>

    <!-- 第二层选框：二级分类（依赖一级选择） -->
    <div class="mb-4" v-if="level2Options.length">
      <label class="block mb-2">二级分类</label>
      <el-select
        v-model="selectedLevel2"
        placeholder="请选择二级分类"
        @change="handleLevel2Change"
        class="w-60"
        :disabled="!selectedLevel1"
      >
        <el-option
          v-for="item in level2Options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>

    <!-- 第三层选框：三级分类（依赖二级选择） -->
    <div class="mb-4" v-if="level3Options.length">
      <label class="block mb-2">三级分类</label>
      <el-select
        v-model="selectedLevel3"
        placeholder="请选择三级分类"
        class="w-60"
        :disabled="!selectedLevel2"
      >
        <el-option
          v-for="item in level3Options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </div>

    <!-- 提交按钮 -->
    <div class="mb-4">
      <el-button
        type="primary"
        @click="submitData"
        :disabled="!selectedLevel1"
      >
        提交选中数据
      </el-button>
    </div>

    <!-- 选中结果展示 -->
    <div class="mt-4">
      <p>已选：{{ getSelectedLabels() }}</p>
    </div>

    <!-- 顶部提示弹窗（用于展示提交的数据） -->
    <teleport to="body">
      <div
        v-if="showDataToast"
        class="data-toast"
        :style="{ top: toastTop + 'px' }"
      >
        <div class="toast-header">提交的数据</div>
        <div class="toast-content">
          <div><strong>选中值：</strong>{{ selectedFullData.values.join(' / ') }}</div>
          <div><strong>选中标签：</strong>{{ selectedFullData.labels.join(' / ') }}</div>
        </div>
      </div>
    </teleport>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onUnmounted } from 'vue'

// 原始级联数据
const originalOptions = [
  {
    value: 'guide',
    label: 'Guide',
    children: [
      {
        value: 'disciplines',
        label: 'Disciplines',
        children: [
          { value: 'consistency', label: 'Consistency' },
          { value: 'feedback', label: 'Feedback' },
          { value: 'efficiency', label: 'Efficiency' },
          { value: 'controllability', label: 'Controllability' },
        ],
      },
      {
        value: 'navigation',
        label: 'Navigation',
        children: [
          { value: 'side nav', label: 'Side Navigation' },
          { value: 'top nav', label: 'Top Navigation' },
        ],
      },
    ],
  },
  {
    value: 'component',
    label: 'Component',
    children: [
      {
        value: 'basic',
        label: 'Basic',
        children: [
          { value: 'layout', label: 'Layout' },
          { value: 'color', label: 'Color' },
          { value: 'typography', label: 'Typography' },
          { value: 'icon', label: 'Icon' },
          { value: 'button', label: 'Button' },
        ],
      },
      {
        value: 'form',
        label: 'Form',
        children: [
          { value: 'radio', label: 'Radio' },
          { value: 'checkbox', label: 'Checkbox' },
          { value: 'input', label: 'Input' },
          { value: 'input-number', label: 'InputNumber' },
          { value: 'select', label: 'Select' },
          { value: 'cascader', label: 'Cascader' },
          { value: 'switch', label: 'Switch' },
          { value: 'slider', label: 'Slider' },
          { value: 'time-picker', label: 'TimePicker' },
          { value: 'date-picker', label: 'DatePicker' },
          { value: 'datetime-picker', label: 'DateTimePicker' },
          { value: 'upload', label: 'Upload' },
          { value: 'rate', label: 'Rate' },
          { value: 'form', label: 'Form' },
        ],
      },
      {
        value: 'data',
        label: 'Data',
        children: [
          { value: 'table', label: 'Table' },
          { value: 'tag', label: 'Tag' },
          { value: 'progress', label: 'Progress' },
          { value: 'tree', label: 'Tree' },
          { value: 'pagination', label: 'Pagination' },
          { value: 'badge', label: 'Badge' },
        ],
      },
      {
        value: 'notice',
        label: 'Notice',
        children: [
          { value: 'alert', label: 'Alert' },
          { value: 'loading', label: 'Loading' },
          { value: 'message', label: 'Message' },
          { value: 'message-box', label: 'MessageBox' },
          { value: 'notification', label: 'Notification' },
        ],
      },
      {
        value: 'navigation',
        label: 'Navigation',
        children: [
          { value: 'menu', label: 'Menu' },
          { value: 'tabs', label: 'Tabs' },
          { value: 'breadcrumb', label: 'Breadcrumb' },
          { value: 'dropdown', label: 'Dropdown' },
          { value: 'steps', label: 'Steps' },
        ],
      },
      {
        value: 'others',
        label: 'Others',
        children: [
          { value: 'dialog', label: 'Dialog' },
          { value: 'tooltip', label: 'Tooltip' },
          { value: 'popover', label: 'Popover' },
          { value: 'card', label: 'Card' },
          { value: 'carousel', label: 'Carousel' },
          { value: 'collapse', label: 'Collapse' },
        ],
      },
    ],
  },
  {
    value: 'resource',
    label: 'Resource',
    children: [
      { value: 'axure', label: 'Axure Components' },
      { value: 'sketch', label: 'Sketch Templates' },
      { value: 'docs', label: 'Design Documentation' },
    ],
  },
]

// 选中值（三级）
const selectedLevel1 = ref('')
const selectedLevel2 = ref('')
const selectedLevel3 = ref('')

// 顶部提示相关
const showDataToast = ref(false)
const toastTop = ref(-100)
let toastTimer: number | null = null

// 一级选项
const level1Options = computed(() => {
  return originalOptions.map(item => ({
    value: item.value,
    label: item.label
  }))
})

// 二级选项
const level2Options = computed(() => {
  if (!selectedLevel1.value) return []
  const level1Item = originalOptions.find(item => item.value === selectedLevel1.value)
  return level1Item?.children ? level1Item.children.map(item => ({
    value: item.value,
    label: item.label
  })) : []
})

// 三级选项
const level3Options = computed(() => {
  if (!selectedLevel1.value || !selectedLevel2.value) return []
  const level1Item = originalOptions.find(item => item.value === selectedLevel1.value)
  if (!level1Item?.children) return []
  const level2Item = level1Item.children.find(item => item.value === selectedLevel2.value)
  return level2Item?.children ? level2Item.children.map(item => ({
    value: item.value,
    label: item.label
  })) : []
})

// 整合完整选中数据
const selectedFullData = computed(() => {
  return {
    values: [selectedLevel1.value, selectedLevel2.value, selectedLevel3.value].filter(Boolean),
    labels: getSelectedLabels().split(' / ').filter(item => item !== '未选择'),
    structured: {
      level1: {
        value: selectedLevel1.value,
        label: level1Options.value.find(item => item.value === selectedLevel1.value)?.label || ''
      },
      level2: {
        value: selectedLevel2.value,
        label: level2Options.value.find(item => item.value === selectedLevel2.value)?.label || ''
      },
      level3: {
        value: selectedLevel3.value,
        label: level3Options.value.find(item => item.value === selectedLevel3.value)?.label || ''
      }
    }
  }
})

// 监听一级选择变化
const handleLevel1Change = () => {
  selectedLevel2.value = ''
  selectedLevel3.value = ''
}

// 监听二级选择变化
const handleLevel2Change = () => {
  selectedLevel3.value = ''
}

// 获取选中标签
const getSelectedLabels = () => {
  const labels = []
  const level1Label = level1Options.value.find(item => item.value === selectedLevel1.value)?.label
  if (level1Label) labels.push(level1Label)
  const level2Label = level2Options.value.find(item => item.value === selectedLevel2.value)?.label
  if (level2Label) labels.push(level2Label)
  const level3Label = level3Options.value.find(item => item.value === selectedLevel3.value)?.label
  if (level3Label) labels.push(level3Label)
  return labels.join(' / ') || '未选择'
}

// 提交数据
const submitData = () => {
  console.log('提交的完整数据：', selectedFullData.value)
  showDataToast.value = true
  toastTop.value = 20

  if (toastTimer) clearTimeout(toastTimer)
  toastTimer = window.setTimeout(() => {
    toastTop.value = -100
    setTimeout(() => {
      showDataToast.value = false
    }, 300)
  }, 3000)
}

// 组件卸载清除定时器
onUnmounted(() => {
  if (toastTimer) clearTimeout(toastTimer)
})
</script>

<style scoped>
.container {
  max-width: 600px;
}
.block {
  display: block;
}
.mb-2 {
  margin-bottom: 8px;
}
.mb-4 {
  margin-bottom: 16px;
}
.mt-4 {
  margin-top: 16px;
}
.w-60 {
  width: 240px;
}

/* 顶部提示框 */
.data-toast {
  position: fixed;
  left: 50%;
  transform: translateX(-50%);
  background: #409eff;
  color: white;
  padding: 12px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  transition: top 0.3s ease;
  min-width: 300px;
  text-align: center;
}

.toast-header {
  font-weight: bold;
  margin-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  padding-bottom: 4px;
}

.toast-content {
  font-size: 14px;
  line-height: 1.5;
}
</style>