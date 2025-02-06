package com.rj.ecommerce_backend.user.dtos;

import java.util.List;
import java.util.Map;

public record UserStatisticsDTO(
        long totalUsers,
        long activeUsers,
        long inactiveUsers,
        long newUsersThisMonth,
        Map<String, Long> roleDistribution,
        List<MonthlyRegistration> registrationsByMonth,
        LoginDistribution lastLoginDistribution,
        List<CountryStats> topCountries,
        VerificationStatus verificationStatus
) {
    public record MonthlyRegistration(String month, long count) {}
    public record LoginDistribution(
            long last24Hours,
            long lastWeek,
            long lastMonth,
            long inactive30Days
    ) {}
    public record CountryStats(String country, long count) {}
    public record VerificationStatus(long verified, long unverified) {}
}
