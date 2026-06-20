$ErrorActionPreference = "Stop"

$env:JAVA_HOME = [Environment]::GetEnvironmentVariable("JAVA_HOME", "User")
$env:MAVEN_HOME = [Environment]::GetEnvironmentVariable("MAVEN_HOME", "User")
$env:M2_HOME = [Environment]::GetEnvironmentVariable("M2_HOME", "User")
$env:MYSQL_HOME = [Environment]::GetEnvironmentVariable("MYSQL_HOME", "User")
$env:Path = [Environment]::GetEnvironmentVariable("Path", "User") + ";" + [Environment]::GetEnvironmentVariable("Path", "Machine")

& (Join-Path $PSScriptRoot "start-mysql.ps1")
Set-Location (Split-Path $PSScriptRoot -Parent)
mvn spring-boot:run
