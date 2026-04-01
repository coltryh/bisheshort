<template>
  <div class="landing-page">
    <!-- Header 导航栏 -->
    <header class="header">
      <div class="container">
        <div class="header-content">
          <!-- Logo -->
          <div class="logo" @click="scrollToTop">
            <div class="logo-icon">
              <el-icon :size="24"><Link /></el-icon>
            </div>
            <span class="logo-text">LinkAI</span>
          </div>

          <!-- 导航 -->
          <nav class="nav">
            <a href="#home" class="nav-item active">首页</a>
            <a href="#features" class="nav-item">功能</a>
            <a href="#assistant" class="nav-item">AI助手</a>
          </nav>

          <!-- 登录按钮 -->
          <el-button type="primary" class="login-btn" @click="goToLogin">
            登录
          </el-button>
        </div>
      </div>
    </header>

    <!-- Hero 区域 -->
    <section id="home" class="hero">
      <div class="container">
        <!-- Badge -->
        <div class="hero-badge">
          <el-icon><Star /></el-icon>
          <span>AI驱动的智能平台</span>
        </div>

        <!-- 主标题 -->
        <h1 class="hero-title">智能短链接平台</h1>

        <!-- 副标题 -->
        <p class="hero-subtitle">AI驱动的链接管理与分析平台</p>

        <!-- CTA 按钮 -->
        <el-button type="primary" size="large" class="cta-btn" @click="goToLogin">
          立即开始
          <el-icon class="ml-2"><ArrowRight /></el-icon>
        </el-button>

        <!-- 装饰区域 -->
        <div class="hero-decoration">
          <div class="decoration-bg"></div>
          <div class="decoration-card">
            <div class="card-header">
              <div class="traffic-lights">
                <span class="light red"></span>
                <span class="light yellow"></span>
                <span class="light green"></span>
              </div>
              <span class="card-url">linkAI.com</span>
            </div>
            <div class="card-content">
              <div class="progress-bar main"></div>
              <div class="progress-bar half"></div>
              <div class="progress-bar third"></div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features 功能区 -->
    <section id="features" class="features">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">强大功能</h2>
          <p class="section-desc">全方位的链接管理解决方案</p>
        </div>

        <div class="features-grid">
          <div v-for="(feature, index) in features" :key="index" class="feature-card">
            <div :class="['feature-icon', feature.colorClass]">
              <el-icon :size="28"><component :is="feature.icon" /></el-icon>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Process 流程区 -->
    <section class="process">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">简单三步</h2>
          <p class="section-desc">轻松上手，开始您的智能链接之旅</p>
        </div>

        <div class="process-grid">
          <!-- 连接线 -->
          <div class="connection-line"></div>

          <div v-for="(step, index) in steps" :key="index" class="process-item">
            <div class="step-badge">{{ step.step }}</div>
            <div class="step-icon">
              <el-icon :size="40"><component :is="step.icon" /></el-icon>
            </div>
            <h3 class="step-title">{{ step.title }}</h3>
            <p class="step-desc">{{ step.description }}</p>
            <div v-if="index < steps.length - 1" class="arrow">→</div>
          </div>
        </div>
      </div>
    </section>

    <!-- AI Assistant 区域 -->
    <section id="assistant" class="assistant">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">AI 智能助手</h2>
          <p class="section-desc">24/7 为您解答一切问题</p>
        </div>

        <div class="assistant-content">
          <div class="assistant-preview">
            <div class="chat-bubble user">如何设置短链接的有效期？</div>
            <div class="chat-bubble ai">
              您可以在创建短链接时选择有效期类型：
              <br />• 永久有效
              <br />• 定时过期（设置具体日期时间）
              <br />• 点击次数限制
              <br /><br />设置后可在管理后台随时修改。
            </div>
            <div class="chat-bubble user">支持批量创建吗？</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <!-- 品牌信息 -->
          <div class="footer-brand">
            <div class="logo">
              <div class="logo-icon light">
                <el-icon :size="24"><Link /></el-icon>
              </div>
              <span class="logo-text">LinkAI</span>
            </div>
            <p class="brand-desc">
              AI驱动的智能短链接管理平台，让每一个链接都发挥最大价值。
            </p>
            <div class="social-links">
              <a href="#" class="social-link">
                <el-icon><House /></el-icon>
              </a>
              <a href="#" class="social-link">
                <el-icon><ChatLineSquare /></el-icon>
              </a>
              <a href="#" class="social-link">
                <el-icon><LocationInformation /></el-icon>
              </a>
            </div>
          </div>

          <!-- 快速链接 -->
          <div class="footer-links">
            <h4>快速链接</h4>
            <ul>
              <li><a href="#features">产品功能</a></li>
              <li><a href="#">价格方案</a></li>
              <li><a href="#">开发文档</a></li>
              <li><a href="#">博客</a></li>
            </ul>
          </div>

          <!-- 支持 -->
          <div class="footer-links">
            <h4>支持</h4>
            <ul>
              <li><a href="#">帮助中心</a></li>
              <li><a href="#">联系我们</a></li>
              <li><a href="#">隐私政策</a></li>
              <li><a href="#">服务条款</a></li>
            </ul>
          </div>
        </div>

        <!-- 版权 -->
        <div class="copyright">
          <p>© 2026 LinkAI. 保留所有权利。</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Link,
  Link as LinkIcon,
  Star,
  ArrowRight,
  DataLine,
  Compass,
  ChatDotRound,
  User,
  Share,
  House,
  ChatLineSquare,
  LocationInformation
} from '@element-plus/icons-vue'

