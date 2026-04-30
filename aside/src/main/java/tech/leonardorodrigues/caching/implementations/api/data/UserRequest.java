package tech.leonardorodrigues.caching.implementations.api.data;

import tech.leonardorodrigues.caching.implementations.entity.User;

public record UserRequest(
        String name,
        int age,
        String email
) {

    public User toEntity(UserRequest request) {
        var user = new User();
        user.setName(request.name);
        user.setAge(request.age);
        user.setEmail(request.email);
        return user;
    }
}