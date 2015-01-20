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
package streamflow.datastore.ldap.impl;

import org.apache.isis.security.shiro.IsisLdapRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

import javax.naming.directory.Attributes;

import streamflow.datastore.core.RoleDao;
import streamflow.datastore.ldap.LdapDao;
import streamflow.model.Role;

@Singleton
public class LdapRoleDao extends LdapDao<Role, String>
        implements RoleDao {

    @Inject
    public LdapRoleDao(IsisLdapRealm ctx) {
        super(ctx, Role.class);
    }

    @Override
    public List<Role> findAll() {
        return null;
    }

	@Override
	public Role findByName(String name) {
		return null;
	}

	@Override
	protected Role toObject(Attributes entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Attributes> toEntity(Role entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
