package rs.edu.raf.si.user_service.cucumber.userservice;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

// Ova klasa sluzi da konfigurise Cucumber testove.
//   - @CucumberContextConfiguration konfigurise Cucumber context
//   - @SpringBootTest - konfigurise Spring Application Context za testiranje
// Po potrebi dodati druge anotacije.

@CucumberContextConfiguration
@SpringBootTest
public class UserServiceTestsConfig {
}
