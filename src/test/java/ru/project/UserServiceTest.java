package ru.project;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    void createUserTest() {
        provideInput("Вася\nvasya@test.ru\n30\n");

        doNothing().when(userDao).saveUser(any());

        userService = new UserService(userDao, new java.util.Scanner(System.in));
        userService.createUser();

        verify(userDao, times(1)).saveUser(any());
        assertTrue(outputStream.toString().contains("Пользователь успешно создан"));
    }

    @Test
    void getUserByIdTest() {
        User user = new User("Петя", "petya@test.ru", 35);
        user.setId(5L);
        when(userDao.getUserById(5L)).thenReturn(user);

        provideInput("5\n");

        userService = new UserService(userDao, new java.util.Scanner(System.in));
        userService.getUser();

        verify(userDao).getUserById(5L);
        assertTrue(outputStream.toString().contains("Петя"));
    }

    @Test
    void getAllUsersTest() {
        List<User> users = List.of(
                createTestUser(1L, "User A", "a@mail.ru"),
                createTestUser(2L, "User B", "b@mail.ru")
        );
        when(userDao.getAllUsers()).thenReturn(users);

        userService = new UserService(userDao, new java.util.Scanner(System.in));
        userService.getAllUsers();

        verify(userDao).getAllUsers();
        String output = outputStream.toString();
        assertTrue(output.contains("User A"));
        assertTrue(output.contains("User B"));
    }

    @Test
    void updateUserTest() {
        User existing = createTestUser(10L, "СтароеИмя", "old@mail.ru");
        when(userDao.getUserById(10L)).thenReturn(existing);
        doNothing().when(userDao).updateUser(any());

        provideInput("10\nНовоеИмя\nnew@mail.ru\n30\n");

        userService = new UserService(userDao, new java.util.Scanner(System.in));
        userService.updateUser();

        verify(userDao).updateUser(argThat(u ->
                "НовоеИмя".equals(u.getName()) && "new@mail.ru".equals(u.getEmail())
        ));
    }

    private User createTestUser(Long id, String name, String email) {
        User user = new User(name, email, 25);
        user.setId(id);
        return user;
    }
}