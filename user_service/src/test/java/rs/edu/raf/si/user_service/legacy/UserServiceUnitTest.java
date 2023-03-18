package rs.edu.raf.si.user_service.legacy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;
import rs.edu.raf.si.user_service.repository.UserRepository;
import rs.edu.raf.si.user_service.service.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

// Vezbe 2: obratite paznju na komentare u ovom fajlu.
// Bitno je da vam klasa sa Unit testovima bude anotirana sa ovom anotacijom kako bi Mockito radio kako treba.
@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    // Klasa koju mockujemo (mozemo imati vise mockovanih klasa).
    //
    // VAZNA NAPOMENA: @Mock mockuje **celu klasu**, tj. pravi objekat cije sve metode vracaju null ili ne rade nista.
    // To znaci da svaku metodu koju pozivamo nad mockovanim objektom prvo moramo da mockujuemo, tj. da sa "given" i
    // "willReturn" definisemo kako ce se ta metoda ponasati.
    // Postoji i "parcijalni mock" koji cemo pokazati sledeci cas (hint: za to se koristi "@Spy" anotacija).
    @Mock
    private UserRepository userRepository;

    // Klasa u koju ubacujemo mockovane objekte (mozemo imati vise ovakvih klasa).
    @InjectMocks
    private UserServiceImpl userService;

    // VAZNA NAPOMENA: Svaka test funkcija mora biti anotirana sa "@Test" kako biste mogli da pokrenete taj test!
    @Test
    void testIsAdmin() {
        // KORAK 1 (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        User user1 = new User();
        user1.setUsername("pera");
        user1.setImePrezime("Pera Peric");
        user1.setIsAdmin(false);

        User user2 = new User();
        user2.setUsername("mika");
        user2.setImePrezime("Mika Mikic");
        user2.setIsAdmin(true);

        // KORAK 2: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        // Vezbe 2: BITNO! Importovati "given" metodu iz Mockito biblioteke!
        given(userRepository.findUserByUsername("pera")).willReturn(Optional.of(user1));
        given(userRepository.findUserByUsername("mika")).willReturn(Optional.of(user2));

        try {
            // KORAK 3: Pozivamo metodu koju zelimo da testiramo.
            Boolean peraAdmin = userService.isAdmin("pera");

            // KORAK 4: Proveramo da li smo dobili ocekivani rezultat.
            // Vezbe 2: BITNO! Importovati "assert*" metode iz JUnit/Jupiter biblioteke!
            assertNotNull(peraAdmin);
            assertEquals(peraAdmin, false);

            // Mozemo vise puta ponoviti korake 3 i 4.
            Boolean mikaAdmin = userService.isAdmin("mika");

            assertNotNull(mikaAdmin);
            assertEquals(mikaAdmin, true);
        } catch (Exception e) {
            // Ako dodje do exception-a, failujemo test.
            //
            // VAZNA NAPOMENA: Ovde korisiti fail umesto da throw-ujete exception, inace ce test proci,
            // a on zapravo ne radi kako treba.
            //
            // Vezbe 2: BITNO! Importovati "fail" metodu iz JUnit/Jupiter biblioteke!
            fail(e.getMessage());
        }
    }

    // VAZNA NAPOMENA: Svaka test funkcija mora biti anotirana sa @Test kako biste mogli da pokrenete taj test!
    @Test
    void testCreateUser() {
        // KORAK 1a (INPUT TESTA): Koje objekte cemo dati funkciji koju testiramo.
        UserCreateForm userCreateForm = new UserCreateForm();
        userCreateForm.setUsername("pera");
        userCreateForm.setImePrezime("Pera Peric");
        userCreateForm.setPassword("test");
        userCreateForm.setIsAdmin(false);

        // KORAK 1b (OUTPUT TESTA): Koje objekte ocekujemo da nam test vrati.
        User user = new User();
        user.setUsername("pera");
        user.setImePrezime("Pera Peric");
        user.setIsAdmin(false);

        // KORAK 2: Mockujemo metode koje rade sa eksternim servisima, npr. bazom podataka.
        //
        // VAZNA NAPOMENA 1: Ovde imate primer mockovanja staticke metode (za to morate da imate ukljucen
        // odgovarajuci dependency u pom.xml).
        //
        // VAZNA NAPOMENA 2: Na drugim vezbama odrzanim u cetvrtak je detaljno objasnjeno zasto mockujemo "gensalt" metodu.
        // Ukratko, da bi unit testovi radili kako treba, bitno je postici determinizam.
        // To znaci da ako u metodi koju testirate pozivate neku drugu metodu koja ne vraca uvek isti rezultat
        // (npr. vraca random broj/string), tu drugu metodu morate da mockujete. Inace, poziv metode koju testirate
        // ce vratiti drugaciji objekat u odnosu na onaj koji ocekujete, pa ce "assert" metoda da izbaci gresku.
        // U nasem slucaju, "gensalt" metoda je ta koja vraca random string, pa smo nju mockovali.
        // Takodje, obratiti paznju da nismo mockovali celu "hashpw" metodu, vec samo "gensalt".
        String salt = BCrypt.gensalt();
        try (MockedStatic<BCrypt> bc = Mockito.mockStatic(BCrypt.class)) {
            bc.when(BCrypt::gensalt).thenReturn(salt);

            String hashPW = BCrypt.hashpw(userCreateForm.getPassword(), salt);
            user.setPassword(hashPW);

            given(userRepository.save(user)).willReturn(user);

            try {
                // KORAK 3: Pozivamo metodu koju zelimo da testiramo.
                User expectedUser = userService.createUser(userCreateForm);

                // KORAK 4: Proveramo da li smo dobili ocekivani rezultat.
                assertNotNull(expectedUser);
                assertEquals(expectedUser.getPassword(), user.getPassword());
            } catch (Exception e) {
                // Ako dodje do exception-a, failujemo test.
                //
                // VAZNA NAPOMENA: Ovde korisiti fail umesto da throw-ujete exception, inace ce test proci,
                // a on zapravo ne radi kako treba.
                //
                // Vezbe 2: BITNO! Importovati "fail" metodu iz JUnit/Jupiter biblioteke!
                fail(e.getMessage());
            }
        }
    }

}
