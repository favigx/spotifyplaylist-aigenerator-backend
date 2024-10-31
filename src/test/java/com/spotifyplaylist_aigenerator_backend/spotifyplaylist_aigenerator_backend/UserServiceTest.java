package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend;

import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.models.User;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.EncryptionService;
import com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private MongoOperations mongoOperations;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserByUsername_UserExists() {
        String username = "testUser";
        String userId = "1";
        String email = "test@example.com";
        String password = "password";
        String spotifyAccessToken = "someAccessToken";

        User expectedUser = new User(userId, email, username, password, null, spotifyAccessToken, false, 0, null);

        when(mongoOperations.findOne(new Query(Criteria.where("username").is(username)), User.class))
                .thenReturn(expectedUser);

        User actualUser = userService.getUserByUsername(username);

        assertEquals(expectedUser, actualUser);
        verify(mongoOperations, times(1)).findOne(new Query(Criteria.where("username").is(username)), User.class);
    }

    @Test
    public void testGetUserByUsername_UserDoesNotExist() {
        String username = "nonExistentUser";

        when(mongoOperations.findOne(new Query(Criteria.where("username").is(username)), User.class))
                .thenReturn(null);

        User actualUser = userService.getUserByUsername(username);

        assertNull(actualUser);
        verify(mongoOperations, times(1)).findOne(new Query(Criteria.where("username").is(username)), User.class);
    }
}