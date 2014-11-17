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
import streamflow.datastore.core.RoleDao;
import streamflow.model.Role;
import streamflow.service.exception.EntityConflictException;
import streamflow.service.exception.EntityInvalidException;
import streamflow.service.exception.EntityNotFoundException;
import streamflow.service.util.IDUtils;

@Singleton
public class RoleService {

    private final RoleDao roleDao;

    @Inject
    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public List<Role> listRoles() {
        return roleDao.findAll();
    }

    public Role createRole(Role role) {
        if (role == null) {
            throw new EntityInvalidException("The provided role was NULL");
        }
        if (role.getName() == null) {
            throw new EntityInvalidException("The role was missing required fields");
        }
        if (hasRole(role.getName())) {
            throw new EntityConflictException(
                    "Role with the specified name already exists: Name = " + role.getName());
        }

        role.setId(IDUtils.formatId(role.getName()));
        role.setCreated(new Date());
        role.setModified(role.getCreated());

        return roleDao.save(role);
    }

    public Role getRole(String roleId) {
        Role role = roleDao.findById(roleId);
        if (role == null) {
            throw new EntityNotFoundException(
                    "Role with the specified ID could not be found: ID = " + roleId);
        }
        return role;
    }

    public boolean hasRole(String roleName) {
        return roleDao.findByName(roleName) != null;
    }

    public void deleteRole(String roleId) {
        roleDao.delete(getRole(roleId));
    }

    public void updateRole(String roleId, Role role) {
        Role oldRole = getRole(roleId);

        if (role == null) {
            throw new EntityInvalidException("The provided role was NULL");
        }
        if (role.getName() == null) {
            throw new EntityInvalidException("The role was missing required fields");
        }
        if (!oldRole.getName().equals(role.getName())) {
            if (hasRole(role.getName())) {
                throw new EntityConflictException(
                        "Role with the specified name already exists: Name = " + role.getName());
            }
        }

        role.setId(roleId);
        role.setCreated(oldRole.getCreated());
        role.setModified(new Date());

        roleDao.update(role);
    }
}
