package com.nak.demo.controller;

import com.nak.demo.dto.base.Response;
import com.nak.demo.dto.setting.ShopSettingDto;
import com.nak.demo.service.ShopSettingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
public class ShopSettingController {

    @Autowired
    private ShopSettingService shopSettingService;

    @GetMapping
    public ResponseEntity<Response> getSetting() {
        ShopSettingDto setting = shopSettingService.getSetting();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200", "success", "successfully retrieved settings", setting));
    }

    /** Admin only (see SecurityConfig). */
    @PutMapping
    public ResponseEntity<Response> updateSetting(@Valid @RequestBody ShopSettingDto payload) {
        ShopSettingDto setting = shopSettingService.updateSetting(payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200", "success", "successfully updated settings", setting));
    }
}
