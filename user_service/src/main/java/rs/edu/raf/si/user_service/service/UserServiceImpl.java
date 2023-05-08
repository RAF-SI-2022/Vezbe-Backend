package rs.edu.raf.si.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.user_service.dto.UserDto;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;
import rs.edu.raf.si.user_service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Vezbe 8: Kesiranje i Redis integracija
// Ovo je primer servisa gde podatke koje metode tog servisa vracaju kesiramo u Redis-u.
// Napomena: kao sto je pomenuto na vezbama, korisnicki servis nije idealan za kesiranje.
// Primera radi, nekada ne zelite da kesirate senzitivne podatke (npr. podatke o korisniku) ili njegove permisije.
// Kesiranje permisija moze da dovede do toga da vi korisniku oduzmete neke permisije, a da on i dalje ima pristup
// sistemu sve dok kes koji se cuva ne istekne (vreme posle kog kes istice se definise u application.properties).
//
// Dobri servisi za kesiranje su oni koji rade nad podacima kojima se cesto pristupa, a koji se ne menjaju toliko
// cesto. To mogu da budu razni sifarnici, npr. lista valuta, lista postanskih brojeva, itd.
//
// Napomena 2: kesiranje ima efekta samo ako se metode pozivaju iz neke druge klase. Ukoliko se metode pozivaju iz
// iste klase, kesiranje nece funkcionisati.

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Vezbe 8
    // Postoje tri razlicite anotacije koje se koriste da se kesiranje ukljuci za odredjenu metodu:
    //   - @Cacheable  - dohvata podatke iz kesa ukoliko su podaci kesirani, inace, izvrasava anotiranu metodu i kesira rezultat
    //                   (koristi se prilikom listanja i dohvatanja podataka)
    //   - @CachePut   - upisuje podatke u kes (koristi se prilikom kreiranja ili azuriranja objekta)
    //   - @CacheEvict - brise podatke iz kesa (koristi se prilikom brisanja objekta)
    // Dodatno, @Caching anotacija vam dozvoljava da kombinujete ili ponavljate anotacije (videti primer sa brisanjem).
    // Sve tri anotacije uzimaju sledeca dva parametra (postoji jos parametara, ali ova dva su bitna):
    //   - value - objekat u kome se cuvaju podaci (gledati na ovo kao tabelu u relacionim bazama podataka)
    //   - key   - kljuc pod kojim se cuva konkretan podatak (koristi se samo za kesiranje konkretnog objekta, tj. ne koristi se kada se kesiraju liste)
    // Napomena: preporuceno je da se liste i konkretni objekti cuvaju u razlicitim objektima (tj. da su value parametri razliciti).
    // Pogledati listUsers i getUser metode kao primer.

    // Vezbe 8
    // Ova metoda dohvata listu svih korisnika i kesira tu istu listu u objektu "users".
    // Kada se metoda pozove, Spring ce proveriti da li u Redis-u, u objektu "users" postoji nesto.
    // Ukoliko postoji, automatski se vraca ono sto je u Redis-u, tj. metoda se uopste nece izvrsiti.
    // Ukoliko ne postoji, izvrsava se metoda, a rezultat izvrsavanja se cuva u Redis-u.
    // U application.properties konfigurisati koliko dugo vazi kesiran podatak, npr. da se posle 15 minuta kes invalidira
    // (u tom slucaju se nakon invalidiranja pri sledecem pozivu metode ona ponovo izvrsava).
    // Primetiti da ovde nemamo "key" parametar u Cacheable anotaciji posto kesiramo listu.
    @Override
    @Cacheable(value = "users")
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream().map(this::convertUserToDto).collect(Collectors.toList());
    }

    // Vezbe 8
    // Ovo je primer kesiranja konkretnog objekta, u ovom slucaju pojedinacnog korisnika.
    // Primetiti da je "value" parametar drugaciji u odnosu na "listUsers" metodu i da imamo "key" parametar.
    // Vrednost "key" parametra ima "#" sto nam dozvoljava da referenciramo argumente funkcije.
    @Override
    @Cacheable(value = "user", key = "#username")
    public UserDto getUser(String username) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if(userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }

        return convertUserToDto(userOpt.get());
    }

    // Vezbe 8
    // Ovo je primer kesiranja prilikom dodavanja novog objekta, u ovom slucaju korisnika.
    // Primetiti da ovde kombinujemo @CachePut i @CacheEvict anotacije:
    //   - @CachePut sluzi da se taj korisnik kesira odmah prilikom kreiranja objekta
    //   - @CacheEvict u ovom slucaju sluzi da obrise kesiranu listu svih korisnika.
    //     Ovo je izuzetno vazno zato sto ako bi ovaj korak preskocili i pokusali da izlistamo sve korisnike,
    //     ne bismo videli korisnika kojeg smo napravili sve dok kes ne istekne
    @Override
    @CachePut(value = "user", key = "#userCreateForm.username")
    @CacheEvict(value = "users", allEntries = true)
    public UserDto createUser(UserCreateForm userCreateForm) throws Exception {
        if(userCreateForm.getUsername().isBlank() ||
            userCreateForm.getPassword().isBlank() ||
            userCreateForm.getImePrezime().isBlank() ||
            userCreateForm.getIsAdmin() == null) {
            throw new Exception("user is missing data");
        }

        User user = new User();
        user.setUsername(userCreateForm.getUsername());
        user.setImePrezime(userCreateForm.getImePrezime());
        user.setIsAdmin(userCreateForm.getIsAdmin());

        String hashPW = BCrypt.hashpw(userCreateForm.getPassword(), BCrypt.gensalt());
        user.setPassword(hashPW);

        user = userRepository.save(user);

        return convertUserToDto(user);
    }

    // Vezbe 8
    // Ovo je primer kesiranja prilikom izmene postojeceg objekta, u ovom slucaju korisnika.
    // @CachePut i @CacheEvict se koriste na isti nacin kao kod kreiranja novog korisnika.
    @Override
    @CachePut(value = "user", key = "#userCreateForm.username")
    @CacheEvict(value = "users", allEntries = true)
    public UserDto editUser(UserCreateForm userCreateForm) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(userCreateForm.getUsername());
        if(userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }
        User user = userOpt.get();

        if(!userCreateForm.getPassword().isBlank()) {
            String hashPW = BCrypt.hashpw(userCreateForm.getPassword(), BCrypt.gensalt());
            user.setPassword(hashPW);
        }
        if(!userCreateForm.getImePrezime().isBlank()) {
            user.setImePrezime(userCreateForm.getImePrezime());
        }
        if(userCreateForm.getIsAdmin() != null) {
            user.setIsAdmin(userCreateForm.getIsAdmin());
        }

        user = userRepository.save(user);

        return convertUserToDto(user);
    }

    // Vezbe 8
    // Ovo je primer brisanja objekta, u ovom slucaju korisnika.
    // U ovom slucaju moramo da:
    //   - Obrisemo pojedinacni kesirani objekat (value = "user").
    //     Ukoliko ovo preskocimo, dohvatanje pojedinacnog korisnika ce vratiti obrisanog korisnika sve dok kes ne istekne.
    //   - Obrisemo kesiranu listu svih korisnika (value = "users").
    //     Ukoliko ovo preskocimo, listanje svih korisnika ce vratiti obrisanog korisnika sve dok kes ne istekne.
    // Posto nam Java ne dozvoljava da navedemo istu anotaicju dva puta (@CacheEvict), koristimo @Caching anotaciju
    // da bi spojili dve @CacheEvict anotacije.
    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "user", key = "#user.username")
    })
    public UserDto deleteUser(User user) {
        userRepository.delete(user);

        return convertUserToDto(user);
    }

    @Override
    public boolean isAdmin(String username) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if(userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }

        return userOpt.get().getIsAdmin();
    }

    private UserDto convertUserToDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setIsAdmin(user.getIsAdmin());
        userDto.setImePrezime(user.getImePrezime());

        return userDto;
    }

}
