# Git Commit Helper - 打包指南

本目录包含所有打包相关的脚本和文档，帮助你将项目打包成 Windows 和 macOS 的安装程序。

## 📁 目录结构

```
packaging/
├── README.md              # 本文档（完整打包指南）
├── ICONS.md              # 图标制作指南
├── check-env.ps1         # 环境检测脚本
├── build-windows.ps1     # Windows 安装程序打包
├── build-windows.bat     # Windows 批处理版本
├── build-mac.sh          # macOS 安装程序打包
├── build-portable.ps1    # 便携版打包（推荐新手）
└── build.ps1             # 跨平台自动检测打包
```

## 🚀 快速开始

### 5 分钟上手

#### 1. 检查环境

```powershell
cd packaging
.\check-env.ps1
```

#### 2. 选择打包方式

**Windows - 便携版（最简单，推荐）**：
```powershell
.\build-portable.ps1
```
- ✅ 无需安装 WiX Toolset
- ✅ 解压即用
- ✅ 3-5 分钟完成

**Windows - 专业安装程序**：
```powershell
# 1. 安装 WiX Toolset
choco install wixtoolset

# 2. 打包
.\build-windows.ps1
```

**macOS**：
```bash
chmod +x build-mac.sh
./build-mac.sh
```

#### 3. 获取结果

打包完成后，查看 `../target/installer/` 目录：
- Windows EXE: `CommitPal-1.0.0.exe`
- Windows 便携版: `CommitPal/` 文件夹
- macOS DMG: `CommitPal-1.0.0.dmg`

---

## 📋 目录

