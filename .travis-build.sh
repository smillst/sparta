#!/bin/bash
ROOT=$TRAVIS_BUILD_DIR/..

# Fail the whole script if any command fails
set -e

wget https://dl.google.com/android/android-sdk_r24.4.1-linux.tgz
tar -xvf android-sdk_*-linux.tgz
cd android-sdk-linux/tools
# install all sdk packages
#./android list sdk
./android update sdk --no-ui -t 3,5

# adb
apt-get install libc6:i386 libstdc++6:i386
# aapt
apt-get install zlib1g:i386

## Build Checker Framework
export CHECKERFRAMEWORK=$ROOT/checker-framework
(cd $ROOT && git clone --depth 9 https://github.com/typetools/checker-framework.git)
# This also builds annotation-tools and jsr308-langtools
(cd $CHECKERFRAMEWORK && ./.travis-build-without-test.sh)


export ANDROID_HOME:$HOME/sdk/android-sdk-linux/
## Build
ant jar

## Run tests
#ant all-tests
