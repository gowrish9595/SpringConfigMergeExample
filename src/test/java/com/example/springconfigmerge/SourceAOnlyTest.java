package com.example.springconfigmerge;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Baseline: only source A is active (no profile override).
 * All keys come from application.yml — this is what the main JAR sees.
 */
@SpringBootTest
class SourceAOnlyTest {

    @Autowired
    Environment env;

    @Autowired
    DbProperties db;

    @Autowired
    NodeProperties node;

    @Test
    @DisplayName("node.id comes from main JAR config")
    void nodeId_fromMainJar() {
        assertThat(env.getProperty("node.id")).isEqualTo("main-node");
    }

    @Test
    @DisplayName("db.url comes from source A")
    void dbUrl_fromA() {
        assertThat(env.getProperty("db.url")).isEqualTo("jdbc:a");
    }

    @Test
    @DisplayName("db.user comes from source A")
    void dbUser_fromA() {
        assertThat(env.getProperty("db.user")).isEqualTo("admin");
    }

    @Test
    @DisplayName("db.pool comes from source A")
    void dbPool_fromA() {
        assertThat(env.getProperty("db.pool", Integer.class)).isEqualTo(10);
    }

    @Test
    @DisplayName("@ConfigurationProperties binds all three fields from A")
    void configProperties_allThreeFieldsBound_fromA() {
        assertThat(db.getUrl()).isEqualTo("jdbc:a");
        assertThat(db.getUser()).isEqualTo("admin");
        assertThat(db.getPool()).isEqualTo(10);
    }
}
