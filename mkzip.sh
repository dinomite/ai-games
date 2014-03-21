#!/bin/bash -e
ORIGINAL_DIR="`pwd`"
SCRIPT_DIR="`( cd \"$MY_PATH\" && pwd )`"
SOURCE_ROOT="src/main/java"

ZIPFILE="$SCRIPT_DIR/dinomite-bot.zip"

rm -f "$ZIPFILE"

cd "$SOURCE_ROOT"

# Make sure it builds
javac bot/BotStarter.java
find .|grep "\.class$"|xargs rm

zip -r "$ZIPFILE" com bot net

cd "$ORIGINAL_DIR"
