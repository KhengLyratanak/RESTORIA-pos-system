package com.nak.demo.mapper;

import com.nak.demo.dto.setting.ShopSettingDto;
import com.nak.demo.entity.ShopSetting;
import org.springframework.stereotype.Component;

@Component
public class ShopSettingMapper {

    public ShopSettingDto toDto(ShopSetting entity) {
        ShopSettingDto dto = new ShopSettingDto();
        dto.setVatRate(entity.getVatRate());
        dto.setServiceRate(entity.getServiceRate());
        return dto;
    }

    public void updateEntityFromDto(ShopSetting entity, ShopSettingDto dto) {
        entity.setVatRate(dto.getVatRate());
        entity.setServiceRate(dto.getServiceRate());
    }
}
