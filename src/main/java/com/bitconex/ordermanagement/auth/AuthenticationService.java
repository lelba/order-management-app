package com.bitconex.ordermanagement.auth;

import com.bitconex.ordermanagement.administration.user.UserRepository;
import com.bitconex.ordermanagement.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
       authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
       var user = userRepository.findUserByUserName(request.getUsername()).orElse(null);
       var jwtToken = jwtService.generateToken(user);
       return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
