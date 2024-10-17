package com.alibou.security.service.serviceImp;

import com.alibou.security.config.JwtService;
import com.alibou.security.dto.AuthenticationRequest;
import com.alibou.security.dto.AuthenticationResponse;
import com.alibou.security.dto.RegisterRequest;
import com.alibou.security.exception.ApiException;
import com.alibou.security.exception.ResourceNotFoundException;
import com.alibou.security.repository.UserRepository;
import com.alibou.security.service.AuthenticationService;
import com.alibou.security.user.Role;
import com.alibou.security.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new ApiException(HttpStatus.BAD_REQUEST,"Email is already in use"); // Custom exception
//        }
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new ResourceNotFoundException("email","email id",request.getEmail()));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
