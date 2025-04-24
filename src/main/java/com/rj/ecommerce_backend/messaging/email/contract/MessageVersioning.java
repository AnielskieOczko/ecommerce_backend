package com.rj.ecommerce_backend.messaging.email.contract;

/**
 * Utility class for handling message versioning across the email service.
 * This helps with maintaining backward compatibility and managing API changes.
 */
public final class MessageVersioning {
    
    /**
     * Current version of the messaging API
     */
    public static final String CURRENT_VERSION = "1.0";
    
    /**
     * Minimum supported version of the messaging API
     */
    public static final String MINIMUM_SUPPORTED_VERSION = "1.0";
    
    /**
     * Checks if the provided version is supported by the current implementation
     *
     * @param version The version to check
     * @return true if the version is supported, false otherwise
     */
    public static boolean isSupported(String version) {
        if (version == null || version.isEmpty()) {
            return false;
        }
        
        try {
            double versionNum = Double.parseDouble(version);
            double minVersion = Double.parseDouble(MINIMUM_SUPPORTED_VERSION);
            double currentVersion = Double.parseDouble(CURRENT_VERSION);
            
            return versionNum >= minVersion && versionNum <= currentVersion;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private MessageVersioning() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}
