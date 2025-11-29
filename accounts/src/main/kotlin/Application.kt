package accounts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Accounts microservice application.
 *
 * At startup, this service will:
 * 1. Register with Eureka (service discovery) using the name ACCOUNTS-SERVICE
 *    (from application.yml: spring.application.name)
 * 2. Fetch configuration from Config Server (discovered via Eureka)
 * 3. Start the embedded web server on the configured port (default: 3333)
 * 4. Begin accepting REST requests at /accounts endpoints
 *
 * Other services can discover this service by querying Eureka for ACCOUNTS-SERVICE.
 */
@SpringBootApplication
class AccountsServer

fun main(args: Array<String>) {
    runApplication<AccountsServer>(*args)
}
