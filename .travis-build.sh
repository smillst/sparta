#!/bin/bash
ROOT=$TRAVIS_BUILD_DIR/..

# Fail the whole script if any command fails
set -e
export CHECKERFRAMEWORK=$ROOT/checker-framework
## Build Checker Framework
(cd $ROOT && git clone --depth 9 https://github.com/typetools/checker-framework.git)
# This also builds annotation-tools and jsr308-langtools
(cd $CHECKERFRAMEWORK && ./.travis-build-without-test.sh)



## Build
ant jar

## Run tests
#ant all-tests
