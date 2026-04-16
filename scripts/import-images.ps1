param(
    [string]$InputDir = ".\\deployment\\images"
)

$ErrorActionPreference = "Stop"

if (!(Test-Path $InputDir)) {
    throw "Input directory not found: $InputDir"
}

$archives = Get-ChildItem -Path $InputDir -Filter *.tar
if ($archives.Count -eq 0) {
    throw "No image archives found in $InputDir"
}

foreach ($archive in $archives) {
    Write-Host "Importing $($archive.FullName)"
    docker load -i $archive.FullName
}

Write-Host "Image import completed."
