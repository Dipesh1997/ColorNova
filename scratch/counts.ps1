$j = Get-Content 'app\src\main\assets\colors.json' -Raw | ConvertFrom-Json
Write-Host "light count: $($j.light.Count)"
Write-Host "medium count: $($j.medium.Count)"
Write-Host "dark count: $($j.dark.Count)"
Write-Host "combos count: $($j.combos.Count)"
$mediumPages = $j.medium | ForEach-Object { $_.p } | Select-Object -Unique
Write-Host "medium unique pages: $($mediumPages.Count) (min: $(($mediumPages | Measure-Object -Minimum).Minimum), max: $(($mediumPages | Measure-Object -Maximum).Maximum))"
