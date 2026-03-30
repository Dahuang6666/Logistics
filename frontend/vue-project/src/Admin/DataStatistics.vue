<template>
  <div class="data-statistics-container">
    <!-- 页头 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">📊 数据统计分析</h1>
        <p class="page-subtitle">实时可视化展示系统核心数据</p>
      </div>
      <button class="refresh-btn" @click="refreshAllCharts">
        <span class="icon">🔄</span> 刷新数据
      </button>
    </div>

    <!-- 图表网格 -->
    <div class="charts-grid">
      <!-- 公告类型分布 -->
      <div class="chart-card">
        <div class="card-header">
          <h3 class="card-title">📢 公告类型分布</h3>
          <span class="card-subtitle">按类型统计</span>
        </div>
        <div ref="announcementChart" class="chart-container"></div>
      </div>

      <!-- 宿舍入住率 -->
      <div class="chart-card">
        <div class="card-header">
          <h3 class="card-title">🏠 各楼宿舍入住率</h3>
          <span class="card-subtitle">容量 vs 已住</span>
        </div>
        <div ref="dormitoryChart" class="chart-container"></div>
      </div>

      <!-- 报修状态分布 -->
      <div class="chart-card">
        <div class="card-header">
          <h3 class="card-title">🔧 报修工单状态</h3>
          <span class="card-subtitle">实时处理情况</span>
        </div>
        <div ref="repairChart" class="chart-container"></div>
      </div>

      <!-- 性别比例 -->
      <div class="chart-card">
        <div class="card-header">
          <h3 class="card-title">👥 用户性别分布</h3>
          <span class="card-subtitle">在校学生统计</span>
        </div>
        <div ref="genderChart" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import {
  getAnnouncementTypeDistribution,
  getDormitoryOccupancy,
  getRepairStatusDistribution,
  getUserGenderDistribution
} from '@/utils/api.js';
import { ElMessage } from 'element-plus';

