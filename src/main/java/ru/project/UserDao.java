package ru.project;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class UserDao {

    public void saveUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            System.out.println("Пользователь сохранён (ID: " + user.getId() + ")");
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Ошибка при rollback: " + rollbackEx.getMessage());
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Ошибка при сохранении пользователя", e);
        }
    }

    public User getUserById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    public void updateUser(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Ошибка при rollback: " + rollbackEx.getMessage());
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Ошибка при обновлении пользователя", e);
        }
    }

    public void deleteUser(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Ошибка при rollback: " + rollbackEx.getMessage());
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Ошибка при удалении пользователя", e);
        }
    }
}