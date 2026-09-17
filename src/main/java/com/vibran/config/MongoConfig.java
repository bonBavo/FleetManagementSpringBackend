package com.vibran.config;

import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.index.GeospatialIndex;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    // Create all indexes programmatically on startup
    // @CompoundIndexes on the document class handles most of them
    // TTL index must be created manually — Spring Data doesn't support it declaratively
    @PostConstruct
    public void createIndexes() {
        try {
            IndexOperations ops = mongoTemplate
                    .indexOps("gps_telemetry");

            // TTL index — auto-delete documents older than 90 days
            // MongoDB runs a background job every 60 seconds to clean expired docs
            ops.createIndex(new Index()
                    .on("timestamp", Sort.Direction.ASC)
                    .expire(90, TimeUnit.DAYS)
                    .named("idx_ttl_90days"));

            log.info("MongoDB indexes created successfully");

        } catch (Exception e) {
            log.error("Failed to create MongoDB indexes: {}", e.getMessage());
        }
    }
}

