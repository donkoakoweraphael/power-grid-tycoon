@echo off
if not exist bin mkdir bin
echo Compiling...
javac -d bin -sourcepath src src/Main.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)
echo Running Power Grid Tycoon (Console Mode)...
java -cp bin Main
pause
