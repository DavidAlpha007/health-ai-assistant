package com.healthai.service;

public interface UserService {

    /**
     * Activate a user by their user ID
     * @param userId the user ID
     * @return true if activation was successful, false otherwise
     */
    boolean activateUser(String userId);

    /**
     * Check if a user is activated
     * @param userId the user ID
     * @return true if user is activated, false otherwise
     */
    boolean isUserActivated(String userId);
}
