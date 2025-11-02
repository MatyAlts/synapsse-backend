package com.synapsse.backend.dto;

public record AuthResponse(
        String token,
        String email,
        boolean isAdmin,
        String firstName,
        String lastName,
        String phone,
        String address,
        String city,
        String province,
        String postalCode
) {}
