package com.synapsse.backend.dto;

public record UserProfileResponse(
        String email,
        String firstName,
        String lastName,
        String phone,
        String address,
        String city,
        String province,
        String postalCode
) {}