1. [前置要求](#前置要求)
2. [快速决策](#快速决策)
3. [打包方法](#打包方法)
4. [环境检测](#环境检测)
5. [场景示例](#场景示例)
6. [打包选项对比](#打包选项对比)
7. [自定义配置](#自定义配置)
8. [故障排除](#故障排除)
9. [最佳实践](#最佳实践)
10. [高级主题](#高级主题)

---

## 前置要求

### 必需工具

1. **Java 21+**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

### 平台特定要求

#### Windows
- **创建 EXE/MSI 安装程序**：需要 [WiX Toolset](https://wixtoolset.org/) 3.11+
- **创建便携版**：无需额外工具（推荐新手）

**安装 WiX Toolset**：
```powershell
# 方法 1: Chocolatey（推荐）
choco install wixtoolset

# 方法 2: Winget
winget install WiXToolset.WiXToolset

# 方法 3: 手动下载
# 访问 https://github.com/wixtoolset/wix3/releases
```

验证安装：
```powershell
candle.exe -?
```

#### macOS
无需额外工具，jpackage 已包含在 JDK 中。

#### Linux
- **DEB 包**：无需额外工具
- **RPM 包**：需要 rpmbuild

---

## 快速决策

### 我应该使用哪种打包方式？

```
┌─ 你在什么平台上？
│
├─ Windows
│  │
│  ├─ 想要专业的安装程序（推荐分发给用户）
│  │  → 需要安装 WiX Toolset → 生成 .exe 安装程序
│  │  命令: .\build-windows.ps1
│  │
│  └─ 想要简单快速（推荐开发测试）
│     → 无需额外工具 → 生成便携版
│     命令: .\build-portable.ps1
│
├─ macOS
│  → 生成 .dmg 安装程序
│  命令: ./build-mac.sh
│
└─ Linux
   → 生成 .deb 或 .rpm 包
   命令: cd .. && mvn clean package && mvn jpackage:jpackage -Djpackage.type=deb
```

---

## 打包方法

### Windows 平台

#### 方案 A：专业安装程序（需要 WiX Toolset）

**适合场景**：
- ✅ 正式发布给最终用户
- ✅ 需要开始菜单和桌面快捷方式
- ✅ 需要标准的安装/卸载流程

**步骤**：
```powershell
# 1. 进入 packaging 目录
cd packaging

# 2. 使用脚本打包
.\build-windows.ps1

# 或手动执行
cd ..
mvn clean package
mvn jpackage:jpackage -Djpackage.type=exe
```

**输出**：`target\installer\CommitPal-1.0.0.exe`

**特性**：
- 自动创建开始菜单项
- 自动创建桌面快捷方式
- 标准的 Windows 安装向导
- 完整的卸载功能

#### 方案 B：便携版（推荐，无需 WiX）

**适合场景**：
- ✅ 快速测试和开发
- ✅ 个人使用
- ✅ U 盘或云盘分发
- ✅ 不想安装 WiX Toolset

**步骤**：
```powershell
cd packaging
.\build-portable.ps1
```

**输出**：`target\installer\CommitPal\` 文件夹

**使用方法**：
1. 复制整个 `CommitPal` 文件夹到任意位置
2. 运行 `CommitPal\CommitPal.exe`

**优点**：
- 无需安装，解压即用
- 不需要 WiX Toolset
- 包含完整 Java 运行时
- 可以放在 U 盘随身携带

#### 方案 C：MSI 安装包（企业部署）

```powershell
cd ..
mvn clean package
mvn jpackage:jpackage -Djpackage.type=msi
```

### macOS 平台

#### DMG 镜像（推荐）

```bash
cd packaging
chmod +x build-mac.sh
./build-mac.sh
```

**输出**：`target/installer/CommitPal-1.0.0.dmg`

**安装方法**：
1. 双击 .dmg 文件
2. 拖拽应用到 Applications 文件夹
3. 从启动台或 Applications 运行

#### PKG 安装包

```bash
cd ..
mvn clean package
mvn jpackage:jpackage -Djpackage.type=pkg
```

### Linux 平台

#### DEB 包（Debian/Ubuntu）

```bash
cd ..
mvn clean package
mvn jpackage:jpackage -Djpackage.type=deb
```

#### RPM 包（RedHat/Fedora）

```bash
mvn jpackage:jpackage -Djpackage.type=rpm
```

---

## 环境检测

在打包前，运行环境检测脚本检查所有依赖：

```powershell
cd packaging
.\check-env.ps1
```

**检测内容**：
- ✅ Java 版本（需要 JDK 21+）
- ✅ Maven 版本（需要 3.6+）
- ✅ WiX Toolset（Windows，如果要生成 EXE）
- ✅ 项目结构完整性
- ✅ 图标文件（可选）

---

## 场景示例

### 场景 1：第一次打包（Windows）

**目标**：快速生成一个可运行的应用

```powershell
# 1. 检查环境
cd packaging
.\check-env.ps1

# 2. 如果没有 WiX，使用便携版
.\build-portable.ps1

# 3. 等待打包完成（首次约 5-10 分钟）

# 4. 测试运行
cd ..\target\installer\CommitPal
.\CommitPal.exe

# 5. 如果需要分发，创建 ZIP
cd ..\..
Compress-Archive -Path "target\installer\CommitPal" `
                 -DestinationPath "CommitPal-portable.zip"
```

### 场景 2：制作专业安装程序

**目标**：创建可分发的 EXE 安装程序

```powershell
# 1. 安装 WiX Toolset
choco install wixtoolset

# 2. 重启 PowerShell 使 PATH 生效

# 3. 验证安装
candle.exe -?

# 4. 打包
cd packaging
.\build-windows.ps1

# 5. 测试安装程序
cd ..\target\installer
.\CommitPal-1.0.0.exe
```

### 场景 3：在 macOS 上打包

```bash
# 1. 给脚本执行权限
cd packaging
chmod +x build-mac.sh

# 2. 运行打包
./build-mac.sh

# 3. 测试 DMG
open ../target/installer/CommitPal-1.0.0.dmg

# 4. 拖拽到 Applications 安装
# 5. 从 Applications 运行测试
open /Applications/CommitPal.app
```

### 场景 4：添加自定义图标

```powershell
# 1. 准备图标文件（参考 ICONS.md）
# 使用在线工具: https://cloudconvert.com/png-to-ico

# 2. 创建资源目录
mkdir ..\src\main\resources -Force

# 3. 复制图标文件
Copy-Item "your-icon.ico" "..\src\main\resources\icon.ico"

# 4. 编辑 pom.xml，取消注释图标配置
# 找到: <!-- <icon>${project.basedir}/src/main/resources/icon.ico</icon> -->
# 改为: <icon>${project.basedir}/src/main/resources/icon.ico</icon>

# 5. 重新打包
.\build-windows.ps1
```

---

## 打包选项对比

| 类型 | 命令 | 输出 | 需要工具 | 大小 | 适用场景 |
|------|------|------|----------|------|----------|
| **Windows EXE** | `.\build-windows.ps1` | .exe 安装程序 | WiX Toolset | ~200MB | 正式发布 |
| **Windows MSI** | `mvn jpackage:jpackage -Djpackage.type=msi` | .msi 安装包 | WiX Toolset | ~200MB | 企业部署 |
| **Windows 便携** | `.\build-portable.ps1` | 文件夹 | 无 | ~200MB | 快速测试 |
| **macOS DMG** | `./build-mac.sh` | .dmg 镜像 | 无 | ~200MB | Mac 用户 |
| **macOS PKG** | `mvn jpackage:jpackage -Djpackage.type=pkg` | .pkg 安装包 | 无 | ~200MB | Mac 安装器 |
| **Linux DEB** | `mvn jpackage:jpackage -Djpackage.type=deb` | .deb 包 | 无 | ~200MB | Debian/Ubuntu |
| **Linux RPM** | `mvn jpackage:jpackage -Djpackage.type=rpm` | .rpm 包 | rpmbuild | ~200MB | RedHat/Fedora |

**注意**：所有安装包都包含完整的 Java 运行时，用户无需单独安装 Java。

### 性能对比

| 方式 | 编译时间 | 打包时间 | 总时间 | 文件大小 |
|------|----------|----------|--------|----------|
| **开发运行** | 30秒 | - | 30秒 | - |
| **便携版** | 30秒 | 3分钟 | 3.5分钟 | 200MB |
| **EXE 安装器** | 30秒 | 5分钟 | 5.5分钟 | 200MB |
| **ZIP 压缩** | - | 1分钟 | - | 70MB |

*测试环境：i7-8700K, 16GB RAM, SSD*

---

## 自定义配置

### 修改应用信息

编辑 `../pom.xml`：

```xml
<!-- 修改版本号 -->
<version>2.0.0</version>

<!-- 修改应用名称（在 jpackage-maven-plugin 配置中） -->
<name>你的应用名称</name>

<!-- 修改供应商 -->
<vendor>你的名字或公司</vendor>

<!-- 修改描述 -->
<description>你的应用描述</description>
```

### 添加应用图标

详细步骤请查看 [ICONS.md](ICONS.md)。

**快速步骤**：
1. 准备图标文件（.ico/.icns/.png）
2. 放到 `src/main/resources/`
3. 在 `pom.xml` 中配置图标路径

### Windows 特定配置

在 `pom.xml` 的 jpackage-maven-plugin 配置中：

```xml
<winMenu>true</winMenu>              <!-- 创建开始菜单项 -->
<winShortcut>true</winShortcut>      <!-- 创建桌面快捷方式 -->
<winDirChooser>true</winDirChooser>  <!-- 允许选择安装目录 -->
<winMenuGroup>你的菜单组</winMenuGroup>
```

### macOS 特定配置

```xml
<macPackageName>你的应用名</macPackageName>
<macPackageIdentifier>com.your.app</macPackageIdentifier>
```

---

## 故障排除

### 常见问题

#### Q1: 打包失败，提示找不到 WiX

**解决方案**：
- **选项 1**：安装 WiX Toolset
  ```powershell
  choco install wixtoolset
  ```
- **选项 2**：使用便携版（无需 WiX）
  ```powershell
  .\build-portable.ps1
  ```

#### Q2: 打包很慢，正常吗？

**是的**，首次打包需要：
- 下载 Maven 依赖：1-2 分钟
- 编译代码：30秒
- 创建自定义 JRE：2-3 分钟
- 打包应用：2-3 分钟
- **总计**：5-10 分钟

后续打包会快很多（约 1-2 分钟）。

#### Q3: 打包后文件很大？

**这是正常的**，因为包含了完整的 Java 运行时：
- 未压缩：约 200MB
- 压缩后：约 70MB

**优点**：用户无需单独安装 Java 即可运行。

#### Q4: 打包后应用无法启动

**排查步骤**：
```powershell
# 1. 检查日志
cd %APPDATA%\CommitPal\logs

# 2. 命令行运行查看错误
cd "C:\Program Files\CommitPal"
.\CommitPal.exe
```

**常见原因和解决方案**：
- JavaFX 模块未正确配置
- 确保 `pom.xml` 中有：
  ```xml
  <javaOptions>
    <option>--add-opens</option>
    <option>javafx.graphics/com.sun.javafx.application=ALL-UNNAMED</option>
  </javaOptions>
  ```

#### Q5: 图标不显示

**检查清单**：
1. 图标文件是否存在：`src\main\resources\icon.ico`
2. `pom.xml` 图标配置是否正确
3. 图标格式是否正确（.ico 应包含多个尺寸）

**重新生成图标**：
```powershell
# 使用 ImageMagick
magick convert icon.png -define icon:auto-resize=256,128,64,48,32,16 icon.ico
```

#### Q6: 可以跨平台打包吗？

**不可以**，必须：
- 在 Windows 上打包 Windows 版本
- 在 macOS 上打包 macOS 版本
- 在 Linux 上打包 Linux 版本

如需跨平台构建，考虑使用 GitHub Actions 或其他 CI/CD 服务。

#### Q7: Windows: "WiX Toolset not found"

**解决方案**：
- 下载并安装 [WiX Toolset](https://github.com/wixtoolset/wix3/releases)
- 确保 WiX 的 bin 目录已添加到系统 PATH
- 重启终端使 PATH 生效

#### Q8: macOS: 签名问题

如果需要发布到 App Store 或启用 Gatekeeper：
```bash
mvn jpackage:jpackage -Djpackage.type=dmg \
  -Dmac-sign=true \
  -Dmac-signing-key-user-name="Your Developer ID"
```

---

## 最佳实践

### 推荐工作流程

#### 开发阶段
```powershell
# 快速运行测试，无需打包
mvn javafx:run
```

#### 内部测试阶段
```powershell
# 创建便携版快速测试
cd packaging
.\build-portable.ps1

# 分发给测试人员
cd ..
Compress-Archive -Path "target\installer\CommitPal" `
                 -DestinationPath "test-build.zip"
```

#### 正式发布阶段
```powershell
# 1. 更新版本号（在 pom.xml）
# <version>1.0.0</version> → <version>1.1.0</version>

# 2. 确保有自定义图标

# 3. 创建正式安装程序
cd packaging
.\build-windows.ps1  # Windows
./build-mac.sh       # macOS

# 4. 测试安装程序
# - 在干净的系统上测试
# - 确认所有功能正常
# - 检查快捷方式和图标

# 5. 发布
# - 上传到 GitHub Releases
# - 或其他分发渠道
```

### 发布前检查清单

- [ ] 版本号已更新（pom.xml）
- [ ] 应用在开发环境运行正常
- [ ] 已准备好自定义图标（可选）
- [ ] 环境检测通过（check-env.ps1）
- [ ] 编译无错误（mvn clean package）
- [ ] 打包成功完成
- [ ] 安装程序可以正常安装
- [ ] 已安装的应用可以正常运行
- [ ] 测试所有主要功能
- [ ] 检查快捷方式和图标显示正常
- [ ] 在干净的系统上测试
- [ ] 确认卸载功能正常
- [ ] 准备好发行说明（Release Notes）

---

## 高级主题

### 代码签名（Windows）

```powershell
# 使用 signtool 签名（需要代码签名证书）
signtool sign /f cert.pfx /p password `
  /t http://timestamp.digicert.com `
  CommitPal-1.0.0.exe
```

### 公证（macOS）

```bash
# 使用 Apple Developer 账号公证应用
xcrun altool --notarize-app --file CommitPal-1.0.0.dmg \
  --primary-bundle-id com.junjie.githelper \
  --username your@email.com --password @keychain:AC_PASSWORD
```

### 持续集成（GitHub Actions）

创建 `.github/workflows/build.yml`：

```yaml
name: Build Installers

on:
  push:
    tags:
      - 'v*'

jobs:
  build-windows:
    runs-on: windows-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Build Windows Installer
        run: |
          choco install wixtoolset
          cd packaging
          .\build-windows.ps1
      - uses: actions/upload-artifact@v3
        with:
          name: windows-installer
          path: target/installer/*.exe

  build-macos:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Build macOS Installer
        run: |
          cd packaging
          chmod +x build-mac.sh
          ./build-mac.sh
      - uses: actions/upload-artifact@v3
        with:
          name: macos-installer
          path: target/installer/*.dmg
```

### 减小安装包体积

编辑 `pom.xml`，只包含必需的 Java 模块：

```xml
<javaOptions>
  <option>--add-modules</option>
  <option>java.base,java.desktop,java.logging</option>
</javaOptions>
```

---

## 学习资源

- **JPackage 官方文档**: https://docs.oracle.com/en/java/javase/21/jpackage/
- **Maven JPackage Plugin**: https://github.com/petr-panteleyev/jpackage-maven-plugin
- **JavaFX 打包指南**: https://openjfx.io/openjfx-docs/#install-javafx
- **图标制作指南**: [ICONS.md](ICONS.md)

---

## 总结

### 快速命令参考

```powershell
# 环境检测
cd packaging && .\check-env.ps1

# Windows 便携版（推荐新手）
.\build-portable.ps1

# Windows 安装程序（需要 WiX）
.\build-windows.ps1

# macOS 安装程序
chmod +x build-mac.sh && ./build-mac.sh

# 开发运行（无需打包）
cd .. && mvn javafx:run
```

### 推荐方案

- **新手/快速测试**: `.\build-portable.ps1`
- **正式发布**: `.\build-windows.ps1` 或 `./build-mac.sh`
- **开发调试**: `mvn javafx:run`

---

**配置完成日期**：2025-11-03  
**Maven 插件版本**：jpackage-maven-plugin 1.6.5  
**支持平台**：Windows / macOS / Linux

