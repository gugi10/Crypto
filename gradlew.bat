@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.
goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.
goto fail

:init
@rem Get command-line arguments, handling Windowz /?
if "%1" == "/?" goto mainHelp
if "%1" == "-?" goto mainHelp
if "%1" == "--help" goto mainHelp
if "%1" == "-h" goto mainHelp

@rem Define where the gradle wrapper jar is located
set WRAPPER_JAR="%APP_HOME%\gradle\wrapper\gradle-wrapper.jar"

@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*

:mainHelp
echo.
echo "Usage: %APP_BASE_NAME% [option...] [task...]"
echo.
echo "Options:"
echo "  --help, -h, -?           Shows this help message."
echo "  --version, -v            Prints version info."
echo "  --console <option>       Specifies console output coloring mode (plain, auto, rich, verbose)."
echo "                           Default is auto."
echo "  --daemon                 Uses the Gradle Daemon to run the build."
echo "                           Starts the Daemon if not running."
echo "  --no-daemon              Does not use the Gradle Daemon to run the build."
echo "  --foreground             Starts the Gradle Daemon in the foreground."
echo "  --gui                    Launches the Gradle GUI."
echo "  --init-script <file>     Specifies an initialization script."
echo "  --offline                The build should operate without accessing network resources."
echo "  --parallel               Build projects in parallel. (incubating)"
echo "  --priority <priority>    Specifies the scheduling priority for the Gradle daemon and all processes launched by it."
echo "                           Values are 'normal' or 'low'. Default is 'normal'."
echo "  --profile                Profiles build execution time and generates a report in the <build_dir>/reports/profile directory."
echo "  --project-cache-dir <dir> Specifies the project-specific cache directory. Defaults to .gradle in the root project."
echo "  --project-dir <dir>      Specifies the start directory for Gradle. Defaults to current directory."
echo "  --quiet, -q              Log errors only."
echo "  --scan                   Creates a build scan. See https://gradle.com/build-scans."
echo "  --stacktrace, -S         Print out the stacktrace for all exceptions."
echo "  --info, -i               Set log level to info."
echo "  --debug, -d              Log in debug mode (includes normal stacktrace)."
echo "  --warning-mode <mode>    Specifies which mode of warnings to generate."
echo "                           Values are 'all', 'fail', 'summary'(default) or 'none'."
echo.
echo "For more information try 'gradlew tasks' or https://docs.gradle.org/current/userguide/command_line_interface.html"
echo.
goto end

:fail
set ERRORLEVEL=1

:end
if "%OS%"=="Windows_NT" endlocal

exit /B %ERRORLEVEL%
