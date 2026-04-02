<template>
  <div style="padding: 20px; background: #f5f5f5; min-height: calc(100vh - 280px);">
    <div style="max-width: 1000px; margin: 0 auto; background: white; border-radius: 12px; padding: 25px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);">

      <h2 style="text-align: center; margin-bottom: 25px; color: #333;">📅 考勤管理</h2>

      <!-- 操作按钮 -->
      <div style="display: flex; justify-content: center; gap: 15px; margin-bottom: 30px;">
        <el-button type="primary">✅ 今日签到</el-button>
        <el-button type="success">🏠 今日签退</el-button>
        <el-button type="info">🔄 刷新记录</el-button>
      </div>

      <!-- 考勤记录表格 -->
      <el-table :data="[]" border stripe style="width: 100%;">
        <el-table-column label="序号" type="index" width="70" align="center" />
        <el-table-column label="用户名" align="center" />
        <el-table-column label="签到时间" align="center" />
        <el-table-column label="签退时间" align="center" />
        <el-table-column label="状态" align="center" />
      </el-table>

    </div>
  </div>
</template>

<script>
export default {
  name: "Attendance"
}
</script>