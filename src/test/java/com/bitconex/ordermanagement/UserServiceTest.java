package com.bitconex.ordermanagement;

import com.bitconex.ordermanagement.administration.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService cut;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testGetUsers_Admin() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);

        List<User> users = new ArrayList<>();
        users.add(admin);

        when(userRepository.findAllByActiveIsTrue()).thenReturn(users);

        List<Object> result = cut.getUsers();

        assertTrue(result.get(0) instanceof AdminDTO);
    }
    @Test
    public void testGetUsers_Customer() {
        User customer = new User();
        customer.setRole(UserRole.CUSTOMER);

        Address address = new Address();
        address.setAddressId(2L);
        customer.setAddress(address);

        List<User> users = new ArrayList<>();
        users.add(customer);

        when(userRepository.findAllByActiveIsTrue()).thenReturn(users);

        List<Object> result = cut.getUsers();

        assertTrue(result.get(0) instanceof CustomerDTO);
    }


    @Test
    public void testGetUsersWithNoUsersInDB() {
        when(userRepository.findAllByActiveIsTrue()).thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> cut.getUsers());

        verify(userRepository, times(1)).findAllByActiveIsTrue();
    }

    @Test
    public void testAddNewUserWithValidData() {
        User user = new User();
        user.setUserName("admin_1");
        user.setPassword("password");

        when(userRepository.findUserByUserName(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded");

        assertDoesNotThrow(() -> cut.addNewUser(user));
        assertTrue(user.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testAddNewUserWithExistingUsername() {
        User existingUser = new User();
        existingUser.setUserName("existing");
        existingUser.setPassword("password");

        User newUser = new User();
        newUser.setUserName("existing");

        when(userRepository.findUserByUserName(newUser.getUsername())).thenReturn(Optional.of(existingUser));
        assertThrows(IllegalStateException.class, () -> cut.addNewUser(newUser));
    }

    @Test
    public void testAddNewUserWithNullUsername() {
        User newUser = new User();
        newUser.setPassword("password");
        assertThrows(IllegalArgumentException.class, () -> cut.addNewUser(newUser));
    }

    @Test
    public void testAddNewUserWithNullPassword() {
        User newUser = new User();
        newUser.setUserName("novikorisnik");
        assertThrows(IllegalArgumentException.class, () -> cut.addNewUser(newUser));

    }

    @Test
    public void testDeleteUserWithExistingUser() {
        String username = "user";
        User user = new User();
        user.setUserName(username);
        user.setActive(true);

        when(userRepository.findUserByUserName(username)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> cut.deleteUser(username));
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUserWithNoExistingUser() {
        String username = "nonexistent";

        when(userRepository.findUserByUserName(username)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> cut.deleteUser(username));
        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void testPrintAllUsersInJsonFormat() throws Exception {
        User user = new User();
        List<User> users = new ArrayList<>();
        users.add(user);
        Address address = new Address();
        user.setAddress(address);
        when(userRepository.findAllByActiveIsTrue()).thenReturn(users);
        cut.printAllUsersInJsonFormat();
        verify(userRepository, times(1)).findAllByActiveIsTrue();
        verify(objectMapper, times(1)).writeValueAsString(anyList());

    }
}
