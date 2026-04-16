param(
    [string]$OutputDir = ".\\deployment\\images"
)

$ErrorActionPreference = "Stop"

if (!(Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir | Out-Null
}

$images = @(
    "coal-platform-web:latest",
    "coal-platform-server:latest",
    "postgres:16"
)

foreach ($image in $images) {
    $safeName = ($image -replace "[:/]", "_") + ".tar"
    $target = Join-Path $OutputDir $safeName
    Write-Host "Exporting $image -> $target"
    docker save -o $target $image
}

Write-Host "Image export completed."
