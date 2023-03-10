package rs.edu.raf.si.user_service.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;

public interface UserService {

    User getUser(String username) throws Exception;
    User createUser(UserCreateForm userCreateForm) throws Exception;
    boolean isAdmin(String username) throws Exception;

}
