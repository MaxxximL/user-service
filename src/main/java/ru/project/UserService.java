package ru.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserDao userDao;
    private final Scanner scanner;

    public UserService() {
        this(new UserDaoImpl(), new Scanner(System.in));
    }

    public UserService(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public UserService(UserDao userDao) {
        this(userDao, new Scanner(System.in));
    }

    public void start() {
        log.info("UserService запущен...");
        while (true) {
            printMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> getUser();
                    case 3 -> getAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 0 -> {
                        System.out.println("Выход из программы...");
                        HibernateUtil.shutdown();
                        return;
                    }
                    default -> System.out.println("Неверный выбор!");
                }
            } catch (Exception e) {
                log.error("Ошибка при обработке команды", e);
                System.out.println("Ошибка ввода! Попробуйте ещё раз.");
            }
        }
    }

    private void printMenu() {
        System.out.println("User Service");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти по ID");
        System.out.println("3. Показать всех");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    void createUser() {
        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine().trim();
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Возраст: ");
            Integer age = Integer.parseInt(scanner.nextLine().trim());

            User user = new User(name, email, age);
            userDao.saveUser(user);

            System.out.println("Пользователь успешно создан!");
        } catch (Exception e) {
            log.error("Ошибка создания пользователя", e);
            System.out.println("Ошибка при создании: " + e.getMessage());
        }
    }

    void getUser() {
        System.out.print("Введите ID: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        User user = userDao.getUserById(id);
        System.out.println(user != null ? user : "Пользователь не найден");
    }

    void getAllUsers() {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст");
        } else {
            users.forEach(System.out::println);
        }
    }

    void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        Long id = Long.parseLong(scanner.nextLine().trim());

        User user = userDao.getUserById(id);
        if (user == null) {
            System.out.println("Пользователь не найден");
            return;
        }

        System.out.print("Новое имя (текущее: " + user.getName() + "): ");
        String name = scanner.nextLine().trim();
        if (!name.isBlank()) user.setName(name);

        System.out.print("Новый email (текущий: " + user.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (!email.isBlank()) user.setEmail(email);

        System.out.print("Новый возраст (текущий: " + user.getAge() + "): ");
        String ageStr = scanner.nextLine().trim();
        if (!ageStr.isBlank()) {
            user.setAge(Integer.parseInt(ageStr));
        }

        userDao.updateUser(user);
        System.out.println("Пользователь обновлён");
    }

    void deleteUser() {
        System.out.print("ID пользователя для удаления: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        userDao.deleteUser(id);
        System.out.println("Пользователь удалён");
    }
}