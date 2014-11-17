package streamflow.service;

import streamflow.service.RoleService;
import streamflow.datastore.core.RoleDao;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    @Mock
    public RoleDao roleDao;
    
    private RoleService roleService;
    
    @Before
    public void setUp() {
        roleService = new RoleService(roleDao);
    }
    
    @Test
    public void listRoles() {
        
    }
}
