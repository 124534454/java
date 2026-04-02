<template>
  <div style="padding: 20px; background: #f5f5f5; min-height: 100vh;">
    <div style="max-width: 1200px; margin: 0 auto; background: white; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.1);">

      <h2 style="margin-bottom: 20px; text-align: center;">AI 助手对话</h2>

      <div ref="msgContainer" style="height: 600px; overflow-y: auto; padding: 15px; border: 1px solid #eee; border-radius: 8px; margin-bottom: 15px; background: #fafafa;">
        <div v-for="(item, index) in messages" :key="index" style="margin-bottom: 15px;">
          <div v-if="item.type === 'user'" style="text-align: right;">
            <div style="background: #409EFF; color: white; padding: 10px 16px; border-radius: 16px 16px 4px 16px; display: inline-block; max-width: 70%;">
              {{ item.content }}
            </div>
          </div>
          <div v-else style="text-align: left;">
            <div style="background: #e9e9eb; color: #333; padding: 10px 16px; border-radius: 16px 16px 16px 4px; display: inline-block; max-width: 70%;">
              {{ item.content }}
            </div>
          </div>
        </div>
        <div v-if="loading" style="text-align: center; color: #999;">AI 正在思考中...</div>
      </div>

      <!-- 输入框 + 清空按钮 -->
      <div style="display: flex; gap: 10px;">
        <el-input
            v-model="userInput"
            placeholder="请输入你想对 AI 说的话..."
            @keyup.enter.native="sendMessage"
        />
        <el-button type="primary" @click="sendMessage" :loading="loading">发送</el-button>
        <!-- 清空按钮 -->
        <el-button type="default" @click="clearChat">清空内容</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "AIClient",
  data() {
    return {
      messages: [{ type: "ai", content: "你好！我是 AI 助手，有什么可以帮你的？" }],
      userInput: "",
      loading: false
    };
  },
  methods: {
    // 发送消息
    async sendMessage() {
      if (!this.userInput.trim()) return;

      const userMsg = this.userInput.trim();
      this.messages.push({ type: "user", content: userMsg });
      this.userInput = "";
      this.loading = true;

      try {
        const res = await fetch("http://localhost:9090/ai/aichat?message=" + encodeURIComponent(userMsg));
        const text = await res.text();

        this.messages.push({
          type: "ai",
          content: text
        });
      } catch (err) {
        this.messages.push({
          type: "ai",
          content: "请求失败：" + err.message
        });
      } finally {
        this.loading = false;
      }
    },


    clearChat() {
      this.messages = [{ type: "ai", content: "你好！我是 AI 助手，有什么可以帮你的？" }];
    }
  },
  watch: {
    messages() {
      this.$nextTick(() => {
        const dom = this.$refs.msgContainer;
        dom.scrollTop = dom.scrollHeight;
      });
    }
  }
};
</script>

<style>
.el-input__wrapper {
  box-shadow: none;
  border: 1px solid #eee !important;
}
</style>