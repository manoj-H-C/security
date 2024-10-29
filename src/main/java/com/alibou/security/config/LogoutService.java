package com.alibou.security.config;

import com.alibou.security.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        // Check jwt token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid token");
            return;
        }

        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);

        // Check if the token is null or already expired/revoked
        if (storedToken == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing Token");
            return;
        }

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token has already been logged out or is invalid");
            return;
        }

        // Mark the token as expired and revoked
        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);

        // Set response status and content type
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Logout successful\"}");
        } catch (IOException e) {
            // Handle exception (logging, etc.)
            e.printStackTrace();
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) {
        try {
            response.setStatus(statusCode);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + message + "\"}");
        } catch (IOException e) {
            // Handle exception (logging, etc.)
            e.printStackTrace();
        }
    }
}
