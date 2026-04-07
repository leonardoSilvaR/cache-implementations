package tech.leonardorodrigues.caching.api;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tech.leonardorodrigues.caching.api.data.UserEmail;
import tech.leonardorodrigues.caching.api.data.UserRequest;
import tech.leonardorodrigues.caching.api.data.UserResponse;
import tech.leonardorodrigues.caching.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> retrieveUsers() {
        return ResponseEntity.ok(userService.getAll());
    }


    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody UserRequest request, HttpServletRequest httpRequest) {
        var savedUser = userService.save(request);
        return ResponseEntity.created(
                UriComponentsBuilder
                        .fromUri(URI.create(httpRequest.getRequestURL().toString()))
                        .path("/{id}")
                        .buildAndExpand(savedUser.id())
                        .toUri()
        ).build();
    }

    @GetMapping("/{id}/email")
    public ResponseEntity<UserEmail> retrieveUserEmail(@PathVariable String id, HttpServletRequest httpRequest) {
        var email = userService.getUserEmail(UUID.fromString(id));

        var responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Cache", email.isCached() ? "hit" : "miss");

        return new ResponseEntity<>(email, responseHeaders, 200);
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<UserEmail> updateUserEmail(@PathVariable String id, @RequestBody UserEmail body) {
        userService.updateUserEmail(UUID.fromString(id), body.email());
        return ResponseEntity.noContent().build();
    }
}
