package com.star.bank.dto;

import java.util.UUID;

public record UserInfo(
        UUID id,
        String username,
        String firstName,
        String lastName
) {
}
