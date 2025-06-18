package com.tvo.propertyregister.integration;

import com.tvo.propertyregister.integration.config.TestConfig;
import com.tvo.propertyregister.integration.config.TestRedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;

import java.util.Objects;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({TestConfig.class, TestRedisConfig.class})
abstract class AbstractServiceTest {
    protected static final MongoDBContainer MONGO_DB_CONTAINER =
            new MongoDBContainer("mongo:6.0");
    protected static final RabbitMQContainer RABBIT_MQ_CONTAINER =
            new RabbitMQContainer("rabbitmq:3.9-management");

    protected static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>("redis:latest")
                    .withExposedPorts(6379);

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    protected void flushAllCache() {
        Set<String> keys = redisTemplate.keys("*");

        if (Objects.nonNull(keys) && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
