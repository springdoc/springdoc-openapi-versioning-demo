package org.springdoc.demos.users.user;

public record User(
        Integer id,
        String name,
        String email

        // a lot more fields here
) {
}
