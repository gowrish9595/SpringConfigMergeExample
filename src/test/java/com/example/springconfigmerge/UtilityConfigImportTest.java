package com.example.springconfigmerge;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utility override using spring.config.additional-location — no Spring profiles.
 *
 * spring.config.additional-location inserts the named file BEFORE the default
 * application.yml in the property source chain, giving it higher priority.
 *
 * This is exactly what the utility JAR does at startup:
 *   java -jar utility.jar \
 *     --spring.config.additional-location=classpath:utility-overrides.yml
 *
 * Property source order (highest → lowest):
 *   1. utility-overrides.yml  → node.id=utility-node
 *   2. application.yml        → node.id=main-node, db.url=jdbc:a, db.user=admin, db.pool=10
 *
 * Resolution per key:
 *   node.id  → collision → utility-overrides.yml wins → "utility-node"
 *   db.url   → no collision → application.yml survives → "jdbc:a"
 *   db.user  → no collision → application.yml survives → "admin"
 *   db.pool  → no collision → application.yml survives → 10
 */
@SpringBootTest(properties = "spring.config.additional-location=classpath:utility-overrides.yml")
class UtilityConfigImportTest {

    @Autowired
    Environment env;

    @Autowired
    NodeProperties nodeProperties;

    @Autowired
    DbProperties dbProperties;

    @Test
    @DisplayName("node.id: utility-overrides.yml wins → utility-node (no profile needed)")
    void nodeId_overriddenByAdditionalLocation() {
        assertThat(env.getProperty("node.id")).isEqualTo("utility-node");
    }

    @Test
    @DisplayName("node.id via @ConfigurationProperties → utility-node")
    void nodeProperties_idOverridden() {
        assertThat(nodeProperties.getId()).isEqualTo("utility-node");
    }

    @Test
    @DisplayName("db.url: utility-overrides.yml says nothing about this key → main JAR value survives")
    void dbUrl_untouched() {
        assertThat(env.getProperty("db.url")).isEqualTo("jdbc:a");
    }

    @Test
    @DisplayName("db.user: utility-overrides.yml says nothing about this key → main JAR value survives")
    void dbUser_untouched() {
        assertThat(env.getProperty("db.user")).isEqualTo("admin");
    }

    @Test
    @DisplayName("db.pool: utility-overrides.yml says nothing about this key → main JAR value survives")
    void dbPool_untouched() {
        assertThat(env.getProperty("db.pool", Integer.class)).isEqualTo(10);
    }

    @Test
    @DisplayName("@ConfigurationProperties: node.id from utility-overrides, all db fields from application.yml")
    void allPropertiesBoundFromCorrectSources() {
        assertThat(nodeProperties.getId()).isEqualTo("utility-node");
        assertThat(dbProperties.getUrl()).isEqualTo("jdbc:a");
        assertThat(dbProperties.getUser()).isEqualTo("admin");
        assertThat(dbProperties.getPool()).isEqualTo(10);
    }
}
