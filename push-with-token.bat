@echo off
echo Setting up repository with token-based authentication...

:: The repository is already initialized from the previous script
echo Setting up remote repository with HTTPS URL...
"C:\Program Files\Git\bin\git.exe" remote set-url origin https://Bradderz65@github.com/Bradderz65/realistic-stamina-mod-extended.git

echo Pushing to GitHub with force...
:: This will prompt for your GitHub password or personal access token
"C:\Program Files\Git\bin\git.exe" push -u origin master --force
"C:\Program Files\Git\bin\git.exe" push origin v1.4.3.0 --force

echo Done!
pause 