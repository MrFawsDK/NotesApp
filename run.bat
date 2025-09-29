@echo off
echo Starter Krypteret Notes App...

REM Tjek om Java er installeret
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo.
    echo FEJL: Java er ikke installeret eller ikke i PATH!
    echo.
    echo Installer Java JDK fra:
    echo https://adoptium.net/
    echo.
    echo Efter installation, genstart computeren og prøv igen.
    pause
    exit /b 1
)

REM Tjek om bin mappen findes
if not exist "bin" (
    echo.
    echo Bin mappen findes ikke. Kør først compile.bat
    echo.
    pause
    exit /b 1
)

REM Tjek om NotesApp.class findes
if not exist "bin\NotesApp.class" (
    echo.
    echo NotesApp.class ikke fundet i bin mappen.
    echo Kør compile.bat først for at kompilere programmet.
    echo.
    pause
    exit /b 1
)

REM Start programmet
cd bin
java NotesApp

if %errorlevel% neq 0 (
    echo.
    echo Fejl ved start af programmet!
    echo Tjek at alle .class filer er korrekt kompileret.
)

pause