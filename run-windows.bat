@echo off
cd /d %~dp0
mvn clean test
if errorlevel 1 pause & exit /b 1
mvn spring-boot:run
