@echo off
echo Kompilerer Notes App...

REM Find Java installation
for /f "tokens=2*" %%i in ('reg query "HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Development Kit" /s /v JavaHome 2^>nul ^| find "JavaHome"') do set JAVA_HOME=%%j

REM Hvis JAVA_HOME ikke blev fundet, prøv almindelig javac
if not defined JAVA_HOME (
    set JAVAC_CMD=javac
) else (
    set JAVAC_CMD="%JAVA_HOME%\bin\javac"
)

REM Opret bin mappe hvis den ikke findes
if not exist "bin" mkdir bin

REM Kompiler Java filer
cd src\main\java
%JAVAC_CMD% -d ..\..\..\bin *.java

if %errorlevel% equ 0 (
    echo.
    echo Kompilering fuldført!
    echo Kør run.bat for at starte programmet.
) else (
    echo.
    echo Fejl under kompilering!
    echo Prøv at installere JDK eller tilføje javac til PATH
)

cd ..\..\..\
pause