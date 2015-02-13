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

import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.service.client.CrowdClient;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import streamflow.auth.service.GroupService;
import streamflow.model.Group;
import streamflow.model.User;


public class CrowdGroupService implements GroupService {
    
    private final CrowdClient crowdClient;
    
    @Inject
    public CrowdGroupService(CrowdClient crowdClient) {
        this.crowdClient = crowdClient;
    }

    @Override
    public List<Group> listGroups() {
        List<Group> groups = new ArrayList<Group>();
        try {
            for (com.atlassian.crowd.model.group.Group crowdGroup :
                    crowdClient.searchGroups(NullRestrictionImpl.INSTANCE, 0, Integer.MAX_VALUE)) {
                groups.add(crowdGroupToGroup(crowdGroup));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return groups;
    }

    @Override
    public Group createGroup(Group group) {
        try {
            crowdClient.addGroup(groupToCrowdGroup(group));
            return group;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } 
    }

    @Override
    public Group getGroup(String groupId) {
        Group group = null;
        try {
            com.atlassian.crowd.model.group.Group crowdGroup = crowdClient.getGroup(groupId);
            group = crowdGroupToGroup(crowdGroup);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return group;
    }

    @Override
    public Group getGroupByName(String groupName) {
        Group group = null;
        try {
            List<com.atlassian.crowd.model.group.Group> crowdGroups = 
                    crowdClient.searchGroups(Restriction.on(GroupTermKeys.NAME)
                            .exactlyMatching(groupName), 0, 1);
            if (!crowdGroups.isEmpty()) {
                group = crowdGroupToGroup(crowdGroups.get(0));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return group;
    }

    @Override
    public void deleteGroup(String groupId) {
        try {
            crowdClient.removeGroup(groupId);
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    @Override
    public void updateGroup(String groupId, Group group) {
        try {
            crowdClient.updateGroup(groupToCrowdGroup(group));
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    @Override
    public List<User> listUsersForGroup(String groupId) {
        List<User> users = new ArrayList<User>();
        try {
            for (com.atlassian.crowd.model.user.User crowdUser : 
                    crowdClient.getUsersOfGroup(groupId, 0, Integer.MAX_VALUE)) {
                users.add(CrowdUserService.crowdUserToUser(crowdUser));
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        
        return users;
    }

    @Override
    public void addUserToGroup(String groupId, String userId) {
        try {
            crowdClient.addUserToGroup(groupId, userId);
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    @Override
    public void removeUserFromGroup(String groupId, String userId) {
        try {
            crowdClient.removeUserFromGroup(groupId, userId);
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }
    
    public static Group crowdGroupToGroup(com.atlassian.crowd.model.group.Group crowdGroup) {
        Group group = new Group();
        group.setId(crowdGroup.getName());
        group.setName(crowdGroup.getName());
        group.setDescription(crowdGroup.getDescription());
        group.setEnabled(crowdGroup.isActive());
        return group;
    }
    
    public static com.atlassian.crowd.model.group.Group groupToCrowdGroup(Group group) {
        com.atlassian.crowd.model.group.Group crowdGroup = null;
        
        return crowdGroup;
    }
}
