param(
    [string]$OutputDir = ".\\deployment\\package"
)

$ErrorActionPreference = "Stop"

$items = @(
    "docker-compose.yml",
    ".env.docker.example",
    "docs\\docker-deployment.md",
    "server\\Dockerfile",
    "web\\Dockerfile",
    "web\\nginx.conf",
    "scripts\\export-images.ps1",
    "scripts\\import-images.ps1"
)

if (Test-Path $OutputDir) {
    Remove-Item -Recurse -Force $OutputDir
}

New-Item -ItemType Directory -Path $OutputDir | Out-Null

foreach ($item in $items) {
    $source = Join-Path (Get-Location) $item
    $target = Join-Path $OutputDir $item
    $targetParent = Split-Path $target -Parent
    if (!(Test-Path $targetParent)) {
        New-Item -ItemType Directory -Path $targetParent | Out-Null
    }
    Copy-Item -Path $source -Destination $target -Force
}

Write-Host "Delivery package prepared at $OutputDir"
