package com.alibou.security.service;

import com.alibou.security.dto.AuthenticationRequest;
import com.alibou.security.dto.AuthenticationResponse;
import com.alibou.security.dto.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
