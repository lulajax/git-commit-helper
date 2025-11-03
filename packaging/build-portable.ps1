#!/usr/bin/env pwsh
# 便携版打包脚本 - 无需 WiX Toolset

Write-Host "================================" -ForegroundColor Green
Write-Host "开始构建便携版应用" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "便携版特点：" -ForegroundColor Cyan
Write-Host "  ✓ 无需安装，解压即用" -ForegroundColor Gray
Write-Host "  ✓ 不需要 WiX Toolset" -ForegroundColor Gray
Write-Host "  ✓ 包含完整的 Java 运行时" -ForegroundColor Gray
Write-Host "  ✓ 可以放在 U 盘或云盘中使用" -ForegroundColor Gray
Write-Host ""

# 切换到项目根目录
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location (Split-Path -Parent $scriptDir)

Write-Host "[1/3] 清理旧的构建文件..." -ForegroundColor Yellow
mvn clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ 清理失败" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/3] 编译并打包应用..." -ForegroundColor Yellow
mvn package
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ 编译失败" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[3/3] 创建便携版应用..." -ForegroundColor Yellow
mvn jpackage:jpackage `"-Djpackage.type=APP_IMAGE`"
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ 打包失败" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "================================" -ForegroundColor Green
Write-Host "✓ 构建成功！" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "便携版位置: " -NoNewline
Write-Host "target\installer\CommitPal\" -ForegroundColor Cyan
Write-Host ""
Write-Host "使用方法：" -ForegroundColor Yellow
Write-Host "  1. 将 CommitPal 文件夹复制到任意位置" -ForegroundColor Gray
Write-Host "  2. 运行 CommitPal\CommitPal.exe" -ForegroundColor Gray
Write-Host ""
Write-Host "文件夹大小：" -ForegroundColor Yellow
if (Test-Path "target\installer\CommitPal") {
    $size = (Get-ChildItem -Path "target\installer\CommitPal" -Recurse | 
             Measure-Object -Property Length -Sum).Sum / 1MB
    Write-Host "  约 $([math]::Round($size, 2)) MB" -ForegroundColor Cyan
}
Write-Host ""

# 询问是否创建 ZIP 压缩包
Write-Host "是否创建 ZIP 压缩包？(y/N): " -NoNewline -ForegroundColor Yellow
$response = Read-Host
if ($response -eq "y" -or $response -eq "Y") {
    Write-Host ""
    Write-Host "正在创建 ZIP 压缩包..." -ForegroundColor Yellow
    $zipPath = "target\installer\CommitPal-portable.zip"
    if (Test-Path $zipPath) {
        Remove-Item $zipPath -Force
    }
    Compress-Archive -Path "target\installer\CommitPal\*" -DestinationPath $zipPath
    Write-Host "✓ 压缩包已创建: $zipPath" -ForegroundColor Green
    
    $zipSize = (Get-Item $zipPath).Length / 1MB
    Write-Host "压缩包大小: $([math]::Round($zipSize, 2)) MB" -ForegroundColor Cyan
}

