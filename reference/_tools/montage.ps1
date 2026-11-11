param([string]$dst,[int]$w=300,[Parameter(ValueFromRemainingArguments=$true)][string[]]$files)
Add-Type -AssemblyName System.Drawing
$imgs=$files|%{[System.Drawing.Image]::FromFile($_)}
$h=[int]($imgs[0].Height*$w/$imgs[0].Width)
$bmp=New-Object System.Drawing.Bitmap ($w*$imgs.Count+4*($imgs.Count-1)),$h
$g=[System.Drawing.Graphics]::FromImage($bmp);$g.Clear([System.Drawing.Color]::Red)
$g.InterpolationMode=[System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$x=0;foreach($i in $imgs){$g.DrawImage($i,$x,0,$w,$h);$x+=$w+4;$i.Dispose()}
$bmp.Save($dst,[System.Drawing.Imaging.ImageFormat]::Jpeg);$bmp.Dispose()
