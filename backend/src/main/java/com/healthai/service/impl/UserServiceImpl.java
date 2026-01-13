package com.healthai.service.impl;

import com.healthai.model.UserActivation;
import com.healthai.repository.UserActivationRepository;
import com.healthai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserActivationRepository userActivationRepository;

    @Override
    @Transactional
    public boolean activateUser(String userId) {
        logger.debug("Activating user: {}", userId);
        
        // Find existing user activation record or create new one
        UserActivation userActivation = userActivationRepository.findById(userId)
                .orElseGet(() -> {
                    UserActivation newUserActivation = new UserActivation();
                    newUserActivation.setUserId(userId);
                    return newUserActivation;
                });
        
        // If already activated, return true
        if (Boolean.TRUE.equals(userActivation.getActivated())) {
            logger.debug("User already activated: {}", userId);
            return true;
        }
        
        // Activate the user
        userActivation.setActivated(true);
        userActivation.setActivateTime(LocalDateTime.now());
        
        // Save updated user activation
        userActivationRepository.save(userActivation);
        
        logger.debug("User activated successfully: {}", userId);
        return true;
    }

    @Override
    public boolean isUserActivated(String userId) {
        logger.debug("Checking activation status for user: {}", userId);
        
        return userActivationRepository.findById(userId)
                .map(UserActivation::getActivated)
                .orElse(false);
    }
}
