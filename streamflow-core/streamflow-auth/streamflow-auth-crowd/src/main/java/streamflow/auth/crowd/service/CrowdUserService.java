/*
 * Copyright 2015 cruzjf.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package streamflow.auth.crowd.service;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.service.client.CrowdClient;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import streamflow.auth.service.UserService;
import streamflow.model.Group;
import streamflow.model.PasswordChange;
import streamflow.model.User;

public class CrowdUserService implements UserService {
    
    private final CrowdClient crowdClient;
    
    @Inject
    public CrowdUserService(CrowdClient crowdClient) {
        this.crowdClient = crowdClient;
    }

    @Override
    public List<User> listUsers() {
        List<User> users = new ArrayList<User>();
        
        try {
            for (com.atlassian.crowd.model.user.User crowdUser :
                    crowdClient.searchUsers(NullRestrictionImpl.INSTANCE, 0, Integer.MAX_VALUE)) {
                
                users.add(crowdUserToUser(crowdUser));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return users;
    }

    @Override
    public User createUser(User user) {
        try {
            PasswordCredential credential = new PasswordCredential(user.getPassword());
            
            crowdClient.addUser(userToCrowdUser(user), credential);
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } 
    }

    @Override
    public User getUser(String userId) {
        User user = null;
        
        try {
            com.atlassian.crowd.model.user.User crowdUser = crowdClient.getUser(userId);
            user = crowdUserToUser(crowdUser);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        User user = null;
        
        try {
            List<com.atlassian.crowd.model.user.User> crowdUsers = 
                    crowdClient.searchUsers(Restriction.on(UserTermKeys.USERNAME)
                            .exactlyMatching(username), 0, 1);
            if (!crowdUsers.isEmpty()) {
                user = crowdUserToUser(crowdUsers.get(0));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = null;
        
        try {
            List<com.atlassian.crowd.model.user.User> crowdUsers = 
                    crowdClient.searchUsers(Restriction.on(UserTermKeys.EMAIL)
                            .exactlyMatching(email), 0, 1);
            if (!crowdUsers.isEmpty()) {
                user = crowdUserToUser(crowdUsers.get(0));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }

    @Override
    public void deleteUser(String userId) {
        try {
            crowdClient.removeUser(userId);
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    @Override
    public void updateUser(String userId, User user) {
        try {
            crowdClient.updateUser(userToCrowdUser(user));
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    @Override
    public List<Group> listGroupsWithUser(String userId) {
        List<Group> groups = new ArrayList<Group>();
        
        try {
            for (com.atlassian.crowd.model.group.Group crowdGroup : 
                    crowdClient.getGroupsForUser(userId, 0, Integer.MAX_VALUE)) {
                groups.add(CrowdGroupService.crowdGroupToGroup(crowdGroup));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        
        return groups;
    }

    @Override
    public void updateUserPassword(String userId, PasswordChange passwordChange) {
        try {
            crowdClient.updateUserCredential(userId, passwordChange.getNewPassword());
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    @Override
    public void updateUserPasswordForced(String userId, PasswordChange passwordChange) {
        try {
            crowdClient.updateUserCredential(userId, passwordChange.getNewPassword());
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }
    
    public static User crowdUserToUser(com.atlassian.crowd.model.user.User crowdUser) {
        User user = new User();
        user.setId(crowdUser.getExternalId());
        user.setUsername(crowdUser.getName());
        user.setEmail(crowdUser.getEmailAddress());
        user.setFirstName(crowdUser.getFirstName());
        user.setLastName(crowdUser.getLastName());
        user.setEnabled(crowdUser.isActive());
        return user;
    }
    
    public static com.atlassian.crowd.model.user.User userToCrowdUser(User user) {
        com.atlassian.crowd.model.user.User crowdUser = null;
        
        return crowdUser;
    }
}
