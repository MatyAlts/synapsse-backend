package com.synapsse.backend.controller;

import com.synapsse.backend.dto.AuthResponse;
import com.synapsse.backend.dto.LoginRequest;
import com.synapsse.backend.dto.RegisterRequest;
import com.synapsse.backend.entity.Role;
import com.synapsse.backend.entity.User;
import com.synapsse.backend.security.JwtService;
import com.synapsse.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .toList());
        String token = jwtService.generateToken(userDetails, buildExtraClaims(user));
        return ResponseEntity.ok(buildAuthResponse(user, token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            User user = userService.findByEmail(request.email());
            String token = jwtService.generateToken((UserDetails) authentication.getPrincipal(), buildExtraClaims(user));
            return ResponseEntity.ok(buildAuthResponse(user, token));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    private Map<String, Object> buildExtraClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        return claims;
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRoles().contains(Role.ADMIN),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getAddress(),
                user.getCity(),
                user.getProvince(),
                user.getPostalCode()
        );
    }
}
