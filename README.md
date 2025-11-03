# Git Commit Helper

一个基于 JavaFX 的 Git 提交助手工具，可以根据代码变更自动生成提交信息。

## 功能特性

- 📝 根据暂存区代码变更，调用大模型生成提交日志
- 🎯 多项目管理，支持管理多个 Git 仓库
- 🤖 支持多种 LLM 提供商（OpenAI、Claude 等）
- ✏️ 可自定义每个项目的 Prompt 模板
- 💾 配置持久化存储

## 环境要求

- JDK 21 或更高版本
- Maven 3.6+

## 项目结构

```
git-commit-helper/
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
~/.git-commit-helper/config.json
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

## 开发

### 编译项目

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 打包

```bash
mvn clean package
```

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
