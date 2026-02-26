@echo off
setlocal EnableExtensions

call mvn clean test org.pitest:pitest-maven:mutationCoverage > nul 2>&1
set "EC=%ERRORLEVEL%"

if "%EC%"=="0" (
  echo PIT: OK
  exit /b 0
) else (
  echo PIT: FAILED
  exit /b %EC%
)
