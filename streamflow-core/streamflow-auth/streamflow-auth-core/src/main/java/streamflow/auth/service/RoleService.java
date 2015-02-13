package streamflow.auth.service;

import java.util.List;
import streamflow.model.Role;
import streamflow.model.User;

public interface RoleService {

    List<Role> listRoles();
    
    Role createRole(Role role);
    
    Role getRole(String roleId);
    
    Role getRoleByName(String roleName);
    
    void deleteRole(String roleId);
    
    void updateRole(String roleId, Role user);
    
    List<User> listUsersWithRole(String roleId);
}
