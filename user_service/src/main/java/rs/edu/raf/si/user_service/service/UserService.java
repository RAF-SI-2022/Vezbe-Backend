package rs.edu.raf.si.user_service.service;

import rs.edu.raf.si.user_service.dto.UserDto;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;

import java.util.List;

public interface UserService {

    UserDto getUser(String username) throws Exception;
    List<UserDto> listUsers();
    UserDto createUser(UserCreateForm userCreateForm) throws Exception;
    UserDto editUser(UserCreateForm userCreateForm) throws Exception;
    UserDto deleteUser(User user);
    boolean isAdmin(String username) throws Exception;

}
