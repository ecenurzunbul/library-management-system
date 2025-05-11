package com.library.service;


import com.library.dto.UserDTO;
import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.service.impl.UserServiceImpl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.*;


import static com.library.constants.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceImplTest {


    @Mock
    private UserRepository userRepository;


    private UserServiceImpl userService;

    private PasswordEncoder passwordEncoder = new PasswordEncoder() {;
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return rawPassword.toString().equals(encodedPassword);
        }
    };

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }


    @Test
    void registerUser_ShouldSaveAndReturnUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");
        userDTO.setRole(User.Role.PATRON);
        userDTO.setContactDetails("123456789");
        userDTO.setPassword("somePassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(userDTO.getName());
        savedUser.setEmail(userDTO.getEmail());
        savedUser.setRole(userDTO.getRole());
        savedUser.setContactDetails(userDTO.getContactDetails());
        userDTO.setPassword("somePassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);


        User result = userService.registerUser(userDTO);


        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(User.Role.PATRON, result.getRole());
        assertEquals("123456789", result.getContactDetails());


        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void updateUser_ShouldUpdateAndReturnUser_WhenUserExists() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setRole(User.Role.PATRON);
        existingUser.setContactDetails("000000000");


        UserDTO userDTO = new UserDTO();
        userDTO.setName("New Name");
        userDTO.setEmail("new@example.com");
        userDTO.setRole(User.Role.LIBRARIAN);
        userDTO.setContactDetails("111111111");


        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));


        User updatedUser = userService.updateUser(userId, userDTO);


        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals("New Name", updatedUser.getName());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals(User.Role.LIBRARIAN, updatedUser.getRole());
        assertEquals("111111111", updatedUser.getContactDetails());


        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());


        UserDTO userDTO = new UserDTO();
        userDTO.setName("Name");
        userDTO.setEmail("email@example.com");
        userDTO.setRole(User.Role.PATRON);
        userDTO.setContactDetails("contact");


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, userDTO);
        });


        assertEquals(USER_NOT_FOUND.getMessage(), exception.getMessage());


        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any());
    }


    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);


        userService.deleteUser(userId);


        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }


    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(userId);
        });


        assertEquals(USER_NOT_FOUND.getMessage(), exception.getMessage());


        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }


    @Test
    void getUserById_ShouldReturnUser_WhenFound() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Name");


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        Optional<User> result = userService.getUserById(userId);


        assertTrue(result.isPresent());
        assertEquals("Name", result.get().getName());


        verify(userRepository, times(1)).findById(userId);
    }


    @Test
    void getUserById_ShouldReturnEmpty_WhenNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());


        Optional<User> result = userService.getUserById(1L);


        assertFalse(result.isPresent());


        verify(userRepository, times(1)).findById(1L);
    }


    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        users.add(user1);
        users.add(user2);


        when(userRepository.findAll()).thenReturn(users);


        List<User> result = userService.getAllUsers();


        assertEquals(2, result.size());
        assertEquals("User 1", result.get(0).getName());
        assertEquals("User 2", result.get(1).getName());


        verify(userRepository, times(1)).findAll();
    }
}
