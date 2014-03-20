#!/bin/bash
ORIGINAL_DIR="`pwd`"
SCRIPT_PATH="`( cd \"$MY_PATH\" && pwd )`"
SCRIPT_DIR="${SCRIPT_PATH##*/}"

ZIPFILE="dinomite-bot.zip"

cd "$SCRIPT_DIR"

rm "$ZIPFILE"
zip -r "$ZIPFILE" README.md com bot

cd "$ORIGINAL_DIR"
