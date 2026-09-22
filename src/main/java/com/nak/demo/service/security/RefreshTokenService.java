package com.nak.demo.service.security;

import com.nak.demo.common.config.ApplicationConfiguration;
import com.nak.demo.entity.RefreshToken;
import com.nak.demo.entity.User;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.repository.RefreshTokenRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.naming.AuthenticationException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

   @Autowired
   private ApplicationConfiguration appConfig;

   private long expiration;

   @PostConstruct
   private void init(){
       this.expiration = appConfig.getSecurity().getRefreshTokenExpiration();
   }
    public RefreshToken createRefreshToken(User user) {

        String refreshToken = generateSecureRefreshToken();


        RefreshToken entity = new RefreshToken();

        entity.setToken(refreshToken);
        entity.setExpiresAt(LocalDateTime.now().plusHours(expiration));
        entity.setUser(user);

        return refreshTokenRepository.save(entity);
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token is invalid"));
    }

    public RefreshToken verifyToken(RefreshToken token) throws AuthenticationException {
        if (!token.isValid()) {
            refreshTokenRepository.delete(token);
            throw new AuthenticationException("Refresh token was expired or revoked");
        }
        return token;
    }

    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        // revoke old rf token
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        //generate new rf token
        return this.createRefreshToken(oldToken.getUser());
    }
    public String generateSecureRefreshToken(){
        SecureRandom random = new SecureRandom();

        byte[] tokenBytes = new byte[64];

        random.nextBytes(tokenBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
