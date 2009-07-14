#!/bin/sh
#PROJECT_HOME=`dirname "$0"`
BUILD_DIR="/home/kai/build"
REPO_BASE="http://omnidroid.googlecode.com/svn"
REPO_VENDOR="$REPO_BASE/vendor"
CO_DIR="$BUILD_DIR/work/checkout"
JUNIT_JAR="junit-4.6.jar"
TRUNK_DIR="$CO_DIR/trunk"
APP_DIR="$TRUNK_DIR/omnidroid"
TEST_DIR="$TRUNK_DIR/omnidroid-test"
ANDROID_DIR="$BUILD_DIR/tools/android-sdk-linux_x86-1.1_r1"
ANDROID_BIN_DIR="$ANDROID_DIR/tools"
MANIFEST="AndroidManifest.xml"
LOG_DIR="$BUILD_DIR/logs"
export JAVA_HOME="/usr"
export PATH="/usr/local/bin:/opt/bin:/opt/java/bin:/usr/bin:/bin:/usr/sbin:/sbin"
export CLASSPATH="${CLASSPATH}:$CO_DIR/vendor/$JUNIT_JAR"

cleanup() {
    # Cleanup
    echo "  + Removing $CO_DIR" && \
    rm -rf $CO_DIR && \
    mkdir -p $CO_DIR && \
    mkdir -p $TRUNK_DIR && \
    chmod 755 $LOG_DIR && \
    chmod 644 $LOG_DIR/* && \
    echo "  + Environment set to:" && \
    env && \
    return $?
}

checkout() {
    # Checkout the source
    echo "  + Checking out source from SVN." && \
    cd $CO_DIR && \
    svn co ${REPO_BASE}/trunk
    return $?
}

build() {
    # TODO update this to 1.5 SDK (./android)
    # Build it
    echo "  + Using android tools to build an ant file" && \
    cd $APP_DIR && \
    $ANDROID_BIN_DIR/activitycreator --out $APP_DIR $MANIFEST && \
    echo "  + Building from ant build file" && \
    ant
    # copy jar to omnidroid-test to run against
    # jar cf omnidroid-test/libs/omnidroid.jar omnidroid/bin/classes/
    return $?
}

run_tests() {
    # Run J-unit Tests
    echo "   + Running Junit Tests on project" && \
    # copy activitycreator command from above for omnidroid-test
    # emulator -no-window -avd testavd -verbose
    # adb install -r omnidroid.apk
    # adb install -r omnidroid-test.apk
    # adb shell am instrument -w edu.nyu.cs.omnidroid.tests/android.test.InstrumentationTestRunner
    #svn co ${REPO_VENDOR}/${JUNIT_JAR}
    echo "TODO: Setup Junit Testing"
}

cleanup && \
checkout && \
build
exit $?
