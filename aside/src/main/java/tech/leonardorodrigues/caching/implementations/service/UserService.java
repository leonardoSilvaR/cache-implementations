package tech.leonardorodrigues.caching.implementations.service;

import tech.leonardorodrigues.caching.implementations.api.data.UserEmail;
import tech.leonardorodrigues.caching.implementations.api.data.UserRequest;
import tech.leonardorodrigues.caching.implementations.api.data.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getAll();

    UserResponse save(UserRequest request);

    UserEmail getUserEmail(UUID userId);

    void updateUserEmail(UUID userId, String email);
}
