# 🌟 Agent F - 东方云链智能客服平台 (Smart-CS)

<div align="center">

![Java](https://img.shields.io/badge/Java-17%2B-blue?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square)
![LangChain4j](https://img.shields.io/badge/LangChain4j-RAG-orange?style=flat-square)
![WebSocket](https://img.shields.io/badge/WebSocket-Realtime-brightgreen?style=flat-square)

</div>

> **项目简介：** Agent F 是一款极简且强大的智能客服中枢，通过结合 RAG 向量检索与 WebSocket 实时流，为跨境电商场景极速生成合乎业务标准且富有同理心的高质量回复草稿，实现无缝的人机协同闭环。

---

## 💡 核心特性

- **🚀 零配置一键启动**：无需复杂的数据库安装或 Docker 配置，基于 H2 In-Memory 数据库实现“代码拉下来直接跑”。
- **📚 专属知识库注入 (RAG)**：支持任意工作域内 PDF 文件的上传（如退换货政策），系统秒级切片并构建轻量化增量向量库，随时供大模型调用。
- **⚡ 实时全双工通信**：利用 WebSocket 将模拟订单消息、AI 思考进程与生成的沟通草稿丝滑地实时推送到前端。
- **🤝 共情引擎与智能推断**：调用主流大模型接口（默认通义千问），精准推断买家情绪意图（Intent），并根据内部知识生成极具同理心 (Empathy) 的商务英语回复。
- **🎨 现代高级交互设计**：搭载专有玻璃拟态暗黑风格（Glassmorphism Dark Mode）UI，界面优雅，交互动效细腻流畅，提供绝佳视觉反馈。

## 🛠️ 技术栈

- **后端服务**: Java 17, Spring Boot 3.x, Spring Data JPA, WebSocket
- **大模型及应用层**: LangChain4j (基于内存模型的 Embedding), 阿里云百炼模型 API 
- **数据存储**: H2 Database (原生 JPA 架构，极易横向迁移至 PostgreSQL 等企业级关系库)
- **前端展现**: 纯原生结构 HTML5/CSS3/Vanilla JS (零构建，强设计感表现)

## 🚀 快速启动指南

系统运行唯一的核心依赖是 **Java 17 或以上版本环境**。

### 1. 启动后端及前端静态资源
1. 通过 Git 克隆本项目到本地，或是下载源码压缩包并解压。
2. 打开系统终端，并进入包含后端工程的 `smart-cs` 目录：
   ```bash
   cd 东方云链/smart-cs
   
   # Windows 系统请运行:
   .\mvnw.cmd spring-boot:run
   
   # Mac/Linux 系统请运行:
   ./mvnw spring-boot:run
   ```
3. 耐心等待，直到控制台输出类似于 `Tomcat started on port 8080 (http)` 的字样，代表启动完毕。

### 2. 访问系统
后端启动完成后，直接在浏览器中打开工作台控制面板：
👉 **[http://localhost:8080](http://localhost:8080)**


## 🕹️ Demo 完整体验路径

1. **第 1 步：导入背景知识**
   - 进入主页后，找到界面**右侧边界的“知识库”区域**。
   - 拖拽任意的业务 PDF 文档（全英文为佳，如公司退货指南。如果没有，可拿一份随意的英文说明测试）至虚线框。
   - 界面提示切片完成（RAG chunking）。
   
2. **第 2 步：模拟触发买家消息**
   - 点击左侧主导航栏的显眼蓝色按钮：`模拟收到买家消息`。
   - 前端会立刻通过底层的 WebSocket 绿道接收到系统捏造的买家客诉，并显示在聊天流中。

3. **第 3 步：AI 托管与人工共制**
   - 此时，系统中央提示 `Agent F 正在检索知识库...`，后台正调用 LLM 生成解决方案。
   - 几秒后，高质量的草稿将带着**业务意图标签**直接弹出至你下方的确认框中。你可以随意调整措辞，确认无误后点击发送按钮，即可走完模拟系统的订单回复与流转。

## 📁 目录结构说明

```text
东方云链/
├── smart-cs/                # 核心 Spring Boot 工程根目录
│   ├── pom.xml              # Maven 依赖
│   ├── src/main/java/...    # 业务驱动代码（处理 API/Socket 交互，连接 LLM 及 RAG）
│   └── src/main/resources/  # 资源目录
│       ├── application.properties # 配置文件（内含大模型调用端点设定）
│       └── static/          # 项目包含的免编译纯原生玻璃态前台（HTML/CSS/JS）
├── walkthrough.md           # 架构设计详情与开发历程记录
└── README.md                # 当前说明文档
```

## ⚠️ 架构进阶说明
我们为了能够在评委的机器上无需配置即刻演示，剥离了繁重的中间件（如 Redis、RedisSearch 或 PG-Vector），同时也将前端做了内嵌静态化。
但内核代码具有**高强度的真实业务解耦模式**；若需要进行商业落地：
1. **数据层**：只需调整 `application.properties` 配置，`H2` 可立即切换至真实的 `MySQL` 或 `PostgreSQL`。
2. **向量层**：LangChain4j 可在一行代码内将 In-Memory 引擎替换至支持海量的商业图/向量数据库之中。
