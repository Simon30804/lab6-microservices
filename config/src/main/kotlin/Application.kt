package config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

/**
 * Spring Cloud Config Server.
 *
 * This provides centralized configuration management for all microservices.
 * The @EnableConfigServer annotation:
 * - Enables the Config Server that serves configuration from external sources
 * - Supports multiple backends: Git repositories, file system (native), Vault, etc.
 * - Registers with Eureka as "configserver" so clients can discover it dynamically
 * - Serves configuration files like "accounts-service.yml" based on client's application name
 *
 * Benefits of Centralized Configuration:
 * - Single source of truth for all service configurations
 * - Environment-specific configs (dev, staging, prod)
 * - Version control of configuration changes
 * - Dynamic updates without redeploying services
 *
 * Startup Order: Start this AFTER Eureka, as it needs to register with Eureka.
 * Other services will discover this Config Server via Eureka.
 */
@SpringBootApplication
@EnableConfigServer
class ConfigServer

fun main(args: Array<String>) {
    runApplication<ConfigServer>(*args)
}