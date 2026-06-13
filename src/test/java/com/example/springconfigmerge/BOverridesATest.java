package com.example.springconfigmerge;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Source B (profile "b") is layered on top of source A.
 *
 * Spring flattens both YAML files to independent dotted keys before resolving:
 *   A emits: db.url=jdbc:a  db.user=admin  db.pool=10
 *   B emits: db.url=jdbc:b   (that's it — no null entries for user or pool)
 *
 * Resolution per key:
 *   db.url  → collision → B wins → "jdbc:b"
 *   db.user → no collision → A survives → "admin"
 *   db.pool → no collision → A survives → 10
 */
@SpringBootTest
@ActiveProfiles("b")
class BOverridesATest {

    @Autowired
    Environment env;

    @Autowired
    DbProperties db;

    // --- Environment.getProperty() checks ---

    @Test
    @DisplayName("db.url: B wins the collision → jdbc:b")
    void dbUrl_bWinsCollision() {
        assertThat(env.getProperty("db.url")).isEqualTo("jdbc:b");
    }

    @Test
    @DisplayName("db.user: no entry in B, A survives → admin")
    void dbUser_notTouchedByB_aValueSurvives() {
        // B never emitted db.user=null; it said nothing about this key.
        assertThat(env.getProperty("db.user")).isEqualTo("admin");
    }

    @Test
    @DisplayName("db.pool: no entry in B, A survives → 10")
    void dbPool_notTouchedByB_aValueSurvives() {
        // B never emitted db.pool=null; it said nothing about this key.
        assertThat(env.getProperty("db.pool", Integer.class)).isEqualTo(10);
    }

    // --- @ConfigurationProperties binding checks ---

    @Test
    @DisplayName("@ConfigurationProperties: all three fields bind — url from B, user+pool from A")
    void configProperties_allThreeFieldsBound_urlFromB_restFromA() {
        // Each field binds independently from its own dotted key.
        // There is no "db object" that B replaces wholesale.
        assertThat(db.getUrl()).isEqualTo("jdbc:b");
        assertThat(db.getUser()).isEqualTo("admin");
        assertThat(db.getPool()).isEqualTo(10);
    }
}
