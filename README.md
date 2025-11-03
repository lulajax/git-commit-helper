# Commit-Pal

一个基于 JavaFX 的 智能 Git 提交信息生成工具。它是一个桌面应用程序，旨在利用 AI 的强大能力，将繁琐的 git commit 信息撰写过程变得智能、高效且充满乐趣。它能自动分析您的代码变更，并结合您为每个项目量身定制的提示词（Prompt），生成高质量、符合规范的提交日志

## 功能特性

- 📝 **智能提交信息**：根据暂存区代码变更，调用大模型生成提交日志
- 📊 **研发周报生成**：选择时间段，自动生成研发周报（新功能✨）
- 🎯 **多项目管理**：支持管理多个 Git 仓库
- 🤖 **多LLM支持**：支持多种 LLM 提供商（OpenAI、Claude 等）
- 🌐 **代理支持**：支持 HTTP 代理（Clash、V2Ray 等）
- ✏️ **自定义Prompt**：可自定义提交信息和周报的 Prompt 模板
- 🎨 **智能 UI**：根据当前 Tab 智能显示对应的提示词配置
- 💾 **配置持久化**：所有配置自动保存

## 环境要求

- JDK 21 或更高版本
- Maven 3.6+

## 项目结构

```
Commit-Pal/
├── lib/                    # JavaFX 依赖库（已包含，无需单独下载）
├── src/
│   ├── main/
│   │   ├── java/          # Java 源代码
│   │   └── resources/     # 资源文件（FXML等）
│   └── test/              # 测试代码
├── pom.xml                # Maven 配置
└── README.md
```

## 启动方式

### 方法 1：使用 Maven 运行（推荐）

```bash
# 设置 JDK 环境（Windows PowerShell）
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# 运行应用
mvn clean javafx:run
```

### 方法 2：在 VSCode 中调试

1. 确保已安装 **Extension Pack for Java**
2. 打开项目
3. 按 `F5` 或点击 "Run and Debug" 面板中的 **"Debug JavaFX Application"**

配置文件已包含在 `.vscode/launch.json` 中，开箱即用。

### 方法 3：在 IntelliJ IDEA 中运行

1. 导入为 Maven 项目
2. 找到 `MainApplication.java`
3. 右键点击 → **"Run 'MainApplication.main()'"** 或 **"Debug"**

## 依赖说明

### JavaFX 依赖（lib 目录）

`lib/` 目录包含了 JavaFX 运行时依赖，包括：
- javafx-base
- javafx-controls
- javafx-fxml
- javafx-graphics

这些依赖已经包含在项目中，无需额外下载。

> **注意**：如果 `lib` 目录为空，运行以下命令复制依赖：
> ```bash
> mvn dependency:copy-dependencies
> Copy-Item -Path "target/dependency/javafx-*.jar" -Destination "lib/"
> ```

## 配置

首次运行时，应用会在用户主目录下创建配置文件：
```
~/.Commit-Pal/config.json
```

配置包括：
- **LLM 提供商设置**（API Key、模型名称、Base URL 等）
- **代理设置**（支持 HTTP 代理）
- **项目列表**及其路径
- **每个项目的自定义 Prompt**

### 代理配置

如果你需要通过代理访问 LLM API（如在中国大陆访问 OpenAI），可以在应用右侧的 "Proxy Settings" 区域配置：

1. 勾选 **"Enable Proxy"** 复选框
2. 输入代理主机地址（例如：`127.0.0.1`）
3. 输入代理端口（例如：`7890`）
4. 点击 **"Save Settings"** 保存

常见代理工具及其默认端口：
- **Clash**: `127.0.0.1:7890`
- **V2Ray**: `127.0.0.1:10809`
- **Shadowsocks**: `127.0.0.1:1080`

## 功能使用指南

### 1. 生成提交信息

1. 在左侧选择一个项目
2. 切换到 **"提交信息"** 标签页
3. 点击 **"Refresh"** 获取暂存区变更
4. 点击 **"Generate"** 生成提交信息
5. 可以手动编辑生成的信息
6. 点击 **"Copy"** 复制或 **"Commit"** 直接提交

### 2. 生成研发周报（新功能✨）

1. 在左侧选择一个项目
2. 切换到 **"研发周报"** 标签页
3. 选择开始和结束日期
4. 点击 **"获取提交日志"** 查看该时间段的提交记录
5. 在右侧的 **"Weekly Report Prompt"** 区域可以自定义周报生成提示词
6. 点击 **"生成周报"** 调用 LLM 生成周报
7. 点击 **"复制周报"** 复制到剪贴板

> **提示**：为避免上下文过长，仅获取提交日志（不含代码变更），这样可以提高生成效率和质量。

#### 周报提示词示例

```
请根据以下 Git 提交日志，生成一份专业的研发周报。

要求：
1. 总结本周主要完成的功能模块
2. 列出技术亮点和创新点
3. 说明解决的关键技术问题
4. 按功能模块分类整理工作内容
5. 使用清晰的 Markdown 格式
6. 保持简洁明了

提交日志：
```

**自定义提示词建议**：
- 可以根据团队规范调整格式要求
- 可以添加特定的统计需求（如提交次数、工作时长等）
- 可以要求突出某些特定类型的工作（如性能优化、Bug 修复等）

## 开发

### 编译项目

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 打包应用程序

创建可分发的 JAR 文件：
```bash
mvn clean package
```

### 生成安装程序（Windows / macOS）

项目支持生成原生安装程序。所有打包相关的脚本和文档都在 `packaging/` 目录中。

#### ⚡ 快速开始

**检查环境**：
```powershell
cd packaging
.\check-env.ps1
```

**Windows 用户**：
```powershell
# 选项 1：便携版（推荐，无需额外工具）
.\build-portable.ps1

# 选项 2：完整安装程序（需要 WiX Toolset）
.\build-windows.ps1
```

**macOS 用户**：
```bash
cd packaging
chmod +x build-mac.sh
./build-mac.sh
```

生成的安装文件位于 `target/installer/` 目录。

#### 📚 完整文档

**查看 [packaging/README.md](packaging/README.md) 获取完整打包指南**，包括：
- 🚀 快速开始（5 分钟上手）
- 📋 环境要求和配置
- 💡 场景示例和最佳实践
- 🔧 故障排除
- 🎨 自定义配置（图标、版本号等）
- 📊 打包选项对比

## 技术栈

- **JavaFX 21** - GUI 框架
- **JGit** - Git 操作
- **Gson** - JSON 处理
- **Hutool** - HTTP 客户端
- **Maven** - 构建工具

## 项目详情

详细的功能需求和设计文档请参考 [项目描述.md](项目描述.md)

## License

MIT License
