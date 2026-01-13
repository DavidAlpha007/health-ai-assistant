package com.healthai.repository;

import com.healthai.model.UserActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivationRepository extends JpaRepository<UserActivation, String> {
}
