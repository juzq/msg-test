@echo off 

echo ���� %~nx1

%~dp0/protoc.exe --java_out=..\..\java -I %~dp1 %~dpnx1

pause
