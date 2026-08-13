package org.nath.sns.service;

import org.nath.sns.dao.UserDAO;
import org.nath.sns.entity.User;
import org.nath.sns.identity.model.CreateUserRequest;
import org.nath.sns.identity.model.SuccessResponse;
import org.nath.sns.identity.model.UpdateUserRequest;

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
    public org.nath.sns.identity.model.User createUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setEmail(createUserRequest.getEmail());
        User createdUser = userDAO.create(user);
        return convertToApiUser(createdUser);
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @return success response if deleted
     * @throws IllegalArgumentException if user not found
     */
    public SuccessResponse deleteUser(Long id) {
        User user = userDAO.findById(id);
        if (user == null) {
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
    public java.util.List<org.nath.sns.identity.model.User> getAllUsers() {
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
    public org.nath.sns.identity.model.User getUserById(Long id) {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        return convertToApiUser(user);
    }

    /**
     * Updates a user's information by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @param updateUserRequest the request containing updated username and email
     * @return the updated user
     * @throws IllegalArgumentException if user not found
     */
    public org.nath.sns.identity.model.User updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User existingUser = userDAO.findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        existingUser.setUsername(updateUserRequest.getUsername());
        existingUser.setEmail(updateUserRequest.getEmail());
        User updatedUser = userDAO.update(existingUser);
        return convertToApiUser(updatedUser);
    }

    /**
     * Converts a JPA User entity to an API User model.
     *
     * @param user the JPA entity
     * @return the API model
     */
    private org.nath.sns.identity.model.User convertToApiUser(User user) {
        org.nath.sns.identity.model.User apiUser = new org.nath.sns.identity.model.User();
        apiUser.setId(user.getId());
        apiUser.setUsername(user.getUsername());
        apiUser.setEmail(user.getEmail());
        return apiUser;
    }
}
