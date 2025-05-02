package com.rj.ecommerce_backend.testutil;

import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Helper class for common test operations
 */
@ActiveProfiles("test")
public class TestHelper {

    /**
     * Compares two LocalDateTime objects with truncated nanoseconds
     * @param dateTime1 First LocalDateTime
     * @param dateTime2 Second LocalDateTime
     * @return true if the dates are equal when truncated to seconds
     */
    public static boolean dateTimesEqualToSecond(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null || dateTime2 == null) {
            return dateTime1 == dateTime2;
        }
        return dateTime1.truncatedTo(ChronoUnit.SECONDS)
                .isEqual(dateTime2.truncatedTo(ChronoUnit.SECONDS));
    }
    
    /**
     * Generates a random email address for testing
     * @return A random email address
     */
    public static String randomEmail() {
        return "user" + System.currentTimeMillis() + "@example.com";
    }
}
