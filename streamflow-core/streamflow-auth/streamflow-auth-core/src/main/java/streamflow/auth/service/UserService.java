package streamflow.auth.service;

import java.util.List;
import streamflow.model.Group;
import streamflow.model.PasswordChange;
import streamflow.model.Role;
import streamflow.model.User;

public interface UserService {

    List<User> listUsers();
    
    User createUser(User user);
    
    User getUser(String userId);
    
    User getUserByUsername(String username);
    
    User getUserByEmail(String email);
    
    void deleteUser(String userId);
    
    void updateUser(String userId, User user);
    
    List<Group> listGroupsWithUser(String userId);
    
    //List<Role> listRolesForUser(String userId);
    
    void updateUserPassword(String userId, PasswordChange passwordChange);
    
    void updateUserPasswordForced(String userId, PasswordChange passwordChange);
}
