package ru.project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class UserService {
    private final UserDao userDao = new UserDao();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createUser();
                case 2 -> getUser();
                case 3 -> getAllUsers();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> {
                    System.out.println("Выход..");
                    HibernateUtil.shutdown();
                    return;
                }
                default -> System.out.println("Неверный выбор!");
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

    private void createUser() {
        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Возраст: ");
            Integer age = scanner.nextInt();
            scanner.nextLine();

            User user = new User(name, email, age);
            user.setCreatedAt(LocalDateTime.now());

            userDao.saveUser(user);
        } catch (Exception e) {
            System.out.println("Ошибка при создании пользователя: " + e.getMessage());
        }
    }

    private void getUser() {
        System.out.print("ID: ");
        Long id = scanner.nextLong();
        User user = userDao.getUserById(id);
        System.out.println(user != null ? user : "Пользователь не найден");
    }

    private void getAllUsers() {
        List<User> users = userDao.getAllUsers();
        users.forEach(System.out::println);
    }

    private void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        User user = userDao.getUserById(id);
        if (user == null) {
            System.out.println("Пользователь не найден");
            return;
        }

        System.out.print("Новое имя (" + user.getName() + "): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) user.setName(name);

        System.out.print("Новый email (" + user.getEmail() + "): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) user.setEmail(email);

        System.out.print("Новый возраст (" + user.getAge() + "): ");
        String ageStr = scanner.nextLine();
        if (!ageStr.isBlank()) user.setAge(Integer.parseInt(ageStr));

        userDao.updateUser(user);
        System.out.println("Пользователь обновлён");
    }

    private void deleteUser() {
        System.out.print("ID для удаления: ");
        Long id = scanner.nextLong();
        userDao.deleteUser(id);
        System.out.println("Пользователь удалён");
    }
}