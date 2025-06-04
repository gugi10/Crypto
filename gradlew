#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=""

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "ERROR: $*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

CLASSPATH_SEPARATOR=:
if $cygwin || $msys; then
  CLASSPATH_SEPARATOR=\;
fi

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
APP_HOME=`dirname "$PRG"`

# Absolutize APP_HOME
APP_HOME=`cd "$APP_HOME" && pwd`

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if ! $cygwin && ! $msys && ! $darwin && ! $nonstop; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# Collect all arguments for passing to Gradle.
# Support for passing arguments with spaces is not best effort, but does work in most common cases.
# Minimal quoting of args when appropriate is used. This is based on the approach used by unixutils options.awk.
GRADLE_OPTS=
for arg in "$@" ; do
  case "$arg" in
    # Handle args with equals signs, like --foo=bar. We need to quote the equals sign.
    *=*) GRADLE_OPTS="$GRADLE_OPTS \"$arg\"" ;;
    # Handle args with spaces. We need to quote the whole arg.
    *\ *) GRADLE_OPTS="$GRADLE_OPTS \"$arg\"" ;;
    # Handle args with only safe characters. No quoting needed.
    *[A-Za-z0-9]*) GRADLE_OPTS="$GRADLE_OPTS $arg" ;;
    # Default: just pass the arg. If it is problematic, use a solution like the equals sign or space handling.
    *) GRADLE_OPTS="$GRADLE_OPTS $arg" ;;
  esac
done

# Add the jar to the classpath
# Defines where the gradle wrapper jar is located
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
# Add the command line client jar to the classpath
# No command line client jar as of gradle 7.x. Remove this line.
# GRADLE_COMMAND_LINE_CLIENT_JAR="$APP_HOME/gradle/wrapper/gradle-cli.jar"
# CLASSPATH="$WRAPPER_JAR${CLASSPATH_SEPARATOR}${GRADLE_COMMAND_LINE_CLIENT_JAR}"
CLASSPATH="$WRAPPER_JAR"


# Execute Gradle
exec "$JAVACMD" "$DEFAULT_JVM_OPTS" "$JAVA_OPTS" "$GRADLE_OPTS" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
