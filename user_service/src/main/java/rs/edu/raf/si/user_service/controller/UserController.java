package rs.edu.raf.si.user_service.controller;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.user_service.dto.UserDto;
import rs.edu.raf.si.user_service.form.UserCreateForm;
import rs.edu.raf.si.user_service.service.UserService;

import java.util.List;

// Vezbe 13: @Timed anotacije ukljucuje metrike za klasu i endpointe
// Obratiti paznju da su i metode anotirane sa @Timed
@RestController
@RequestMapping("/api")
@Timed
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed("controller.users.list")
    public ResponseEntity<List<UserDto>> listUsers() {
        try {
            return ResponseEntity.ok(userService.listUsers());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed("controller.users.get")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        try {
            return ResponseEntity.ok(userService.getUser(username));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed("controller.users.create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreateForm userCreateForm) {
        try {
            return ResponseEntity.ok().body(userService.createUser(userCreateForm));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed("controller.users.edit")
    public ResponseEntity<UserDto> editUser(@RequestBody UserCreateForm userCreateForm) {
        try {
            return ResponseEntity.ok().body(userService.editUser(userCreateForm));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
