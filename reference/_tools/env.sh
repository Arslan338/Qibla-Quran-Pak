export MSYS_NO_PATHCONV=1
ADB="/c/Users/Admin/AppData/Local/Android/Sdk/platform-tools/adb.exe"
PKG=prayertime.compassdirection.qiblafinder.hijricalender
T=G:/QiblaQuranPak/reference/_tools
S=G:/QiblaQuranPak/reference/screens
tap(){ "$ADB" shell input tap $1 $2; }
sw(){ "$ADB" shell input swipe $1 $2 $3 $4 ${5:-400}; }
back(){ "$ADB" shell input keyevent 4; }
cap(){ $T/cap.sh "$@"; }
shot(){ "$ADB" exec-out screencap -p > $S/$1.png; }
dump(){ rm -f $S/_tmp.xml; for i in 1 2 3; do "$ADB" shell rm -f /sdcard/u.xml; "$ADB" shell uiautomator dump /sdcard/u.xml 2>&1 | grep -q dumped && break; done; "$ADB" pull /sdcard/u.xml $S/_tmp.xml >/dev/null 2>&1 && node G:/QiblaQuranPak/reference/_tools/ui.js $S/_tmp.xml; }
act(){ "$ADB" shell dumpsys activity activities | grep -E "topResumedActivity|ResumedActivity" | head -2; }
px(){ node G:/QiblaQuranPak/reference/_tools/px.js "$@"; }
closead(){ # wait for interstitial close and tap it
 for i in $(seq 1 25); do
  local a=$("$ADB" shell dumpsys activity activities | grep -m1 topResumedActivity)
  if ! echo "$a" | grep -qE "AdActivity|vending|AudienceNetwork|applovin|unity|mbridge|ironsource|vungle|inmobi"; then echo "no ad: $a"; return 0; fi
  if echo "$a" | grep -q vending; then back; sleep 1; continue; fi
  "$ADB" shell rm -f /sdcard/u.xml; "$ADB" shell uiautomator dump /sdcard/u.xml >/dev/null 2>&1; "$ADB" pull /sdcard/u.xml $S/_ad.xml >/dev/null 2>&1
  local l=$(node $T/ui.js $S/_ad.xml | grep -iE "\[Close( sheet)?\]|\[close\]|Skip|\"Close\"|\"X\"|close_button|Continue|close button" | head -1)
  if echo "$l" | grep -q "Close sheet"; then back; sleep 1; continue; fi
  if [ -n "$l" ]; then local b=$(echo "$l" | sed -E 's/.*px\[([0-9]+),([0-9]+),([0-9]+),([0-9]+)\].*/\1 \2 \3 \4/'); set -- $b; echo "tap close: $l"; tap $(( ($1+$3)/2 )) $(( ($2+$4)/2 )); sleep 1.5; else sleep 2; fi
 done; echo "gave up"; }
TH="C:/Users/Admin/AppData/Local/Temp/claude/043bc22a1ed08443/a8fc43c0-08b5-409a-8c32-3e78b4bfb8f9/scratchpad/th"
th(){ powershell -NoProfile -ExecutionPolicy Bypass -File G:/QiblaQuranPak/reference/_tools/thumb.ps1 "G:/QiblaQuranPak/reference/screens/$1.png" "$TH/$1.jpg" ${2:-540}; echo "$TH/$1.jpg"; }
mont(){ local out=$1; local w=$2; shift 2; local args=(); for f in "$@"; do args+=("G:/QiblaQuranPak/reference/screens/$f.png"); done; powershell -NoProfile -ExecutionPolicy Bypass -File G:/QiblaQuranPak/reference/_tools/montage.ps1 "$TH/$out.jpg" $w "${args[@]}"; echo "$TH/$out.jpg"; }
visit(){ tap $1 $2; sleep ${4:-3}; closead >/dev/null; LINES_MAX=200 cap $3 | grep -E "\"" | grep -vE "#(home|hijri_calendar|learning|quran)_tv" | sed -E 's/ px\[[0-9,]+\]//' | cut -c1-160 | head -${5:-30}; }
closeprem(){ if dump | grep -q fiftyPercentOffText; then tap 720 2293; sleep 1.5; local l=$(dump | grep yesBtn); if [ -n "$l" ]; then local b=$(echo "$l" | sed -E 's/.*px\[([0-9]+),([0-9]+),([0-9]+),([0-9]+)\].*/\1 \2 \3 \4/'); set -- $b; tap $(( ($1+$3)/2 )) $(( ($2+$4)/2 )); sleep 1.5; fi; echo "closed premium"; fi; }
