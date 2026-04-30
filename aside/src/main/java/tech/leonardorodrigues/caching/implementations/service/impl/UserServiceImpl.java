package tech.leonardorodrigues.caching.implementations.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tech.leonardorodrigues.caching.implementations.api.data.UserEmail;
import tech.leonardorodrigues.caching.implementations.api.data.UserRequest;
import tech.leonardorodrigues.caching.implementations.api.data.UserResponse;
import tech.leonardorodrigues.caching.implementations.entity.User;
import tech.leonardorodrigues.caching.implementations.repository.UserRepository;
import tech.leonardorodrigues.caching.implementations.service.UserService;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserServiceImpl implements UserService {

    private static final String CACHE_KEY_PREFIX = "user-email:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(15);
    private final MeterRegistry meterRegistry;

    private final UserRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserServiceImpl(MeterRegistry meterRegistry, UserRepository repository, RedisTemplate<String, Object> redisTemplate) {
        this.meterRegistry = meterRegistry;
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<UserResponse> getAll() {
        return repository.findAll().stream().map(UserResponse::toResponse).toList();
    }

    @Override
    public UserResponse save(UserRequest request) {
        var savedUser = repository.save(request.toEntity(request));
        return UserResponse.toResponse(savedUser);
    }

    @Override
    public UserEmail getUserEmail(UUID userId) {
        var key = cacheKey(userId);
        var cachedEmail = redisTemplate.opsForValue().get(key);

        if (cachedEmail != null) {
            meterRegistry.counter("cache.email.hit")
                    .increment();
            return new UserEmail(cachedEmail.toString(), true);
        }


        var email = slowDatabaseLookup(userId);
        redisTemplate.opsForValue().set(key, email, CACHE_TTL);
        meterRegistry.counter("cache.email.miss")
                .increment();
        return new UserEmail(email, false);
    }

    @Override
    public void updateUserEmail(UUID userId, String email) {
        var user = repository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        user.setEmail(email);

        //Using here Write + Delete strategy
        repository.save(user);
        redisTemplate.delete(cacheKey(userId));
    }


    private String slowDatabaseLookup(UUID id) {
        simulateRadomLatency();
        return repository.findById(id)
                .map(User::getEmail)
                .orElseThrow(() ->
                        new NoSuchElementException("Email not found for user id: " + id)
                );
    }

    private String cacheKey(UUID id) {
        return CACHE_KEY_PREFIX + id;
    }

    private void simulateRadomLatency() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(900, 1400));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