export default {
  name: 'DataStatistics',
  data() {
    return {
      charts: {
        announcement: null,
        dormitory: null,
        repair: null,
        gender: null
      }
    };
  },
  mounted() {
    this.initAllCharts();
    // 监听窗口大小变化,自动调整图表
    window.addEventListener('resize', this.handleResize);
  },
  beforeUnmount() {
    // 销毁所有图表实例
    Object.values(this.charts).forEach(chart => {
      if (chart) chart.dispose();
    });
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    // 初始化所有图表
    async initAllCharts() {
      await Promise.all([
        this.initAnnouncementChart(),
        this.initDormitoryChart(),
        this.initRepairChart(),
        this.initGenderChart()
      ]);
    },

    // 1. 公告类型分布 (环形图)
    async initAnnouncementChart() {
      try {
        const res = await getAnnouncementTypeDistribution();
        if (res.data.code === 1) {
          const chartData = res.data.data || [];

          this.charts.announcement = echarts.init(this.$refs.announcementChart);
          this.charts.announcement.setOption({
            tooltip: {
              trigger: 'item',
              formatter: '{b}: {c} 条 ({d}%)'
            },
            legend: {
              orient: 'vertical',
              right: '10%',
              top: 'center'
            },
            series: [
              {
                name: '公告类型',
                type: 'pie',
                radius: ['40%', '70%'],
                center: ['35%', '50%'],
                avoidLabelOverlap: false,
                itemStyle: {
                  borderRadius: 10,
                  borderColor: '#fff',
                  borderWidth: 2
                },
                label: {
                  show: false,
                  position: 'center'
                },
                emphasis: {
                  label: {
                    show: true,
                    fontSize: 20,
                    fontWeight: 'bold'
                  }
                },
                labelLine: {
                  show: false
                },
                data: chartData,
                color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272']
              }
            ]
          });
        }
      } catch (error) {
        console.error('加载公告数据失败:', error);
        ElMessage.error('公告数据加载失败');
      }
    },

    // 2. 宿舍入住率 (柱状图)
    async initDormitoryChart() {
      try {
        const res = await getDormitoryOccupancy();
        if (res.data.code === 1) {
          const chartData = res.data.data || [];

          const buildings = chartData.map(item => item.buildingNumber);
          const totalCapacity = chartData.map(item => item.totalCapacity);
          const occupiedBeds = chartData.map(item => item.occupiedBeds);

          this.charts.dormitory = echarts.init(this.$refs.dormitoryChart);
          this.charts.dormitory.setOption({
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
              },
              formatter: (params) => {
                const building = params[0].axisValue;
                const total = params[0].value;
                const occupied = params[1].value;
                const rate = ((occupied / total) * 100).toFixed(1);
                return `${building}<br/>总容量: ${total}<br/>已入住: ${occupied}<br/>入住率: ${rate}%`;
              }
            },
            legend: {
              data: ['总容量', '已入住'],
              top: 10
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              data: buildings,
              axisTick: {
                alignWithLabel: true
              }
            },
            yAxis: {
              type: 'value',
              name: '床位数'
            },
            series: [
              {
                name: '总容量',
                type: 'bar',
                data: totalCapacity,
                itemStyle: {
                  color: '#91cc75'
                }
              },
              {
                name: '已入住',
                type: 'bar',
                data: occupiedBeds,
                itemStyle: {
                  color: '#5470c6'
                }
              }
            ]
          });
        }
      } catch (error) {
        console.error('加载宿舍数据失败:', error);
        ElMessage.error('宿舍数据加载失败');
      }
    },

    // 3. 报修状态分布 (环形图)
    async initRepairChart() {
      try {
        const res = await getRepairStatusDistribution();
        if (res.data.code === 1) {
          const chartData = res.data.data || [];

          this.charts.repair = echarts.init(this.$refs.repairChart);
          this.charts.repair.setOption({
            tooltip: {
              trigger: 'item',
              formatter: '{b}: {c} 条 ({d}%)'
            },
            legend: {
              orient: 'vertical',
              right: '10%',
              top: 'center'
            },
            series: [
              {
                name: '报修状态',
                type: 'pie',
                radius: ['40%', '70%'],
                center: ['35%', '50%'],
                avoidLabelOverlap: false,
                itemStyle: {
                  borderRadius: 10,
                  borderColor: '#fff',
                  borderWidth: 2
                },
                label: {
                  show: false
                },
                emphasis: {
                  label: {
                    show: true,
                    fontSize: 20,
                    fontWeight: 'bold'
                  }
                },
                data: chartData,
                color: ['#ee6666', '#fac858', '#91cc75']
              }
            ]
          });
        }
      } catch (error) {
        console.error('加载报修数据失败:', error);
        ElMessage.error('报修数据加载失败');
      }
    },

    // 4. 性别比例 (饼图)
    async initGenderChart() {
      try {
        const res = await getUserGenderDistribution();
        if (res.data.code === 1) {
          const chartData = res.data.data || [];

          this.charts.gender = echarts.init(this.$refs.genderChart);
          this.charts.gender.setOption({
            tooltip: {
              trigger: 'item',
              formatter: '{b}: {c} 人 ({d}%)'
            },
            legend: {
              orient: 'vertical',
              right: '10%',
              top: 'center'
            },
            series: [
              {
                name: '性别',
                type: 'pie',
                radius: '60%',
                center: ['35%', '50%'],
                data: chartData,
                emphasis: {
                  itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                },
                label: {
                  formatter: '{b}\n{c} 人'
                },
                color: ['#5470c6', '#ee6666']
              }
            ]
          });
        }
      } catch (error) {
        console.error('加载性别数据失败:', error);
        ElMessage.error('性别数据加载失败');
      }
    },

    // 刷新所有图表
    async refreshAllCharts() {
      ElMessage.info('正在刷新数据...');
      await this.initAllCharts();
      ElMessage.success('数据刷新完成');
    },

    // 响应式调整
    handleResize() {
      Object.values(this.charts).forEach(chart => {
        if (chart) chart.resize();
      });
    }
  }
};
</script>

<style scoped>
.data-statistics-container {
  padding: 20px;
  background: #fcfcfc;
  min-height: 100%;
}

/* 页头 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 30px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0;
}

.page-subtitle {
  color: #888;
  font-size: 14px;
  margin-top: 5px;
}

.refresh-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
  font-size: 14px;
}

.refresh-btn:hover {
  background: #2980b9;
  transform: translateY(-1px);
}

.icon {
  margin-right: 5px;
}

/* 图表网格 */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}

/* 图表卡片 */
.chart-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  border: 1px solid #eee;
  transition: all 0.3s;
}

.chart-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.card-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #f8f9fb 0%, #ffffff 100%);
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0 0 5px 0;
}

.card-subtitle {
  font-size: 12px;
  color: #999;
}

.chart-container {
  width: 100%;
  height: 350px;
  padding: 20px;
}
</style>