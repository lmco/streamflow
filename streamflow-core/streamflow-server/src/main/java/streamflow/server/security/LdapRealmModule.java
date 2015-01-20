package streamflow.server.security;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

import org.apache.isis.security.shiro.IsisLdapContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import streamflow.service.RoleService;
import streamflow.service.UserService;

public class LdapRealmModule extends AbstractModule {
    public static Logger LOG = LoggerFactory.getLogger(LdapRealmModule.class);

    @Override
    protected void configure() {
        LOG.info("Initializing [LDAP Realm] Module...");

        //Credentials Matching is not necessary - the LDAP directory will do it automatically:
    	//bind(CredentialsMatcher.class).to(AllowAllCredentialsMatcher.class);
        
        // this did not work for accessing the ldap object within the user/role DAO's
        //bind(LdapContextFactory.class).to(IsisLdapContextFactory.class);
    }
     
    @Provides
    @Inject
    public LdapRealm provideInstance(UserService userService, RoleService roleService) {
    	
    	//Subject subject = SecurityUtils.getSubject();
    	
        IsisLdapContextFactory ldapFactory = new IsisLdapContextFactory();
        ldapFactory.setUrl("ldap://ldap.sec.lmco:10389");

        LdapRealm ldapRealm = new LdapRealm(userService, roleService);
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
        
        return ldapRealm;
    }
}
