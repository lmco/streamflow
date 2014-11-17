package streamflow.service;

import streamflow.service.UserService;
import streamflow.datastore.core.UserDao;

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
public class UserServiceTest {

    @Mock
    public UserDao userDao;
    
    private UserService userService;
    
    @Before
    public void setUp() {
        userService = new UserService(userDao);
    }
    
    @Test
    public void listUsers() {
        
    }
}
