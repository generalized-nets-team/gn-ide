@echo off

if "%PROCESSOR_ARCHITECTURE%"=="x86" java -cp Genedit.jar;swt-win32.jar net.generalised.genedit.main.Main "%1" "%2" "%3" "%4"
if not "%PROCESSOR_ARCHITECTURE%"=="x86" java -cp Genedit.jar;swt-win64.jar net.generalised.genedit.main.Main "%1" "%2" "%3" "%4"

