@echo off

if exist gnKeys del gnKeys

keytool -genkey -keystore gnKeys -alias mitex -validity 3650
rem 1491 ?

pause
