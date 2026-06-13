package com.example.springconfigmerge;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simulates a utility program that imports the main JAR and needs its own
 * node.id to avoid transaction collisions.
 *
 * Strategy: activate the "utility" profile, which provides application-utility.yml.
 * That file emits exactly one key — node.id=utility-node — and says nothing about
 * db.url / db.user / db.pool, so those survive from the main JAR's application.yml.
 *
 * Spring flattens both YAML files to independent dotted keys:
 *   main JAR emits: node.id=main-node  db.url=jdbc:a  db.user=admin  db.pool=10
 *   utility emits:  node.id=utility-node
 *
 *   node.id  → collision → utility wins → "utility-node"  (no more collision with main app)
 *   db.url   → no collision → main JAR value survives → "jdbc:a"
 *   db.user  → no collision → main JAR value survives → "admin"
 *   db.pool  → no collision → main JAR value survives → 10
 */
@SpringBootTest
@ActiveProfiles("utility")
class UtilityNodeOverrideTest {

    @Autowired
    Environment env;

    @Autowired
    NodeProperties nodeProperties;

    @Autowired
    DbProperties dbProperties;

    @Test
    @DisplayName("node.id: utility profile wins → utility-node (no collision with main app)")
    void nodeId_overriddenByUtilityProfile() {
        assertThat(env.getProperty("node.id")).isEqualTo("utility-node");
    }

    @Test
    @DisplayName("node.id via @ConfigurationProperties → utility-node")
    void nodeProperties_idOverridden() {
        assertThat(nodeProperties.getId()).isEqualTo("utility-node");
    }

    @Test
    @DisplayName("db.url: utility profile says nothing about this key → main JAR value survives")
    void dbUrl_untouched_mainJarValueSurvives() {
        assertThat(env.getProperty("db.url")).isEqualTo("jdbc:a");
    }

    @Test
    @DisplayName("db.user: utility profile says nothing about this key → main JAR value survives")
    void dbUser_untouched_mainJarValueSurvives() {
        assertThat(env.getProperty("db.user")).isEqualTo("admin");
    }

    @Test
    @DisplayName("db.pool: utility profile says nothing about this key → main JAR value survives")
    void dbPool_untouched_mainJarValueSurvives() {
        assertThat(env.getProperty("db.pool", Integer.class)).isEqualTo(10);
    }

    @Test
    @DisplayName("@ConfigurationProperties: all db fields bind from main JAR, node.id from utility")
    void allPropertiesBound_correctSources() {
        assertThat(nodeProperties.getId()).isEqualTo("utility-node");
        assertThat(dbProperties.getUrl()).isEqualTo("jdbc:a");
        assertThat(dbProperties.getUser()).isEqualTo("admin");
        assertThat(dbProperties.getPool()).isEqualTo(10);
    }
}
