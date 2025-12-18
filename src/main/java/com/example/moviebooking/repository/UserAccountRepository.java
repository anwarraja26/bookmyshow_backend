package com.example.moviebooking.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.moviebooking.model.UserAccount;

public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
    Optional<UserAccount> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
