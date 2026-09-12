package org.nath.sns.dto;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthenticatedUser implements Principal {
    private final Long id;
    private final String username;
    private final Set<String> roles;

    public AuthenticatedUser(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles != null ? new HashSet<>(roles) : Collections.emptySet();
    }

    public Long getId() { return id; }
    @Override public String getName() { return username; }
    public boolean hasRole(String role) { return roles.contains(role); }
    public Set<String> getRoles() { return Collections.unmodifiableSet(roles); }
}
