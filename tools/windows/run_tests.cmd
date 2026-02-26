@echo off
setlocal EnableExtensions

set "LOG=%TEMP%\mvn_test_%RANDOM%.log"

call mvn clean test > "%LOG%" 2>&1
set "EC=%ERRORLEVEL%"

if "%EC%"=="0" (
  del "%LOG%" >nul 2>&1
  echo TESTS: OK
  exit /b 0
) else (
  echo TESTS: FAILED
  echo ---------------- Maven output ----------------
  type "%LOG%"
  echo ---------------- End output ------------------
  del "%LOG%" >nul 2>&1
  exit /b %EC%
)
