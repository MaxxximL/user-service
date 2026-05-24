package ru.project;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("userdb")
            .withUsername("postgres")
            .withPassword("postgres");

    private UserDao userDao;

    @BeforeAll
    static void beforeAll() {
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());

        HibernateUtil.rebuildSessionFactory();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl();
        cleanDatabase();
    }

    private void cleanDatabase() {
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSaveUser() {
        User user = new User("John Connor", "john@mail.ru", 16);
        userDao.saveUser(user);
        assertNotNull(user.getId(), "ID пользователя должен быть сгенерирован");
    }

    @Test
    void testGetUserById() {
        User user = new User("Sarah Connor", "sarah@mail.ru", 35);
        userDao.saveUser(user);

        User found = userDao.getUserById(user.getId());

        assertNotNull(found);
        assertEquals("Sarah Connor", found.getName());
        assertEquals("sarah@mail.ru", found.getEmail());
    }

    @Test
    void testGetAllUsers() {
        userDao.saveUser(new User("User1", "u1@mail.ru", 30));
        userDao.saveUser(new User("User2", "u2@mail.ru", 35));

        List<User> users = userDao.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void testUpdateUser() {
        User user = new User("Old", "old@mail.ru", 20);
        userDao.saveUser(user);

        user.setName("New Name");
        user.setAge(33);
        userDao.updateUser(user);

        User updated = userDao.getUserById(user.getId());
        assertEquals("New Name", updated.getName());
        assertEquals(33, updated.getAge());
    }

    @Test
    void testDeleteUser() {
        User user = new User("DeleteMe", "delete@mail.ru", 40);
        userDao.saveUser(user);

        userDao.deleteUser(user.getId());

        assertNull(userDao.getUserById(user.getId()));
    }

    @AfterAll
    static void afterAll() {
        HibernateUtil.shutdown();
    }
}