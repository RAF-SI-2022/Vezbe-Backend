package rs.edu.raf.si.user_service.cucumber.userservice;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import rs.edu.raf.si.user_service.dto.UserDto;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;
import rs.edu.raf.si.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTestsStepsAdmin extends UserServiceTestsConfig {

    @Autowired
    private UserService userService;

    @When("imamo korisnika {string} koji je administrator")
    public void imamo_korisnika_koji_je_administrator(String username) {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername(username);
        userCreateForm.setPassword("test");
        userCreateForm.setImePrezime(username);
        userCreateForm.setIsAdmin(true);

        try {
            UserDto userDto = userService.createUser(userCreateForm);
            assertNotNull(userDto);
            assertEquals(userCreateForm.getUsername(), userDto.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("metoda isAdmin treba da vrati da je {string} administrator")
    public void metoda_is_admin_treba_da_vrati_da_je_on_administrator(String username) {
        try {
            Boolean admin = userService.isAdmin(username);
            assertTrue(admin);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
