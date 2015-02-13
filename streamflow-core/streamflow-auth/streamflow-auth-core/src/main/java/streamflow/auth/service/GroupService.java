package streamflow.auth.service;

import java.util.List;
import streamflow.model.Group;
import streamflow.model.User;

public interface GroupService {
    
    List<Group> listGroups();
    
    Group createGroup(Group group);
    
    Group getGroup(String groupId);
    
    Group getGroupByName(String groupName);
    
    void deleteGroup(String groupId);
    
    void updateGroup(String groupId, Group group);
    
    List<User> listUsersForGroup(String groupId);
    
    void addUserToGroup(String groupId, String userId);
    
    void removeUserFromGroup(String groupId, String userId);
}
