package rs.edu.raf.si.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;
import rs.edu.raf.si.user_service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUser(String username) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if(userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }

        return userOpt.get();
    }

    @Override
    public User createUser(UserCreateForm userCreateForm) throws Exception {
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

        return userRepository.save(user);
    }

    @Override
    public boolean isAdmin(String username) throws Exception {
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if(userOpt.isEmpty()) {
            throw new Exception("user does not exist");
        }

        return userOpt.get().getIsAdmin();
    }

}
