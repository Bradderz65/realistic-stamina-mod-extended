@echo off
echo Pushing changelog to GitHub...

echo Checking Git status...
"C:\Program Files\Git\bin\git.exe" status

echo Adding all changes...
"C:\Program Files\Git\bin\git.exe" add .

echo Committing changes with changelog reference...
"C:\Program Files\Git\bin\git.exe" commit -m "Updated CHANGELOG.md with latest changes"

echo Pushing to GitHub...
"C:\Program Files\Git\bin\git.exe" push -u origin master

echo Done! Changelog has been pushed to GitHub.
pause 