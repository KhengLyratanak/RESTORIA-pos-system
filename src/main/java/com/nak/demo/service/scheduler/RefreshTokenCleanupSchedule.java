package com.nak.demo.service.scheduler;


import com.nak.demo.service.security.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenCleanupSchedule {
@Autowired
    private RefreshTokenService refreshTokenService;
    @Scheduled(cron="0 0 */3 * * * ")
    public void cleanupExpiredAndRevokedToken(){
        //execute to clean up tokens
        refreshTokenService.deleteExpiredAndRevokedToken();

    }
}
