$ErrorActionPreference = "Stop"

$mysqlHome = "C:\Program Files\MySQL\MySQL Server 8.4"
$mysqld = Join-Path $mysqlHome "bin\mysqld.exe"
$mysql = Join-Path $mysqlHome "bin\mysql.exe"
$myini = "C:\ProgramData\MySQL\MySQL Server 8.4\my.ini"

if (-not (Test-Path $mysqld)) {
    throw "MySQL Server was not found at $mysqld"
}

$portOpen = (Test-NetConnection 127.0.0.1 -Port 3306 -WarningAction SilentlyContinue).TcpTestSucceeded
if (-not $portOpen) {
    Start-Process -FilePath $mysqld -ArgumentList "--defaults-file=`"$myini`"" -WindowStyle Hidden
    for ($i = 0; $i -lt 30; $i++) {
        Start-Sleep -Seconds 2
        $portOpen = (Test-NetConnection 127.0.0.1 -Port 3306 -WarningAction SilentlyContinue).TcpTestSucceeded
        if ($portOpen) { break }
    }
}

if (-not $portOpen) {
    throw "MySQL did not start on localhost:3306"
}

$cmd = "`"$mysql`" -u root -proot --protocol=tcp --host=127.0.0.1 --port=3306 -e `"CREATE DATABASE IF NOT EXISTS fertilizer_db;`" 2>NUL"
cmd /c $cmd
if ($LASTEXITCODE -ne 0) {
    throw "Unable to connect to MySQL as root/root."
}
Write-Host "MySQL is running on localhost:3306 and fertilizer_db is ready."
