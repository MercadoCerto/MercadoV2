Write-Host ""
Write-Host "=== Configurando Java 21 para o projeto MercadoCerto ==="
Write-Host ""

# 1. Tenta localizar o Java 21 no sistema
$javaPaths = @(
  "C:\Program Files\Eclipse Adoptium",
  "C:\Program Files\Java",
  "C:\Program Files (x86)\Java"
)

$foundPath = $null

foreach ($path in $javaPaths) {
  if (Test-Path $path) {
    $jdkFolders = Get-ChildItem -Path $path -Directory | Where-Object { $_.Name -match "jdk-21" }
    if ($jdkFolders.Count -gt 0) {
      $foundPath = "$($jdkFolders[0].FullName)"
      break
    }
  }
}

if (-not $foundPath) {
  Write-Host "Nenhuma instalação do Java 21 foi encontrada."
  Write-Host "Baixe e instale o Java 21 por aqui: https://adoptium.net/temurin/releases/?version=21"
  exit
}

# 2. Atualiza as variáveis de ambiente
$javaBin = "$foundPath\bin"
[Environment]::SetEnvironmentVariable("JAVA_HOME", $foundPath, [EnvironmentVariableTarget]::Machine)

$envPath = [Environment]::GetEnvironmentVariable("Path", [EnvironmentVariableTarget]::Machine)
if ($envPath -notmatch [regex]::Escape($javaBin)) {
  [Environment]::SetEnvironmentVariable("Path", "$envPath;$javaBin", [EnvironmentVariableTarget]::Machine)
  Write-Host "JAVA_HOME e PATH configurados."
}

# 3. Atualiza a configuração do Maven no pom.xml
$pomPath = "C:\Users\David Souza\Pictures\startup\pom.xml"
if (Test-Path $pomPath) {
  (Get-Content $pomPath) |
    ForEach-Object { $_ -replace '<java\.version>.*</java\.version>', '<java.version>21</java.version>' } |
    Set-Content $pomPath -Encoding UTF8
  Write-Host "Versão do Java atualizada no pom.xml para 21."
} else {
  Write-Host "Arquivo pom.xml não encontrado no caminho esperado."
}

# 4. Exibe a versão final para validação
Write-Host ""
Write-Host "Validando configuração..."
cmd /c "java -version"
Write-Host ""
Write-Host "Configuração concluída com sucesso."
Write-Host "Reinicie o PowerShell e execute: mvn clean package"
Write-Host ""
