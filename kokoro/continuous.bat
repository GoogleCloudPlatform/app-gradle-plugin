@echo on

REM Use Java 8 for build
echo %JAVA_HOME%

cd github/app-gradle-plugin

call gcloud.cmd components update --quiet
call gcloud.cmd components install app-engine-java --quiet

rem skip format check, because it fails for some line ending weirdness
rem and it's anyway checked on ubuntu
call gradlew.bat check -x verifyGoogleJavaFormat
REM curl -s https://codecov.io/bash | bash

exit /b %ERRORLEVEL%
