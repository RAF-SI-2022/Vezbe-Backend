package rs.edu.raf.si.user_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Vezbe 2: treba da prekopirate celu ovu klasu u svaki vas mikroservis.
// Swagger-u pristupate preko sledeceg linka: http://localhost:8080/swagger-ui/index.html
// (promeniti URL/port u zavisnosti od mikroservisa).
//
// Na sledecem linku imate nesto vise o tome kako mozete da prosirite Swagger dokumentaciju sa dodatnim detaljima:
// https://www.baeldung.com/swagger-set-example-description
// (pratite od koraka 4 pa na dalje).
@Configuration
public class SwaggerConfig {

    // Ovo treba da se promeni u naziv mikroservisa.
    private final String APP_TITLE = "Korisnicki servis";
    // Ovo treba da se promeni u neki opis mikroservisa.
    private final String APP_DESCRIPTION = "API za korisnicki servis";

    // Ovo ispod ostavite kako jeste.
    private final String APP_API_VERSION = "1.0";
    private final String APP_LICENSE = "Licenca";
    private final String APP_LICENSE_URL = "Licenca";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title(APP_TITLE)
                        .description(APP_DESCRIPTION)
                        .version(APP_API_VERSION)
                        .license(new License().name(APP_LICENSE).url(APP_LICENSE_URL)));
    }

}
