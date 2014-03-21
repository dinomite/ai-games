#!/bin/bash
ORIGINAL_DIR="`pwd`"
SCRIPT_DIR="`( cd \"$MY_PATH\" && pwd )`"
SOURCE_ROOT="src/main/java"

ZIPFILE="$SCRIPT_DIR/dinomite-bot.zip"

rm "$ZIPFILE"

cd "$SOURCE_ROOT"
zip -r "$ZIPFILE" README.md com bot net

cd "$ORIGINAL_DIR"
