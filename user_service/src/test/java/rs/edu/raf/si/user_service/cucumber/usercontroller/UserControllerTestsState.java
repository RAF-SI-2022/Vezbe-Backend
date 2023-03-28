package rs.edu.raf.si.user_service.cucumber.usercontroller;

import io.cucumber.spring.ScenarioScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ScenarioScope
public class UserControllerTestsState {

    private String jwtToken;

}
