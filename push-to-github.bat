@echo off
echo Initializing Git repository...
"C:\Program Files\Git\bin\git.exe" init

echo Configuring Git...
"C:\Program Files\Git\bin\git.exe" config user.name "Bradderz65"
"C:\Program Files\Git\bin\git.exe" config user.email "bradjhosk@outlook.com"

echo Adding files to repository...
"C:\Program Files\Git\bin\git.exe" add .

echo Committing changes...
"C:\Program Files\Git\bin\git.exe" commit -m "Implemented walking stamina drain with configurable options"

echo Setting up remote repository...
"C:\Program Files\Git\bin\git.exe" remote add origin https://github.com/Bradderz65/realistic-stamina-mod.git

echo Pushing to GitHub...
"C:\Program Files\Git\bin\git.exe" push -u origin master

echo Done!
pause 