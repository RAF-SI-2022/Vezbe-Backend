package rs.edu.raf.si.user_service.cucumber.usercontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import rs.edu.raf.si.user_service.form.LoginResponseForm;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;
import rs.edu.raf.si.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTestsSteps extends UserControllerTestsConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserControllerTestsState state;

    @Given("logovali smo se na aplikaciju kao administrator")
    public void logovali_smo_se_na_aplikaciju_kao_administrator() {
        try {
            ResultActions resultActions = mockMvc.perform(post("/auth/login")
                            .contentType("application/json")
                            .content("{\"username\":\"admin\",\"password\":\"admin\"}"))
                    .andExpect(status().isOk());
            MvcResult mvcResult = resultActions.andReturn();

            String loginResponse = mvcResult.getResponse().getContentAsString();
            LoginResponseForm loginResponseForm = objectMapper.readValue(loginResponse, LoginResponseForm.class);
            state.setJwtToken(loginResponseForm.getJwt());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @When("pravimo novog korisnika koji se zove {string}")
    public void pravimo_novog_korisnika_koji_se_zove(String username) {
        System.err.println(state.getJwtToken());
        try {
            UserCreateForm userCreateForm = new UserCreateForm();
            userCreateForm.setUsername(username);
            userCreateForm.setImePrezime(username);
            userCreateForm.setPassword("test");
            userCreateForm.setIsAdmin(true);

            mockMvc.perform(post("/api")
                            .contentType("application/json")
                            .header("Authorization", "Bearer " + state.getJwtToken())
                            .content(objectMapper.writeValueAsString(userCreateForm)))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("proveravmo da li je {string} upisan u bazu podataka")
    public void proveravmo_da_li_je_upisan_u_bazu_podataka(String username) {
        try {
            User user = userService.getUser(username);
            assertNotNull(user);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("proveram da li je JWT token setovan")
    public void proveram_da_li_je_jwt_token_setovan() {
        System.err.println(state.getJwtToken());
    }

}
