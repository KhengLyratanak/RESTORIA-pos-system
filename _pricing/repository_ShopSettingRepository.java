package com.nak.demo.repository;

import com.nak.demo.entity.ShopSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopSettingRepository extends JpaRepository<ShopSetting, Long> {
}
