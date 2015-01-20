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
import org.mongodb.morphia.Datastore;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import streamflow.datastore.core.GenericDao;
import streamflow.datastore.core.UserDao;
import streamflow.datastore.ldap.LdapDao;
import streamflow.model.User;
import streamflow.model.util.Entity;

@Singleton
public class LdapUserDao extends LdapDao<User, String> 
        implements UserDao {
	 
    @Inject
    public LdapUserDao(IsisLdapRealm realm) {
        super(realm, User.class);
    }
    
    @Override
    public List<User> findAll() 
    {
    	List<User> users = new ArrayList<User>();
    	
    	try {
    		users = query("ou=users,dc=mach5,dc=lmco,dc=com", "(objectclass=lockheedPerson)");
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    		
    	return users;
    }
    
    public User findByCommonName(String cn)
    {
    	User user = null;
    	
		try {
			List<User> users = query("cn="+cn+",ou=users,dc=mach5,dc=lmco,dc=com", "(objectclass=lockheedPerson)");
			user = AssertSingleResult(users);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return user;
    }
    
    @Override
    public User findById(String id) {
    	User user = findByUsername(id);
    	return user;
    }
    
    @Override
    public User findByUsername(String uid) {
    	User user = null;
    	
    	try {
    		List<User> users = query("ou=users,dc=mach5,dc=lmco,dc=com", "(&(objectclass=lockheedPerson)(uid="+uid+"))");
    		user = AssertSingleResult(users);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return user;	
    }

    @Override
    public User findByEmail(String email) 
    {
    	User user = null;
    	
    	try {
    		List<User> users = query("email="+email+",ou=users,dc=mach5,dc=lmco,dc=com", "(objectclass=lockheedPerson)");
    		user = AssertSingleResult(users);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return user;
    }
   
    String extractAttributeIfExist(Attributes attributes, String key)
    {
    	String value = "";
    	
    	if (attributes.get(key) != null)
    	{
    		value = attributes.get(key).toString();
    		
    		// remove the key and colon from the value, Apache DS does this automatically
        	int pos = value.indexOf(": ");
        	if (pos > -1)
        	{
        		value = value.substring(pos+2);
        	}
    	}
    	
    	return value;
    }

	@Override
	protected User toObject(Attributes atts) {
	
        String uid        = extractAttributeIfExist(atts, "uid");
        String username   = extractAttributeIfExist(atts, "username");
        String pass       = extractAttributeIfExist(atts, "userPassword");
        String salt       = extractAttributeIfExist(atts, "passwordSalt");
        String first      = extractAttributeIfExist(atts, "firstName");
        String last       = extractAttributeIfExist(atts, "lastName");
        String email      = extractAttributeIfExist(atts, "email");
        String enabledStr = extractAttributeIfExist(atts, "enabled");
        String created    = extractAttributeIfExist(atts, "created");
        String modified   = extractAttributeIfExist(atts, "modified");
        String roleStr    = extractAttributeIfExist(atts, "roles");
        String cn         = extractAttributeIfExist(atts, "cn");
        String sn         = extractAttributeIfExist(atts, "sn");

        // roles are stored in ldap field as a serialized list of delimited values.  Roles can also be 
        // stored as an object type which can be iterated over to populate this list
        HashSet<String> roles = new HashSet<String>();
        String[] values = roleStr.split(",;");
        for (String value : values)
        {
        	roles.add(value);	
        }
        
        User user = new User();

        //user.setCreated(created);
        //user.setModified(modified);
        
        boolean enabled = Boolean.parseBoolean(enabledStr);
        user.setEmail(email);
        user.setEnabled(enabled);
        user.setFirstName(first);
        user.setId(uid);
        user.setLastName(last);
        user.setPassword(pass);
        user.setPasswordSalt(salt);
        user.setRoles(roles);
        user.setUsername(username);
        
        return user;
	}

	@Override
	protected List<Attributes> toEntity(User entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
