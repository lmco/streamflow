/**
 * Copyright 2014 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package streamflow.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Date;
import java.util.List;
import streamflow.datastore.core.UserDao;
import streamflow.service.util.IDUtils;
import streamflow.model.PasswordChange;
import streamflow.model.User;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.util.CryptoUtils;

@Singleton
public class UserService {

    private final UserDao userDao;

    @Inject
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> listUsers() {
        return userDao.findAll();
    }

    public User createUser(User user) {
        if (user == null) {
            throw new EntityInvalidException("The provided user is NULL");
        }
        if (user.getUsername() == null || user.getEmail() == null
                || user.getFirstName() == null || user.getLastName() == null) {
            throw new EntityInvalidException("User is missing a required field");
        }
        if (getUserByUsername(user.getUsername()) != null) {
            throw new EntityConflictException(
                    "User with the specified username already exists: Username = " 
                            + user.getUsername());
        }
        if (getUserByEmail(user.getEmail()) != null) {
            throw new EntityConflictException(
                    "User with the specified email already exists: Email = " 
                            + user.getEmail());
        }

        if (user.getId() == null) {
            user.setId(IDUtils.randomUUID());
        }
        user.setCreated(new Date());
        user.setModified(user.getCreated());
        user.setPasswordSalt(CryptoUtils.generateSalt());
        user.setPassword(CryptoUtils.hashPassword(
                user.getPassword(), user.getPasswordSalt()));

        return userDao.save(user);
    }

    public User getUser(String userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException(
                    "User with the specified ID could not be found: ID = " + userId);
        }
        return user;
    }

    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public void deleteUser(String userId) {
        userDao.delete(getUser(userId));
    }

    public void updateUser(String userId, User user) {
        System.out.println("Updating user: " + user);
        
        User oldUser = getUser(userId);
        
        if (user == null) {
            throw new EntityInvalidException("The provided user was NULL");
        }
        if (user.getUsername() == null || user.getEmail() == null
                || user.getFirstName() == null || user.getLastName() == null) {
            throw new EntityInvalidException("The user was missing required fields");
        }
        if (!oldUser.getUsername().equals(user.getUsername())) {
            if (getUserByUsername(user.getUsername()) != null) {
                throw new EntityConflictException(
                        "User with the specified username already exists: Username = " 
                                + user.getUsername());
            }
        }
        if (!oldUser.getEmail().equals(user.getEmail())) {
            if (getUserByEmail(user.getEmail()) != null) {
                throw new EntityConflictException(
                        "User with the specified email already exists: Email = " 
                                + user.getEmail());
            }
        }
        
        System.out.println("User Old Password = " + oldUser.getPassword());
        System.out.println("User Old Password Salt = " + oldUser.getPasswordSalt());

        user.setId(userId);
        user.setModified(new Date());
        user.setCreated(oldUser.getCreated());
        user.setPassword(oldUser.getPassword());
        user.setPasswordSalt(oldUser.getPasswordSalt());

        userDao.update(user);
    }

    public void updateUserPassword(String userId, PasswordChange passwordChange) {
        User user = getUser(userId);
        
        if (passwordChange.getCurrentPassword() == null || passwordChange.getNewPassword() == null
                || passwordChange.getConfirmPassword() == null) {
            throw new EntityInvalidException(
                    "The password change request is missing required fields");
        }
        if (!passwordChange.getNewPassword().equals(passwordChange.getConfirmPassword())) {
            throw new EntityInvalidException(
                    "The new password and the confirmed password do not match");
        }

        String currentSalt = user.getPasswordSalt();
        String currentHashedPassword = CryptoUtils.hashPassword(
                passwordChange.getCurrentPassword(), currentSalt);

        if (!currentHashedPassword.equals(user.getPassword())) {
            throw new EntityInvalidException(
                    "The provided current password does not match actual password");
        }

        user.setModified(new Date());
        user.setPasswordSalt(CryptoUtils.generateSalt());
        user.setPassword(CryptoUtils.hashPassword(
                passwordChange.getNewPassword(), user.getPasswordSalt()));
        
        userDao.update(user);
    }

    public void updateUserPasswordForced(String userId, PasswordChange passwordChange) {
        User user = getUser(userId);

        if (!passwordChange.getNewPassword().equals(passwordChange.getConfirmPassword())) {
            throw new EntityInvalidException(
                    "The new password and the confirmed password do not match");
        }

        user.setModified(new Date());
        user.setPasswordSalt(CryptoUtils.generateSalt());
        user.setPassword(CryptoUtils.hashPassword(
                passwordChange.getNewPassword(), user.getPasswordSalt()));
        
        userDao.update(user);
    }
}
