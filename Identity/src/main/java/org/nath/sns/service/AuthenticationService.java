package org.nath.sns.service;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.nath.sns.dao.UserDAO;
import org.nath.sns.dao.UserRoleDAO;
import org.nath.sns.entity.UserEntity;
import org.nath.sns.identity.model.LoginRequest;
import org.nath.sns.identity.model.LoginResponse;

import java.util.List;
import java.util.Optional;

@Slf4j
public class AuthenticationService {

    private final UserDAO userDAO;
    private final UserRoleDAO userRoleDAO;
    private final JwtTokenService tokenService;

    @Inject
    public AuthenticationService(UserDAO userDAO, UserRoleDAO userRoleDAO, JwtTokenService tokenService) {
        this.userDAO = userDAO;
        this.userRoleDAO = userRoleDAO;
        this.tokenService = tokenService;
    }

    public Optional<UserEntity> validatCredentials(LoginRequest loginRequest) {
        Optional<UserEntity> user = userDAO.findByUsername(loginRequest.getUsername());
        log.debug("user.isEmpty():"+user.isEmpty());
        if (user.isEmpty() || !BCrypt.checkpw(loginRequest.getPassword(), user.get().getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password or User not found");
        }
        return user;
    }


    public LoginResponse generateToken(UserEntity user) {
        List<String> roles = userRoleDAO.findRoleNamesByUserId(user.getId());

        String token = tokenService.generateToken(user.getId(), user.getUsername(), roles);
        return new LoginResponse().accessToken(token);
    }

}
