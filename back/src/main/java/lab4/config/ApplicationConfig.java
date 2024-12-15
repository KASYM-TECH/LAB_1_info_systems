package lab4.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.ValidationMode;
import lab4.security.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@ComponentScan("lab4")
@EnableWebMvc
@EnableWebSecurity
@EnableJpaRepositories(basePackages = "lab4.database.repository")
@PropertySource("classpath:hibernate.cfg")
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class ApplicationConfig implements WebMvcConfigurer {
    private final int COOKIE_MAX_AGE = 3600;

    @Value("${db_username}")
    private String USERNAME;

    @Value("${password}")
    private String PASSWORD;

    @Value("${driver}")
    private String DB_DRIVER;

    @Value("${url}")
    private String DB_URL;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true).maxAge(COOKIE_MAX_AGE)
                .allowedHeaders("*");
    }

    @Bean
    public DataSource dataSource() {
        // HikariCP Configuration
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(DB_DRIVER);
        config.setJdbcUrl(DB_URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);

        // Connection pool limits
        config.setMinimumIdle(5);              // Minimum idle connections
        config.setMaximumPoolSize(100);         // Maximum number of connections in the pool
        config.setIdleTimeout(30000);          // Timeout for idle connections (ms)
        config.setMaxLifetime(1800000);        // Max lifetime of a connection (ms)
        config.setConnectionTimeout(30000);    // Timeout for getting a connection (ms)

        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("lab4.database.entity");
        factory.setDataSource(dataSource());
        factory.setValidationMode(ValidationMode.AUTO);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        (ar) -> {
                            ar.requestMatchers("/ws/**").permitAll();
                            ar.requestMatchers("/api/auth/**").permitAll(); // Allow all methods on /api/movies/**
                            ar.requestMatchers("/api/movies/**").permitAll(); // Allow all methods on /api/movies/**
                            ar.requestMatchers("/api/**").permitAll(); // Allow all methods on other /api/** endpoints as well
                            ar.requestMatchers("/api/admin/**").permitAll(); // Allow all methods on other /api/** endpoints as well
                        }
                );
        httpSecurity.addFilterAfter(getJWTAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtFilter getJWTAuthFilter() {
        return new JwtFilter();
    }


}
