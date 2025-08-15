@echo off
REM Batch file to clean, compile, package, and launch the JavaFX app

setlocal


echo Launching JavaFX app...
mvn clean javafx:run

endlocal
pause