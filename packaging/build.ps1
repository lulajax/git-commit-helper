#!/usr/bin/env pwsh
# 跨平台打包脚本（PowerShell Core）

Write-Host "================================" -ForegroundColor Green
Write-Host Commit-Pal - 自动打包工具" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""

# 切换到项目根目录
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location (Split-Path -Parent $scriptDir)

# 检测操作系统
$isWindows = $IsWindows -or ($PSVersionTable.PSVersion.Major -lt 6) -or ($null -eq $IsWindows)
$isMacOS = $IsMacOS
$isLinux = $IsLinux

if ($isWindows) {
    Write-Host "检测到 Windows 系统，将生成 .exe 安装程序" -ForegroundColor Cyan
    $packageType = "EXE"
} elseif ($isMacOS) {
    Write-Host "检测到 macOS 系统，将生成 .dmg 安装程序" -ForegroundColor Cyan
    $packageType = "DMG"
} elseif ($isLinux) {
    Write-Host "检测到 Linux 系统，将生成 .deb 安装程序" -ForegroundColor Cyan
    $packageType = "DEB"
} else {
    Write-Host "无法检测操作系统，将生成应用镜像" -ForegroundColor Yellow
    $packageType = "APP_IMAGE"
}

Write-Host ""
Write-Host "[1/4] 检查 Java 版本..." -ForegroundColor Yellow
$javaVersion = java -version 2>&1 | Select-String -Pattern 'version "(\d+)'
if ($javaVersion) {
    Write-Host "✓ Java 已安装" -ForegroundColor Green
} else {
    Write-Host "✗ 未找到 Java，请安装 JDK 21 或更高版本" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/4] 清理旧的构建文件..." -ForegroundColor Yellow
mvn clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ 清理失败" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[3/4] 编译并打包应用..." -ForegroundColor Yellow
mvn package
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ 编译失败" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[4/4] 创建安装程序 ($packageType)..." -ForegroundColor Yellow
if ($isWindows -and ($packageType -eq "EXE" -or $packageType -eq "MSI")) {
    mvn jpackage:jpackage `"-Djpackage.type=$packageType`" `"-Djpackage.winMenu=true`" `"-Djpackage.winShortcut=true`" `"-Djpackage.winDirChooser=true`" `"-Djpackage.winMenuGroup=Git Commit Helper`"
} else {
    mvn jpackage:jpackage `"-Djpackage.type=$packageType`"
}
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ 打包失败" -ForegroundColor Red
    Write-Host ""
    Write-Host "提示：" -ForegroundColor Yellow
    if ($isWindows) {
        Write-Host "Windows 需要安装 WiX Toolset: https://wixtoolset.org/" -ForegroundColor Yellow
    }
    exit 1
}

Write-Host ""
Write-Host "================================" -ForegroundColor Green
Write-Host "✓ 构建成功！" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "安装文件位置: " -NoNewline
Write-Host "target\installer\" -ForegroundColor Cyan
Write-Host ""

# 列出生成的文件
if (Test-Path "target\installer") {
    Write-Host "生成的文件：" -ForegroundColor Yellow
    Get-ChildItem -Path "target\installer" -File | ForEach-Object {
        Write-Host "  - $($_.Name)" -ForegroundColor Cyan
    }
}