const router = useRouter()

// 功能卡片数据
const features = [
  {
    icon: LinkIcon,
    title: '短链接生成',
    description: '快速生成简洁易记的短链接，支持自定义链接和批量创建',
    colorClass: 'green'
  },
  {
    icon: DataLine,
    title: '访问统计',
    description: '实时追踪链接点击数据，多维度分析访问来源和用户行为',
    colorClass: 'blue'
  },
  {
    icon: Compass,
    title: 'AI智能分析',
    description: '利用人工智能深度分析链接性能，提供优化建议和预测趋势',
    colorClass: 'purple'
  },
  {
    icon: ChatDotRound,
    title: '智能客服',
    description: '24/7 AI客服助手，随时解答疑问，提供专业的技术支持',
    colorClass: 'orange'
  }
]

// 步骤数据
const steps = [
  {
    icon: User,
    title: '注册账号',
    description: '快速注册，几秒钟即可开始使用',
    step: '01'
  },
  {
    icon: Link,
    title: '创建链接',
    description: '输入原始链接，一键生成短链接',
    step: '02'
  },
  {
    icon: Share,
    title: '分享追踪',
    description: '分享链接并实时追踪访问数据',
    step: '03'
  }
]

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 滚动到顶部
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
/* 通用样式 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Header */
.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid #f0f0f0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #059669, #047857);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo-icon.light {
  background: white;
  color: #059669;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #064e3b;
}

.nav {
  display: flex;
  gap: 32px;
}

.nav-item {
  font-weight: 500;
  color: #059669;
  text-decoration: none;
  transition: color 0.2s;
}

.nav-item:hover {
  color: #10b981;
}

.login-btn {
  background: #059669 !important;
  border-color: #059669 !important;
  border-radius: 9999px;
  padding: 8px 24px;
}

.login-btn:hover {
  background: #047857 !important;
  border-color: #047857 !important;
}

/* Hero */
.hero {
  padding: 80px 0;
  text-align: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #ecfdf5;
  border: 1px solid #a7f3d0;
  border-radius: 9999px;
  color: #065f46;
  font-weight: 500;
  margin-bottom: 32px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: #064e3b;
  margin-bottom: 24px;
}

.hero-subtitle {
  font-size: 20px;
  color: #6b7280;
  margin-bottom: 40px;
}

.cta-btn {
  background: #f97316 !important;
  border-color: #f97316 !important;
  border-radius: 9999px;
  padding: 16px 32px;
  font-size: 18px;
  font-weight: 600;
  box-shadow: 0 10px 15px -3px rgba(249, 115, 22, 0.3);
}

.cta-btn:hover {
  background: #ea580c !important;
  border-color: #ea580c !important;
  box-shadow: 0 20px 25px -5px rgba(249, 115, 22, 0.4);
}

.hero-decoration {
  margin-top: 80px;
  position: relative;
}

