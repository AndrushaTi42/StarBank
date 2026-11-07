package com.star.bank.dto;

public record UserStats(
        long debitDeposit,
        long debitWithdraw,
        long savingDeposit,
        int debitCount,
        int creditCount,
        int investCount
) {
}
