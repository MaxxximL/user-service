package ru.project;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public UserService(UserRepository userRepository, KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public UserDto create(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());

        User saved = userRepository.save(user);

        kafkaTemplate.send("user-events",
                new UserEvent("USER_CREATED", saved.getEmail(), saved.getName()));

        return toDto(saved);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        userRepository.deleteById(id);

        kafkaTemplate.send("user-events",
                new UserEvent("USER_DELETED", user.getEmail(), user.getName()));
    }

    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден : " + id));
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public UserDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        if (dto.getAge() != null) user.setAge(dto.getAge());

        return toDto(userRepository.save(user));
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}