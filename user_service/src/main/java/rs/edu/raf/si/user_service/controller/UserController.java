package rs.edu.raf.si.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.model.User;
import rs.edu.raf.si.user_service.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody UserCreateForm userCreateForm) {
        try {
            User user = userService.createUser(userCreateForm);
            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
