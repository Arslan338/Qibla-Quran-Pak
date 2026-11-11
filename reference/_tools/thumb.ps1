param([string]$src,[string]$dst,[int]$w=540)
Add-Type -AssemblyName System.Drawing
$img=[System.Drawing.Image]::FromFile($src)
$h=[int]($img.Height*$w/$img.Width)
$bmp=New-Object System.Drawing.Bitmap $w,$h
$g=[System.Drawing.Graphics]::FromImage($bmp)
$g.InterpolationMode=[System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.DrawImage($img,0,0,$w,$h)
$bmp.Save($dst,[System.Drawing.Imaging.ImageFormat]::Jpeg)
$img.Dispose();$bmp.Dispose()
