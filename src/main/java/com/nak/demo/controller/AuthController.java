package com.nak.demo.controller;

import com.nak.demo.dto.auth.AuthDto;
import com.nak.demo.dto.auth.AuthResponseDto;
import com.nak.demo.dto.auth.RefreshTokenDto;
import com.nak.demo.dto.auth.RefreshTokenResponeDto;
import com.nak.demo.dto.base.Response;
import com.nak.demo.dto.user.UserDto;
import com.nak.demo.service.security.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody UserDto payload){
        AuthResponseDto dto = authService.register(payload);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("201","success","successfully registed user",dto));
    }
    @PostMapping("/login")
    public ResponseEntity<Response> login(@Valid @RequestBody AuthDto payload){
        AuthResponseDto dto = authService.login(payload);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully login",dto));
    }
    @PostMapping("/refresh")
    public ResponseEntity<Response> refreshToken (@Valid @RequestBody RefreshTokenDto payload) throws AuthenticationException {
        RefreshTokenResponeDto dto = authService.refreshToken(payload);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("200","success","successfully refreshed token ",dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout (@Valid @RequestBody RefreshTokenDto payload){
        authService.logout(payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully logged out"));
    }
}
