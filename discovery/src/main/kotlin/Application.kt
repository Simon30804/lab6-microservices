package registration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

/**
 * Eureka Service Discovery Server.
 *
 * This is the service registry for the microservices architecture.
 * The @EnableEurekaServer annotation:
 * - Starts a Eureka server that maintains a registry of all available services
 * - Provides a web dashboard at http://localhost:8761
 * - Handles service registration: Services register themselves at startup
 * - Handles service discovery: Clients query Eureka to find service instances
 * - Manages health: Monitors heartbeats and removes dead instances
 *
 * Startup Order: This must be started FIRST, as other services depend on it
 * for service discovery and configuration server discovery.
 */
@EnableEurekaServer
@SpringBootApplication
class EurekaServer

fun main(args: Array<String>) {
    runApplication<EurekaServer>(*args)
}