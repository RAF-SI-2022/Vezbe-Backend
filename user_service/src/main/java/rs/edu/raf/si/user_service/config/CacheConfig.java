package rs.edu.raf.si.user_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

// Vezbe 8: Potrebno je dodati @EnableCaching anotaciju u jednu od konfiguracionih klasa kako bi Redis kesiranje
// funkcionisalo.
@Configuration
@EnableCaching
public class CacheConfig {

}
