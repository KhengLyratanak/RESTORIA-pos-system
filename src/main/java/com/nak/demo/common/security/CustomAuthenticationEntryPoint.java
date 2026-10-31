package com.nak.demo.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nak.demo.dto.base.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
   @Autowired
   private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Response ErrorRespone = Response.error("401", "fail","Authentication required, Please provide a valid JWT token");

        response.getWriter().write(objectMapper.writeValueAsString(ErrorRespone));
    }
}
