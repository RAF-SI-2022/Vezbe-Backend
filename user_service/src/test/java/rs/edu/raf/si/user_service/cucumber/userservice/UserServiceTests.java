package rs.edu.raf.si.user_service.cucumber.userservice;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

// Objasnjenje anotacija:
//  - @Suite: grupise testove, u ovom slucaju Cucumber testove
//  - @IncludeEngines: ukljucuje Cucumber testove
//  - @SelectClasspathResource - testira features definisane u resources/features/userservice direktorijumu
//  - @ConfigurationParameter - koristimo ovu anotaciju da naznacimo u kom paketu se nalazi Glue kod (obicno je to ovaj
//    isti paket)

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/userservice")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "rs.edu.raf.si.user_service.cucumber.userservice")
public class UserServiceTests {
}
