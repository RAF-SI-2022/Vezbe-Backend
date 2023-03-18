package rs.edu.raf.si.user_service.cucumber.userservice;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;
import rs.edu.raf.si.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

// U ovoj klasi se dodaje Glue kod, tj. definisu se step-ovi.
// BITNO: Ova klasa mora da nasledjuje konfiugracionu klasu!
// Za generisanje step metoda, pokrenuti testove. Cucumber ce fail-ovati ali ce vam pritom
// generisati metode koje vi mozete da copy-paste.

public class UserServiceTestsSteps extends UserServiceTestsConfig {

    @Autowired
    private UserService userService;

    @When("kada se napravi novi korisnik")
    public void kada_se_napravi_novi_korisnik() {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername("zika");
        userCreateForm.setPassword("test");
        userCreateForm.setImePrezime("Zika Zivkovic");
        userCreateForm.setIsAdmin(false);

        try {
            User user = userService.createUser(userCreateForm);
            assertNotNull(user);
            assertEquals(userCreateForm.getUsername(), user.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("taj korisnik je sacuvan u bazi podataka")
    public void taj_korisnik_je_sacuvan_u_bazi_podataka() {
        try {
            User user = userService.getUser("zika");
            assertNotNull(user);
            assertEquals("zika", user.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @When("napravimo novog korisnika koji se zove {string}, ima username {string} i password {string}")
    public void napravimo_novog_korisnika_koji_se_zove_ima_username_i_password(String ime, String username, String password) {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername(username);
        userCreateForm.setPassword(password);
        userCreateForm.setImePrezime(ime);
        userCreateForm.setIsAdmin(false);

        try {
            User user = userService.createUser(userCreateForm);
            assertNotNull(user);
            assertEquals(userCreateForm.getUsername(), user.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("{string} je sacuvan u bazi podataka")
    public void je_sacuvan_u_bazi_podataka(String username) {
        try {
            User user = userService.getUser(username);
            assertNotNull(user);
            assertEquals(username, user.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Given("korisnik {string} postoji u bazi podataka")
    public void korisnik_postoji_u_bazi_podataka(String username) {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername(username);
        userCreateForm.setPassword("test");
        userCreateForm.setImePrezime(username);
        userCreateForm.setIsAdmin(false);

        try {
            User user = userService.createUser(userCreateForm);
            assertNotNull(user);
            assertEquals(userCreateForm.getUsername(), user.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @When("zelimo da obrisemo korisnika ciji je username {string}")
    public void zelimo_da_obrisemo_korisnika_cije_je_username(String username) {
        try {
            User user = userService.getUser(username);
            userService.deleteUser(user);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("{string} je izbrisan iz baze podataka")
    public void je_izbrisan_iz_baze_podataka(String username) {
        Exception exception = assertThrows(Exception.class, () -> {
            userService.getUser(username);
        });

        String expectedExceptionMessage = "user does not exist";
        String actualExceptionMessage = exception.getMessage();

        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }

}
