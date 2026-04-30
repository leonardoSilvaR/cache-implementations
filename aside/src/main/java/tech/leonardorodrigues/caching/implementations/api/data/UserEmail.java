package tech.leonardorodrigues.caching.implementations.api.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record UserEmail(
        String email,
        @JsonIgnore boolean isCached
) {

}
