@echo off
echo Starting ADB server...
cd /d "C:\Users\dipes\AppData\Local\Android\Sdk\platform-tools"
adb.exe start-server

echo Starting Pixel 10 Emulator with visible window...
cd /d "C:\Users\dipes\AppData\Local\Android\Sdk\emulator"
start "" emulator.exe -avd Pixel_10 -no-snapshot-load
exit
