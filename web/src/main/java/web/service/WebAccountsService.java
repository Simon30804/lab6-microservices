package web.service;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import web.model.Account;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service layer that encapsulates communication with the Accounts microservice.
 *
 * This class demonstrates service-to-service communication in
 * microservices architecture:
 * - Uses RestTemplate (configured with @LoadBalanced) to make HTTP calls
 * - Service discovery: The serviceUrl contains a logical name (e.g., "ACCOUNTS-SERVICE")
 *   that Eureka resolves to actual instance URLs
 * - Load balancing: When multiple instances exist, requests are automatically
 *   distributed across them
 * - Resilience: If one instance fails, Eureka routes requests to healthy instances
 *
 * This pattern hides the complexity of service discovery from the controller layer.
 *
 * @author Paul Chapman
 */
public class WebAccountsService {

    private final RestTemplate restTemplate;

    private final String serviceUrl;

    private final Logger logger = Logger.getLogger(WebAccountsService.class
            .getName());

    public WebAccountsService(String serviceUrl, RestTemplate restTemplate) {
        this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl
                : "http://" + serviceUrl;
        this.restTemplate = restTemplate;
    }

    /**
     * Educational method demonstrating how RestTemplate uses Eureka for service discovery.
     *
     * The RestTemplate works because it uses a custom request-factory
     * that integrates with Spring Cloud LoadBalancer (replacing Ribbon in newer versions).
     * When you make a request to a service name like "ACCOUNTS-SERVICE":
     * 1. Spring Cloud intercepts the request
     * 2. Queries Eureka for available instances
     * 3. Selects an instance (load balancing)
     * 4. Replaces the service name with the actual URL
     * 5. Makes the HTTP request
     *
     * This method logs the request factory to show that it's not a standard RestTemplate.
     * This method exists purely for educational purposes to demonstrate the integration.
     */
    @PostConstruct
    public void demoOnly() {
        // Can't do this in the constructor because the RestTemplate injection
        // happens afterwards.
        logger.warning("The RestTemplate request factory is "
                + restTemplate.getRequestFactory());
    }

    /**
     * Finds an account by account number by calling the Accounts microservice.
     *
     * This method demonstrates service-to-service communication:
     * - The serviceUrl (e.g., "http://ACCOUNTS-SERVICE") is resolved by Eureka
     * - RestTemplate automatically handles service discovery and load balancing
     * - If the Accounts service is unavailable, this will throw an exception
     *   (consider implementing a circuit breaker for production)
     *
     * @param accountNumber The 9-digit account number
     * @return The account if found
     */
    public Account findByNumber(String accountNumber) {

        logger.info("findByNumber() invoked: for " + accountNumber);
        return restTemplate.getForObject(serviceUrl + "/accounts/{number}",
                Account.class, accountNumber);
    }

    /**
     * Finds accounts by owner name (partial match) by calling the Accounts microservice.
     *
     * This method shows error handling in microservices:
     * - Catches HttpClientErrorException (404) when no accounts are found
     * - Returns null instead of throwing, allowing graceful handling
     * - Demonstrates that service failures need to be handled explicitly
     *
     * @param name Partial owner name to search for
     * @return List of matching accounts, or null if none found
     */
    public List<Account> byOwnerContains(String name) {
        logger.info("byOwnerContains() invoked:  for " + name);
        Account[] accounts = null;

        try {
            accounts = restTemplate.getForObject(serviceUrl
                    + "/accounts/owner/{name}", Account[].class, name);
        } catch (HttpClientErrorException e) { // 404
            // Nothing found
        }

        if (accounts == null || accounts.length == 0) {
            return null;
        } else {
            return Arrays.asList(accounts);
        }
    }
}
