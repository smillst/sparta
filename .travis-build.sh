#!/bin/bash
ROOT=$TRAVIS_BUILD_DIR/..

# Fail the whole script if any command fails
set -e

wget http://dl.google.com/android/android-sdk_r24.2-linux.tgz
tar -xvf android-sdk_r24.2-linux.tgz
cd android-sdk-linux/tools
# install all sdk packages
./android update sdk --no-ui

# set path
vi ~/.zshrc << EOT

export PATH=${PATH}:$HOME/sdk/android-sdk-linux/platform-tools:$HOME/sdk/android-sdk-linux/tools:$HOME/sdk/android-sdk-linux/build-tools/22.0.1/

EOT

source ~/.zshrc

# adb
sudo apt-get install libc6:i386 libstdc++6:i386
# aapt
sudo apt-get install zlib1g:i386

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
