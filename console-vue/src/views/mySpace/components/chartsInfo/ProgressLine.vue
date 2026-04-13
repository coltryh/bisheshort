<template>
  <div class="main-box">
    <span v-if="!dataLists || dataLists.length === 0"> 所选日期内没有访问数据 </span>
    <div v-else>
      <div class="flex-box" v-for="(item, index) in computedData" :key="index">
        <div class="flex-item">
          <div>
            <img :src="getUrl(item?.browser, item?.os)" width="25" alt="" />
            <span>{{ item?.browser || item?.os }} {{ item?.percentage }}%</span>
          </div>
          <div>
            <span>{{ item?.cnt }} 次</span>
          </div>
        </div>
        <div>
          <el-progress
            color="#3464e0"
            :text-inside="true"
            :show-text="false"
            :stroke-width="12"
            :percentage="item?.percentage"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import edge from '@/assets/png/edge.png'
import Andriod from '@/assets/png/Andriod.png'
import Chorme from '@/assets/png/Chorme.png'
import firefox from '@/assets/png/firefox.png'
import iOS from '@/assets/png/iOS.png'
import macOS from '@/assets/png/macOS.png'
import other from '@/assets/png/other.png'
import Safair from '@/assets/png/Safair.png'
import WeChat from '@/assets/png/WeChat.png'
import Windows from '@/assets/png/Windows.png'
import linux from '@/assets/png/linux.png'
import opera from '@/assets/png/opera.png'
import IE from '@/assets/png/IE.png'

const props = defineProps({
  dataLists: {
    type: Array
  }
})

// 计算总cnt并计算百分比
const computedData = computed(() => {
  if (!props.dataLists || props.dataLists.length === 0) {
    return []
  }
  const total = props.dataLists.reduce((sum, item) => sum + (item?.cnt || 0), 0)
  if (total === 0) {
    return props.dataLists.map(item => ({ ...item, percentage: 0 }))
  }
  return props.dataLists.map(item => ({
    ...item,
    percentage: Math.round((item?.cnt || 0) * 100 / total)
  }))
})

const getUrl = (img1, img2) => {
  if (img1) {
    img1 = img1.toLowerCase()
  }
  if (img2) {
    img2 = img2.toLowerCase()
  }
  if (img1?.includes('edge') || img2?.includes('edge')) {
    return edge
  } else if (img1?.includes('chrome') || img2?.includes('chrome')) {
    return Chorme
  } else if (img1?.includes('android') || img2?.includes('android')) {
    return Andriod
  } else if (img1?.includes('fire') || img2?.includes('fire')) {
    return firefox
  } else if (img1?.includes('ios') || img2?.includes('ios')) {
    return iOS
  } else if (img1?.includes('mac') || img2?.includes('mac')) {
    return macOS
  } else if (img1?.includes('safari') || img2?.includes('safari')) {
    return Safair
  } else if (img1?.includes('windows') || img2?.includes('windows')) {
    return Windows
  } else if (img1?.includes('opera') || img2?.includes('opera')) {
    return opera
  }
   else if (img1?.includes('internet') || img2?.includes('internet')) {
    return IE
  }
  else if (
    img1?.includes('wechat') ||
    img1?.includes('微信') ||
    img2?.includes('wechat') ||
    img2?.includes('微信')
  ) {
    return WeChat
  } else if (img1?.includes('linux') || img2?.includes('linux')) {
    return linux
  } else {
    return other
  }
}
</script>

<style lang="scss" scoped>
.main-box {
  padding: 20px;

  .flex-box {
    height: 50px;
    display: flex;
    flex-direction: column;

    .flex-item {
      height: 35px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      span {
        font-size: 15px;
      }

      img {
        margin-right: 10px;
      }
    }
  }
}

:deep(.el-progress-bar__innerText) {
  font-size: 12px;
  transform: translateY(-1.5px) !important;
}
</style>
