# 打包目录说明

本目录包含所有打包相关的脚本和文档。

## 📁 文件结构

```
packaging/
├── README.md              # 完整打包指南（主文档，先看这个！）
├── ICONS.md              # 图标制作指南
├── check-env.ps1         # 环境检测脚本
├── build-windows.ps1     # Windows 安装程序打包脚本
├── build-windows.bat     # Windows 批处理版本
├── build-mac.sh          # macOS 安装程序打包脚本
├── build-portable.ps1    # 便携版打包脚本（推荐新手）
└── build.ps1             # 跨平台自动检测打包脚本
```

## 🚀 快速开始

### 1. 检查环境
```powershell
.\check-env.ps1
```

### 2. 选择打包方式

**Windows - 便携版（推荐）**：
```powershell
.\build-portable.ps1
```

**Windows - 安装程序**：
```powershell
.\build-windows.ps1
```

**macOS**：
```bash
chmod +x build-mac.sh
./build-mac.sh
```

### 3. 获取结果
打包完成后，查看 `../target/installer/` 目录。

## 📖 文档说明

### README.md（主文档）
这是完整的打包指南，包含：
- ✅ 环境要求和安装说明
- ✅ 详细的打包步骤
- ✅ 多种打包方式对比
- ✅ 实际场景示例
- ✅ 常见问题解答
- ✅ 故障排除指南
- ✅ 自定义配置说明
- ✅ 最佳实践和高级主题

**建议阅读顺序**：
1. 先看"快速开始"部分
2. 根据需要查看具体章节

### ICONS.md
图标制作和配置指南，包含：
- 图标格式要求
- 制作工具推荐
- 在线转换工具
- 命令行制作方法
- 配置步骤

## 🛠️ 脚本说明

### check-env.ps1 - 环境检测
**用途**：检查打包所需的环境是否完整  
**检查内容**：
- Java 版本（需要 21+）
- Maven 版本（需要 3.6+）
- WiX Toolset（Windows，可选）
- 项目结构完整性

**使用时机**：首次打包前，或遇到打包问题时

### build-portable.ps1 - 便携版打包（推荐）
**用途**：创建无需安装的便携版应用  
**优点**：
- 无需 WiX Toolset
- 解压即用
- 适合快速测试

**适用场景**：
- 首次打包
- 开发测试
- 个人使用
- U 盘分发

### build-windows.ps1 - Windows 安装程序
**用途**：创建专业的 Windows 安装程序（.exe）  
**要求**：需要安装 WiX Toolset  
**特性**：
- 安装向导
- 开始菜单项
- 桌面快捷方式
- 卸载功能

**适用场景**：
- 正式发布
- 分发给最终用户

### build-windows.bat - 批处理版本
功能与 `build-windows.ps1` 相同，适用于 CMD 环境。

### build-mac.sh - macOS 打包
**用途**：创建 macOS DMG 安装镜像  
**要求**：在 macOS 系统上运行  
**输出**：.dmg 文件

### build.ps1 - 跨平台自动打包
**用途**：自动检测操作系统并执行相应的打包脚本  
**特点**：
- 自动检测 Windows/macOS/Linux
- 选择合适的打包类型
- 统一的命令接口

## ⏱️ 打包时间参考

| 阶段 | 首次打包 | 后续打包 |
|------|----------|----------|
| 下载依赖 | 1-2 分钟 | 跳过 |
| 编译代码 | 30秒 | 30秒 |
| 创建 JRE | 2-3 分钟 | 跳过 |
| 打包应用 | 2-3 分钟 | 1-2 分钟 |
| **总计** | **5-10 分钟** | **1-3 分钟** |

## 📦 输出说明

所有打包结果都在 `../target/installer/` 目录中：

- **Windows EXE**: `CommitPal-1.0.0.exe` (~200MB)
- **Windows 便携版**: `CommitPal/` 文件夹 (~200MB)
- **macOS DMG**: `CommitPal-1.0.0.dmg` (~200MB)

**为什么这么大？**  
包含了完整的 Java 运行时，用户无需单独安装 Java。

## 🔧 常用命令

```powershell
# 检查环境
.\check-env.ps1

# 便携版（最简单）
.\build-portable.ps1

# 专业安装程序
.\build-windows.ps1

# 跨平台自动
.\build.ps1

# macOS (在 Mac 上运行)
./build-mac.sh

# 手动打包
cd ..
mvn clean package
mvn jpackage:jpackage -Djpackage.type=exe
```

## ❓ 遇到问题？

1. **先运行环境检测**：`.\check-env.ps1`
2. **查看完整文档**：[README.md](README.md)
3. **查看故障排除章节**：README.md 中有详细的问题解答

## 💡 提示

- **首次使用**：建议先使用 `build-portable.ps1`，无需额外配置
- **正式发布**：使用 `build-windows.ps1` 或 `build-mac.sh`
- **添加图标**：参考 [ICONS.md](ICONS.md)
- **修改配置**：编辑 `../pom.xml`

---

**更多详细信息，请查看 [README.md](README.md)**

