package org.nath.sns.service;

import org.nath.sns.dao.UserDAO;
import org.nath.sns.entity.UserEntity;
import org.nath.sns.identity.model.User;
import org.nath.sns.identity.model.CreateUserRequest;
import org.nath.sns.identity.model.SuccessResponse;
import org.nath.sns.identity.model.UpdateUserRequest;

import org.mindrot.jbcrypt.BCrypt;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class UserService {

    private final UserDAO userDAO;

    @Inject
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Creates a new user from the provided request.
     *
     * @param createUserRequest the request containing username and email
     * @return the created user with generated ID
     */
    public User createUser(CreateUserRequest createUserRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(createUserRequest.getUsername());
        // Generate a random salt and hash the plaintext password (log_rounds = 12 recommended)
        String salt = BCrypt.gensalt(12);
        String hashedPassword = BCrypt.hashpw(createUserRequest.getPassword(), salt);
        userEntity.setPasswordHash(hashedPassword);
        userEntity.setEmail(createUserRequest.getEmail());
        UserEntity createdUserEntity = userDAO.create(userEntity);
        return convertToApiUser(createdUserEntity);
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @return success response if deleted
     * @throws IllegalArgumentException if user not found
     */
    public SuccessResponse deleteUser(Long id) {
        UserEntity userEntity = userDAO.findById(id);
        if (userEntity == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userDAO.delete(id);
        return new SuccessResponse().message("User deleted successfully");
    }

    /**
     * Retrieves a list of all users.
     *
     * @return list of all users
     */
    public java.util.List<User> getAllUsers() {
        return userDAO.findAll().stream()
                .map(this::convertToApiUser)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @return the user
     * @throws IllegalArgumentException if user not found
     */
    public User getUserById(Long id) {
        UserEntity userEntity = userDAO.findById(id);
        if (userEntity == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        return convertToApiUser(userEntity);
    }

    /**
     * Updates a user's information by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @param updateUserRequest the request containing updated username and email
     * @return the updated user
     * @throws IllegalArgumentException if user not found
     */
    public User updateUser(Long id, UpdateUserRequest updateUserRequest) {
        UserEntity existingUserEntity = userDAO.findById(id);
        if (existingUserEntity == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        if(updateUserRequest.getPassword()!=null) {
            // Generate a random salt and hash the plaintext password (log_rounds = 12 recommended)
            String salt = BCrypt.gensalt(12);
            String hashedPassword = BCrypt.hashpw(updateUserRequest.getPassword(), salt);
            existingUserEntity.setPasswordHash(hashedPassword);
        }
        existingUserEntity.setUsername(updateUserRequest.getUsername());
        existingUserEntity.setEmail(updateUserRequest.getEmail());
        UserEntity updatedUserEntity = userDAO.update(existingUserEntity);
        return convertToApiUser(updatedUserEntity);
    }

    /**
     * Converts a JPA User entity to an API User model.
     *
     * @param userEntity the JPA entity
     * @return the API model
     */
    private User convertToApiUser(UserEntity userEntity) {
        org.nath.sns.identity.model.User apiUser = new org.nath.sns.identity.model.User();
        apiUser.setId(userEntity.getId());
        apiUser.setUsername(userEntity.getUsername());
        apiUser.setEmail(userEntity.getEmail());
        return apiUser;
    }
}
