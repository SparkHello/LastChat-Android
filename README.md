# LastChat

一个开源 Android AI 聊天助手应用，灵感来自 [LastChat](https://github.com/Cocolalilal/LastChat) 项目。

## 功能

- **多 LLM 提供商支持** - OpenAI, Anthropic, Google Gemini, Ollama, 自定义 API
- **流式响应** - 实时打字机效果输出
- **Markdown 渲染** - 代码高亮、标题、列表、引用
- **Material You 3 主题** - 动态色彩、浅色/深色模式
- **助手管理** - 创建自定义 AI 助手和角色
- **对话历史** - 本地存储，支持搜索
- **本地优先** - 所有数据保存在设备本地

## 使用方式

### 方式一：下载预编译 APK（推荐）

1. 进入本仓库的 [Releases](../../releases) 页面
2. 下载最新的 `app-debug.apk`
3. 在 Android 设置中允许"安装未知来源应用"
4. 安装并打开
5. 在"设置" > "AI Providers"中添加你的 API Key

### 方式二：自行编译

```bash
git clone <本仓库地址>
cd LastChat
./gradlew assembleDebug
```

APK 将生成在 `app/build/outputs/apk/debug/app-debug.apk`

## 技术栈

- Kotlin + Jetpack Compose
- Material Design 3 (Material You)
- Room 数据库
- Retrofit + OkHttp
- Koin 依赖注入

## 许可证

AGPL-3.0