.decoration-bg {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 100%;
  background: linear-gradient(to right, #d1fae5, #ecfdf5, #d1fae5);
  border-radius: 24px;
  filter: blur(64px);
  opacity: 0.5;
}

.decoration-card {
  position: relative;
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
  border: 1px solid #f3f4f6;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.traffic-lights {
  display: flex;
  gap: 8px;
}

.light {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.light.red { background: #f87171; }
.light.yellow { background: #fbbf24; }
.light.green { background: #34d399; }

.card-url {
  font-size: 14px;
  color: #9ca3af;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.progress-bar {
  height: 8px;
  background: #e5e7eb;
  border-radius: 9999px;
}

.progress-bar.main {
  width: 75%;
  background: linear-gradient(to right, #059669, #10b981);
}

.progress-bar.half {
  width: 50%;
  background: #e5e7eb;
}

.progress-bar.third {
  width: 66%;
  background: #e5e7eb;
}

/* Features */
.features {
  padding: 80px 0;
  background: linear-gradient(to bottom, white, #ecfdf5);
}

.section-header {
  text-align: center;
  margin-bottom: 64px;
}

.section-title {
  font-size: 36px;
  font-weight: 700;
  color: #064e3b;
  margin-bottom: 16px;
}

.section-desc {
  font-size: 18px;
  color: #6b7280;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 16px;
}

.feature-icon.green {
  background: linear-gradient(135deg, #10b981, #059669);
}

.feature-icon.blue {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.feature-icon.purple {
  background: linear-gradient(135deg, #8b5cf6, #6d28d9);
}

.feature-icon.orange {
  background: linear-gradient(135deg, #f97316, #c2410c);
}

.feature-title {
  font-size: 18px;
  font-weight: 600;
  color: #064e3b;
  margin-bottom: 8px;
}

.feature-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}

/* Process */
.process {
  padding: 80px 0;
}

.process-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 48px;
  position: relative;
}

.connection-line {
  position: absolute;
  top: 60px;
  left: 25%;
  right: 25%;
  height: 2px;
  background: linear-gradient(to right, #6ee7b7, #34d399, #6ee7b7);
}

.process-item {
  text-align: center;
  position: relative;
}

.step-badge {
  position: absolute;
  top: -12px;
  right: calc(50% - 48px);
  width: 48px;
  height: 48px;
  background: #f97316;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 700;
  font-size: 14px;
  box-shadow: 0 10px 15px -3px rgba(249, 115, 22, 0.3);
  z-index: 1;
}

.step-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #059669, #10b981);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 24px;
  box-shadow: 0 10px 15px -3px rgba(5, 150, 105, 0.3);
}

.step-title {
  font-size: 24px;
  font-weight: 700;
  color: #064e3b;
  margin-bottom: 12px;
}

.step-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}

.arrow {
  position: absolute;
  top: 60px;
  right: -24px;
  font-size: 32px;
  color: #6ee7b7;
}

/* Assistant */
.assistant {
  padding: 80px 0;
  background: #ecfdf5;
}

.assistant-content {
  max-width: 600px;
  margin: 0 auto;
}

.chat-bubble {
  padding: 16px 20px;
  border-radius: 16px;
  margin-bottom: 16px;
  line-height: 1.6;
}

.chat-bubble.user {
  background: #059669;
  color: white;
  margin-left: 48px;
  border-bottom-right-radius: 4px;
}

.chat-bubble.ai {
  background: white;
  color: #374151;
  margin-right: 48px;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* Footer */
.footer {
  background: #064e3b;
  color: white;
  padding: 48px 0 24px;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 48px;
  margin-bottom: 32px;
}

.footer-brand .logo {
  margin-bottom: 16px;
}

.footer-brand .logo-text {
  color: white;
}

.brand-desc {
  color: #a7f3d0;
  line-height: 1.6;
  margin-bottom: 16px;
}

.social-links {
  display: flex;
  gap: 16px;
}

.social-link {
  width: 40px;
  height: 40px;
  background: #065f46;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a7f3d0;
  transition: all 0.2s;
}

.social-link:hover {
  background: #047857;
  color: white;
}

.footer-links h4 {
  font-weight: 700;
  margin-bottom: 16px;
}

.footer-links ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 8px;
}

.footer-links a {
  color: #a7f3d0;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: white;
}

.copyright {
  border-top: 1px solid #065f46;
  padding-top: 24px;
  text-align: center;
  color: #6ee7b7;
}

/* 响应式 */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .nav {
    display: none;
  }

  .hero-title {
    font-size: 36px;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .process-grid {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .connection-line {
    display: none;
  }

  .arrow {
    display: none;
  }

  .footer-content {
    grid-template-columns: 1fr;
  }
}
</style>
