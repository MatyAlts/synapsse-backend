package com.synapsse.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserProfileRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String firstName,
        @NotBlank(message = "El apellido es obligatorio")
        String lastName,
        String phone,
        String address,
        String city,
        String province,
        String postalCode
) {}
