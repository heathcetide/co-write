package com.cowrite.project.config;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.cowrite.project.config.RabbitMQConfig.EXCHANGE_EVENTS;

@org.springframework.context.annotation.Configuration
@ConditionalOnProperty(prefix = "spring.debezium", name = "enabled", havingValue = "true")
public class CdcDebeziumEngine implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(CdcDebeziumEngine.class);

    private final DebeziumEngine<io.debezium.engine.ChangeEvent<String, String>> engine;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "debezium-engine"));

    public CdcDebeziumEngine(DebeziumEngine<io.debezium.engine.ChangeEvent<String, String>> engine) {
        this.engine = engine;
    }

    @Bean
    public DebeziumEngine<io.debezium.engine.ChangeEvent<String, String>> debeziumEngine(RabbitTemplate rabbitTemplate,
                                                                                         @Value("${spring.debezium.name}") String name,
                                                                                         @Value("${spring.debezium.offset.storage.file.filename}") String offsetFile,
                                                                                         @Value("${spring.debezium.history.file.filename}") String historyFile,
                                                                                         @Value("${spring.debezium.connector.class}") String connectorClass,
                                                                                         @Value("${spring.debezium.database.hostname}") String dbHost,
                                                                                         @Value("${spring.debezium.database.port}") String dbPort,
                                                                                         @Value("${spring.debezium.database.user}") String dbUser,
                                                                                         @Value("${spring.debezium.database.password}") String dbPassword,
                                                                                         @Value("${spring.debezium.database.server.id}") String serverId,
                                                                                         @Value("${spring.debezium.database.server.name}") String serverName,
                                                                                         @Value("${spring.debezium.include.schema.changes}") String includeSchema,
                                                                                         @Value("${spring.debezium.database.include.list}") String dbInclude,
                                                                                         @Value("${spring.debezium.table.include.list}") String tableInclude) {

        Properties props = new Properties();
        props.setProperty("name", name);
        props.setProperty("connector.class", connectorClass);
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", offsetFile);
        props.setProperty("offset.flush.interval.ms", "1000");
        props.setProperty("database.hostname", dbHost);
        props.setProperty("database.port", dbPort);
        props.setProperty("database.user", dbUser);
        props.setProperty("database.password", dbPassword);
        props.setProperty("database.server.id", serverId);
        props.setProperty("database.server.name", serverName);
        props.setProperty("include.schema.changes", includeSchema);
        props.setProperty("database.include.list", dbInclude);
        props.setProperty("table.include.list", tableInclude);
        props.setProperty("database.history", "io.debezium.relational.history.FileDatabaseHistory");
        props.setProperty("database.history.file.filename", historyFile);

        Configuration config = Configuration.from(props);

        return DebeziumEngine.create(Json.class)
                .using(config.asProperties())
                .notifying(record -> {
                    try {
                        String topic = record.destination();
                        // 将 CDC 事件统一转发到交换机，路由键如 cdc.<topic>
                        String routingKey = "cdc." + topic.replace('.', '_');
                        rabbitTemplate.convertAndSend(EXCHANGE_EVENTS, routingKey, record.value());
                    } catch (Exception e) {
                        log.error("CDC 事件转发失败: {}", record, e);
                    }
                })
                .build();
    }

    @Override
    public void start() {
        executor.submit(engine);
        log.info("Debezium CDC 引擎已启动");
    }

    @Override
    public void stop() {
        try {
            engine.close();
        } catch (Exception ignored) {}
        executor.shutdownNow();
        log.info("Debezium CDC 引擎已停止");
    }

    @Override
    public boolean isRunning() {
        return !executor.isShutdown();
    }
}


