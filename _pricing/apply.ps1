$ErrorActionPreference = "Stop"
# Copies the discount / VAT / service charge files into the project, then removes this folder.
$here = $PSScriptRoot
$root = Split-Path $here -Parent
$java = Join-Path $root "src\main\java\com\nak\demo"

New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "common\security\SecurityConfig.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "common_security_SecurityConfig.java") (Join-Path $java "common\security\SecurityConfig.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "controller\OrderController.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "controller_OrderController.java") (Join-Path $java "controller\OrderController.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "dto\order\OrderCreateDto.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "dto_order_OrderCreateDto.java") (Join-Path $java "dto\order\OrderCreateDto.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "dto\order\OrderResponseDto.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "dto_order_OrderResponseDto.java") (Join-Path $java "dto\order\OrderResponseDto.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "entity\Order.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "entity_Order.java") (Join-Path $java "entity\Order.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "entity\OrderItem.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "entity_OrderItem.java") (Join-Path $java "entity\OrderItem.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "mapper\OrderItemMapper.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "mapper_OrderItemMapper.java") (Join-Path $java "mapper\OrderItemMapper.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "mapper\OrderMapper.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "mapper_OrderMapper.java") (Join-Path $java "mapper\OrderMapper.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "service\OrderService.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "service_OrderService.java") (Join-Path $java "service\OrderService.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "common\enums\DiscountType.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "common_enums_DiscountType.java") (Join-Path $java "common\enums\DiscountType.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "controller\ShopSettingController.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "controller_ShopSettingController.java") (Join-Path $java "controller\ShopSettingController.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "dto\order\OrderDiscountDto.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "dto_order_OrderDiscountDto.java") (Join-Path $java "dto\order\OrderDiscountDto.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "dto\setting\ShopSettingDto.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "dto_setting_ShopSettingDto.java") (Join-Path $java "dto\setting\ShopSettingDto.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "entity\ShopSetting.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "entity_ShopSetting.java") (Join-Path $java "entity\ShopSetting.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "mapper\ShopSettingMapper.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "mapper_ShopSettingMapper.java") (Join-Path $java "mapper\ShopSettingMapper.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "repository\ShopSettingRepository.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "repository_ShopSettingRepository.java") (Join-Path $java "repository\ShopSettingRepository.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "service\OrderPricingService.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "service_OrderPricingService.java") (Join-Path $java "service\OrderPricingService.java") -Force
New-Item -ItemType Directory -Force -Path (Split-Path (Join-Path $java "service\ShopSettingService.java") -Parent) | Out-Null
Copy-Item (Join-Path $here "service_ShopSettingService.java") (Join-Path $java "service\ShopSettingService.java") -Force
Write-Host "Done: 18 files copied (9 changed, 9 new)." -ForegroundColor Green
Set-Location $root
Remove-Item $here -Recurse -Force

