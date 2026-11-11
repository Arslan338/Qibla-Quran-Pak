#!/usr/bin/env bash
export MSYS_NO_PATHCONV=1
# usage: cap.sh NAME [nodump]  -> screens/NAME.png (+ NAME.xml)
ADB="/c/Users/Admin/AppData/Local/Android/Sdk/platform-tools/adb.exe"
OUT=G:/QiblaQuranPak/reference/screens
"$ADB" exec-out screencap -p > "$OUT/$1.png"
if [ "$2" != "nodump" ]; then
  rm -f "$OUT/$1.xml"
  for i in 1 2 3; do
    "$ADB" shell rm -f /sdcard/u.xml
    if "$ADB" shell uiautomator dump /sdcard/u.xml 2>&1 | grep -q "dumped"; then break; fi
  done
  "$ADB" pull /sdcard/u.xml "$OUT/$1.xml" >/dev/null 2>&1 || { echo "DUMP FAILED"; exit 0; }
  node G:/QiblaQuranPak/reference/_tools/ui.js "$OUT/$1.xml" | grep -vE "ad_|video|ima-|tappable|mute|pause|replay|Ad Choices|WebView" | head -${LINES_MAX:-70}
fi
