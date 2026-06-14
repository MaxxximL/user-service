package ru.project;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@Import(TestMailConfig.class)
class NotificationServiceTest {

    @Container
    static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.6.0")
    );

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Autowired
    private GreenMail greenMail;

    @DynamicPropertySource
    static void setKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.value-serializer",
                () -> "org.springframework.kafka.support.serializer.JsonSerializer");
        registry.add("spring.kafka.consumer.value-deserializer",
                () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
        registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages", () -> "*");
        registry.add("spring.kafka.consumer.session.timeout.ms", () -> "15000");
    }

    @BeforeEach
    void setUp() throws InterruptedException, FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        Thread.sleep(2000);
    }

    @AfterEach
    void tearDown() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void whenUserCreated_thenSendWelcomeEmail() {
        UserEvent event = new UserEvent("USER_CREATED", "test@gmail.com", "Vasya");
        kafkaTemplate.send("user-events", event);

        await().atMost(50, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(greenMail.getReceivedMessages())
                            .as("No email received for USER_CREATED")
                            .hasSize(1);

                    MimeMessage msg = greenMail.getReceivedMessages()[0];
                    assertThat(msg.getSubject())
                            .as("Wrong subject")
                            .containsIgnoringCase("создан");
                });
    }

    @Test
    void whenUserDeleted_thenSendDeleteEmail() {
        UserEvent event = new UserEvent("USER_DELETED", "test@gmail.com", "John");
        kafkaTemplate.send("user-events", event);

        await().atMost(50, TimeUnit.SECONDS)
                .pollInterval(2, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(greenMail.getReceivedMessages())
                            .as("No email received for USER_DELETED")
                            .hasSize(1);

                    MimeMessage msg = greenMail.getReceivedMessages()[0];
                    assertThat(msg.getSubject())
                            .as("Wrong subject")
                            .containsIgnoringCase("удал");
                });
    }
}