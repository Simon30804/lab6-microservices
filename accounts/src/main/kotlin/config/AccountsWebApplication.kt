package accounts.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.logging.Logger
import javax.sql.DataSource

/**
 * Configuration class for the Accounts microservice.
 *
 * This class sets up the in-memory database for the demo.
 * In a real microservices architecture:
 * - Each service would have its own database (database per service pattern)
 * - Services don't share databases to maintain loose coupling
 * - Data consistency across services is handled via eventual consistency patterns
 * - This in-memory H2 database is suitable for demos but production would use
 *   a persistent database (PostgreSQL, MySQL, etc.)
 *
 * The database is populated with test data at startup for demonstration purposes.
 *
 * @author Paul Chapman
 */
@Configuration
@PropertySource("classpath:db-config.properties")
class AccountsWebApplication {
    private val logger = Logger.getLogger(
        AccountsWebApplication::class.java
            .name
    )

    /**
     * Creates an in-memory H2 database populated with test data.
     *
     * This demonstrates the "database per service" pattern:
     * - Each microservice owns its data
     * - No shared database between services (maintains independence)
     * - In-memory database is perfect for demos (data resets on restart)
     * - Production would use persistent storage with proper connection pooling
     *
     * The database schema and initial data are loaded from SQL scripts in
     * src/main/resources/testdb/
     */
    @Bean
    fun dataSource(): DataSource {
        logger.info("dataSource() invoked")

        // Create an in-memory H2 relational database containing some demo
        // accounts.
        val dataSource: DataSource = EmbeddedDatabaseBuilder()
            .addScript("classpath:testdb/schema.sql")
            .addScript("classpath:testdb/data.sql").build()
        logger.info("dataSource = $dataSource")

        // Sanity check
        val jdbcTemplate = JdbcTemplate(dataSource)
        val accounts: List<Map<String?, Any?>> = jdbcTemplate
            .queryForList("SELECT number FROM T_ACCOUNT")
        logger.info("System has " + accounts.size + " accounts")

        // Populate with random balances
        val rand = Random()
        for (item in accounts) {
            val number = item["number"] as String?
            val balance: BigDecimal = BigDecimal.valueOf(rand.nextInt(10000000) / 100.0)
                .setScale(2, RoundingMode.HALF_UP)
            jdbcTemplate.update(
                "UPDATE T_ACCOUNT SET balance = ? WHERE number = ?",
                balance, number
            )
        }
        return dataSource
    }
}