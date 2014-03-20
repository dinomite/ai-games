#!/bin/bash
ORIGINAL_PATH="`pwd`"
SCRIPT_PATH="`( cd \"$MY_PATH\" && pwd )`"
SCRIPT_DIR="${SCRIPT_PATH##*/}"
PARENT_DIR="`dirname "$SCRIPT_PATH"`"

ZIPFILE="$SCRIPT_DIR/dinomite-bot.zip"

cd "$PARENT_DIR"

rm "$ZIPFILE"
#zip -x *.idea* *.git* *README.md *.iml *.DS_Store *dinomite-bot.zip *mkzip.sh -r "$ZIPFILE" "$SCRIPT_DIR"
zip -r "$ZIPFILE" "$SCRIPT_DIR/README.md" "$SCRIPT_DIR/com" "$SCRIPT_DIR/net"

cd "$ORIGINAL_PATH"
