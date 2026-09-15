package com.daniel.auth.services;

import com.daniel.auth.dto.LoginRequest;
import com.daniel.auth.dto.TokenResponse;

public interface AuthService {

    TokenResponse autenticar(LoginRequest request) throws Exception;
}

