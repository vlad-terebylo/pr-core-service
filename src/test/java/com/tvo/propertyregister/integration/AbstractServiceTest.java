package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.TestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;

// TODO: 5/17/2025 to view and refactor all tests(including integration and unit)

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
abstract class AbstractServiceTest {
    protected static final MongoDBContainer MONGO_DB_CONTAINER =
            new MongoDBContainer("mongo:6.0");
    protected static final RabbitMQContainer RABBIT_MQ_CONTAINER =
            new RabbitMQContainer("rabbitmq:3.9-management");
}
