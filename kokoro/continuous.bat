@echo on

REM Java 9 does not work with our build at the moment, so force java 8
set JAVA_HOME=c:\program files\java\jdk1.8.0_211
set PATH=%JAVA_HOME%\bin;%PATH%

cd github/app-gradle-plugin

call gcloud.cmd components update --quiet
call gcloud.cmd components install app-engine-java --quiet

rem skip format check, because it fails for some line ending weirdness
rem and it's anyway checked on ubuntu
call gradlew.bat check -x verifyGoogleJavaFormat
REM curl -s https://codecov.io/bash | bash

exit /b %ERRORLEVEL%
