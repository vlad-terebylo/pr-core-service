package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.TestMongoConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMongoConfig.class)
abstract class AbstractServiceTest {
    protected static final MongoDBContainer MONGO_DB_CONTAINER =
            new MongoDBContainer("mongo:6.0");
    protected static final RabbitMQContainer RABBIT_MQ_CONTAINER =
            new RabbitMQContainer("rabbitmq:3.9-management");
}
