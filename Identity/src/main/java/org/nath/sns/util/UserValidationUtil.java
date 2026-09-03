package org.nath.sns.util;

import org.nath.sns.dto.AuthenticatedUser;

public class UserValidationUtil {

    public static void validateDifferentUser(AuthenticatedUser user, Long id) {
        if(user.getId()!=id) {
            throw new IllegalArgumentException("Not Allowed");
        }
    }
}
