#!/bin/bash
export ANDROID_HOME=/home/codespace/Android
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
./gradlew --refresh-dependencies
