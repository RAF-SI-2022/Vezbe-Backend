package rs.edu.raf.si.user_service.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import rs.edu.raf.si.user_service.form.LoginResponseForm;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;
import rs.edu.raf.si.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Vezbe 2: obratite paznju na komentare u ovom fajlu.
// Bitno je da vam klasa sa Integracionim testovima bude anotirana sa ovim anotacijama.
// SpringBootTest ce pokrenuti Spring Application Context za svrhe testiranje. To znaci da je potrebno da imate
// pokrenutu bazu podataka.
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceIntegrationTest {

    // Posto imamo Spring Application Context, mozemo koristiti "@Autowired" anotaciju (ovo nije moguce u Unit testovima).
    // MockMvc nam dozvoljava da testiramo web servise tako sto cemo slati HTTP zahteve.
    // Gledajte na ovu klasu kao Postman ili Insomniu.
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper nam dozvoljava da pretvorimo objekat u JSON string i obrnuto.
    @Autowired
    private ObjectMapper objectMapper;

    // Mozemo inject-ovati i nase servise, komponente i ostale bean-ove,
    // a zatim te klase, tj. objekte koristiti u testovima.
    @Autowired
    private UserService userService;

    // Isto kao i kod unit testova, svi testovi moraju biti anotirani sa "@Test".
    @Test
    void testCreateUser() throws Exception {
        // KORAK 1: Logujemo se na servis kako bi dobili JWT token.
        //
        // NAPOMENA: Za razliku od Unit testova, u integracionim testovima je obavezno da se logujemo onako kako treba
        // (tako sto cemo uputiti zahtev na login rutu, a zatim uzeti JWT token koji dobijemo kao odgovor).
        // Za potrebe Unit testova mozete ubaciti neki JWT token kao konstantu umesto logovanja.
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"admin\"}"))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();

        // OPCIJA 1: Login ruta vraca samo string koji sadrzi JWT token.
        // Replace sluzi da ukloni navodnike ukoliko je JWT token pod navodnicima.
        // String token = mvcResult.getResponse().getContentAsString().replace("\"", "");

        // OPCIJA 2: Login ruta vraca JSON objekat koji sadrzi JWT token.
        // U tom slucaju parsiramo JSON objekat u Java objekat (ovo vam moze biti korisno za druge testove).
        // Primere sa ObjectMapper-om imate na sledecem linku: https://www.baeldung.com/jackson-object-mapper-tutorial
        // NAPOMENA: Klasa u koju deserializujete odgovor mora da ima prazan konstruktor i settere
        // ("@NoArgsConstructor" i "@Data" u lomboku).
        String loginResponse = mvcResult.getResponse().getContentAsString();
        LoginResponseForm loginResponseForm = objectMapper.readValue(loginResponse, LoginResponseForm.class);
        String token = loginResponseForm.getJwt();

        // KORAK 2: Definisemo objekat koji cemo poslati ruti koju testiramo.
        //
        // VAZNA NAPOMENA: Mozete imati problema ako imate neke constraitove u bazi podataka, npr. da vam je username
        // unique. U tom slucaju, kreiranje korisnika "pera" bi proslo prvi put, ali drugi put bi test failovao
        // zato sto ne moze da kreira korisnika koji vec postoji. Drugim recima, u zavisnosti od podesavanja vaseg
        // mikroservisa, ono sto integracioni test odradi ce ostati sacuvano u bazi podataka nakon sto se test zavrsi.
        // Postoji vise nacina da se ovaj problem resi:
        //   - koriscenjem "create-drop" strategije tako da na kraju baza podataka bude obrisana
        //   - rucno brisanjem objekta na kraju testa
        //   - koriscenjem random vrednosti
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername("pera");
        userCreateForm.setImePrezime("Pera Peric");
        userCreateForm.setPassword("test");
        userCreateForm.setIsAdmin(false);

        // KORAK 3: Pozivamo metodu koju zelimo da testiramo i proveravamo da li smo dobili ocekivani odgovor
        // (u ovom slucaju da je status HTTP zahteva OK).
        //
        // Toj metodi prosledjujemo serializovan JSON objekat, kao i autorizacioni token.
        // Po potrebi promeniti "post" u zavisnosti da li je ruta GET, POST ili nesto drugo.
        //
        // Po potrebi mozete dodati jos neke provere (videti gore kod logovanja kako mozemo da izvucemo odgovor
        // koji smo dobili.
        mockMvc.perform(post("/api")
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(userCreateForm)))
                .andExpect(status().isOk());
    }

    // Primer testa gde direktno koristimo servis umesto HTTP zahteva.
    // Isto kao i kod unit testova, svi testovi moraju biti anotirani sa "@Test".
    @Test
    void testCreateUserSpring() {
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setImePrezime("Pera Peric");
        userCreateForm.setUsername("petar");
        userCreateForm.setPassword("test");
        userCreateForm.setIsAdmin(true);

        try {
            User user = userService.createUser(userCreateForm);
            assertNotNull(user);
            assertEquals(userCreateForm.getUsername(), user.getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
