# CoWrite

## 🚀 Project Introduction

CoWrite is a real-time document collaboration platform for technical teams, content organizations and knowledge-based enterprises. It has the use experience of linguaque/Notion and supports:

- Multi-person online editing collaboration
- document structure tree management
- Template-label system
- Fine-grained access control
- Comments/Audit Log

---

## 🎯 Core highlights

- 🧩 Document collaboration model is complete : knowledge base, documents, comments and versions.
- 🔄 Real-time editing capability (WebSocket/OT) reservation
- 🔐 Permission control granularity to document level
- 📦 Template and tag system make documents reusable and organized
- 🔔 Notification Center+Webhook External Integration
- 📜 Operation log audit, ensuring that the content can be traced

---


health endpoint: http://localhost:8080/actuator/health

### Architecture diagram

![Architecture diagram](.doc/img.png)

https://imzbf.github.io/md-editor-v3/zh-CN/api/
### Starting method
```bash
scripts\start.bat
```




### Frontend Configuration
前端直接连接到 Java 后端（默认端口 8080），无需额外的 Node.js 中间层。

```

            ┌───────────────┐
            │ WebSocket 客户端 │
            └───────────────┘
                     │
                 消息发送
                     ▼
         ┌────────────────────┐
         │ Netty ServerHandler │
         └────────────────────┘
                     │
        将消息写入 Redis Stream
                     ▼
        ┌──────────────────────────────┐
        │ Redis Stream（doc:{docId}）   │
        └──────────────────────────────┘
                     │
       被文档消费者线程消费（顺序）
                     ▼
         ┌────────────────────┐
         │ 文档处理执行器线程池 │
         └────────────────────┘
                     │
                执行 OT 引擎
                     │
               广播给其他用户
```