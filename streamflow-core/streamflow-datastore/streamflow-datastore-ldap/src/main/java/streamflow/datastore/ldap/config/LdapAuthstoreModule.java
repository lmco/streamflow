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
package streamflow.datastore.ldap.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mongodb.Mongo;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import streamflow.datastore.core.RoleDao;
import streamflow.datastore.core.UserDao;
import streamflow.datastore.ldap.impl.LdapRoleDao;
import streamflow.datastore.ldap.impl.LdapUserDao;
import streamflow.model.config.AuthConfig;
import streamflow.model.config.DatastoreConfig;

import org.apache.isis.security.shiro.IsisLdapContextFactory;
import org.apache.isis.security.shiro.IsisLdapRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.subject.Subject;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapAuthstoreModule extends AbstractModule {

    public static Logger LOG = LoggerFactory.getLogger(LdapAuthstoreModule.class);

    @Override
    protected void configure() {
        LOG.info("Initializing [LDAP Authstore] Module...");

        bind(RoleDao.class).to(LdapRoleDao.class);
        bind(UserDao.class).to(LdapUserDao.class);
    }
    
    @Provides
    public IsisLdapRealm IsisLdapRealm(DatastoreConfig datastoreConfig) {
        String dbName = datastoreConfig.getProperty("dbName", String.class);
        if (dbName == null || dbName.isEmpty()) {
            dbName = "streamflow";
        }
        //Datastore ds = new Morphia().createDatastore(mongo, dbName);

        IsisLdapContextFactory ldapFactory = new IsisLdapContextFactory();
        ldapFactory.setUrl("ldap://ldap.sec.lmco:10389");

        IsisLdapRealm ldapRealm = new IsisLdapRealm();
        ldapRealm.setContextFactory(ldapFactory);
        ldapRealm.setSearchBase("dc=mach5,dc=lmco,dc=com");
        ldapRealm.setGroupObjectClass("groupOfUniqueNames");
        ldapRealm.setUniqueMemberAttribute("uniqueMember");
        ldapRealm.setUniqueMemberAttributeValueTemplate("cn={0},ou=users,dc=mach5,dc=lmco,dc=com");
        ldapRealm.setUserDnTemplate("cn={0},ou=users,dc=mach5,dc=lmco,dc=com");

        Map<String, String> rolesbygroup = new HashMap<String, String>();
        rolesbygroup.put("user", "user_role");
        rolesbygroup.put("admin", "admin_role");
        ldapRealm.setRolesByGroup(rolesbygroup);

        String permissionsByRoleStr = 
        		"user_role = *:ToDoItemsJdo:*:*,*:ToDoItem:*:*; " +
		        "admin_role = *";
        ldapRealm.setPermissionsByRole(permissionsByRoleStr);
        
        LdapContextFactory ctx = null;
        return ldapRealm;
    }
}
