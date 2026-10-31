package com.nak.demo.service.security;


import com.nak.demo.dto.auth.AuthDto;
import com.nak.demo.dto.auth.AuthResponseDto;
import com.nak.demo.dto.auth.RefreshTokenDto;
import com.nak.demo.dto.auth.RefreshTokenResponeDto;
import com.nak.demo.dto.user.ChangePasswordDto;
import com.nak.demo.dto.user.UserDto;
import com.nak.demo.entity.RefreshToken;
import com.nak.demo.entity.User;
import com.nak.demo.exception.model.CustomAuthenticationException;
import com.nak.demo.exception.model.DuplicateException;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.mapper.UserMapper;
import com.nak.demo.repository.UserRepository;
import com.nak.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.Objects;


@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public AuthResponseDto register(UserDto payload){

        if(userRepository.existsByName(payload.getName())){
            throw new DuplicateException("user already existed");
        }

        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new DuplicateException("email already existed");
        }
            User user = mapper.toEntity(payload);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

     User createdUser = userRepository.save(user);
     String accessToken = jwtUtil.generateToken(createdUser);

     RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
     return new AuthResponseDto(accessToken,refreshToken.getToken());
    }

    public AuthResponseDto login(AuthDto payload){
     try{
         authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(payload.getUsername(),payload.getPassword())
         );
     }catch (BadCredentialsException e){
            throw new CustomAuthenticationException("invalid username or password");
     }

        UserDetails userDetails = userService.loadUserByUsername(payload.getUsername());
        String accessToken = jwtUtil.generateToken(userDetails);

        User user = (User) userDetails;
        RefreshToken refreshToken =  refreshTokenService.createRefreshToken(user);
        return new AuthResponseDto(accessToken,refreshToken.getToken());
    }

    public RefreshTokenResponeDto refreshToken(RefreshTokenDto payload) throws AuthenticationException {
        RefreshToken refreshToken = refreshTokenService.findByToken(payload.getRefreshToken());
        refreshToken = refreshTokenService.verifyToken(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateToken(user);
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);
        return new RefreshTokenResponeDto(newAccessToken, newRefreshToken.getToken(), "Bearer");
    }
    @Transactional
    public void changePassword(ChangePasswordDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("user not found with id :" + userId));
        if (!Objects.equals(user.getPassword(), dto.getOldPassword())) {
            throw new ResourceNotFoundException("old password is incorrect,please return the correct password");
        }
        if (!Objects.equals(dto.getNewPassword(), dto.getConfirmPassword())) {
            throw new ResourceNotFoundException("new password and confirm password must be the same");
        }
        mapper.updateEntityChangePassword(user, dto.getNewPassword());
        userRepository.save(user);

    }

    @Transactional
    public void logout(RefreshTokenDto payload){
        refreshTokenService.revoke(payload.getRefreshToken());
    }

    private void assertUnique(String name, String email) {
        if (userRepository.existsByNameIgnoreCase(name.trim())) {
            throw new DuplicateException("username already exists");
        }
        if (userRepository.existsByEmailIgnoreCase(email.trim())) {
            throw new DuplicateException("email already exists");
        }
    }

    private AuthResponseDto issueTokens(User user) {
        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponseDto(accessToken, refreshToken.getToken(), mapper.toDto(user));
    }

}
