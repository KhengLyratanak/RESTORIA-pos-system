package com.nak.demo.service;

import com.nak.demo.dto.setting.ShopSettingDto;
import com.nak.demo.entity.ShopSetting;
import com.nak.demo.mapper.ShopSettingMapper;
import com.nak.demo.repository.ShopSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopSettingService {

    @Autowired
    private ShopSettingRepository shopSettingRepository;

    @Autowired
    private ShopSettingMapper shopSettingMapper;

    /** The settings row. The first time, it is created with 0 % VAT and 0 % service. */
    @Transactional
    public ShopSetting getCurrent() {
        return shopSettingRepository.findById(ShopSetting.ID)
                .orElseGet(() -> shopSettingRepository.save(new ShopSetting()));
    }

    @Transactional
    public ShopSettingDto getSetting() {
        return shopSettingMapper.toDto(getCurrent());
    }

    @Transactional
    public ShopSettingDto updateSetting(ShopSettingDto payload) {
        ShopSetting setting = getCurrent();
        shopSettingMapper.updateEntityFromDto(setting, payload);
        shopSettingRepository.save(setting);
        return shopSettingMapper.toDto(setting);
    }
}
