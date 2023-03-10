package rs.edu.raf.si.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.si.user_service.form.LoginRequestForm;
import rs.edu.raf.si.user_service.form.LoginResponseForm;
import rs.edu.raf.si.user_service.model.User;
import rs.edu.raf.si.user_service.service.UserService;
import rs.edu.raf.si.user_service.util.JwtUtil;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequestForm loginRequestForm) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestForm.getUsername(), loginRequestForm.getPassword()));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }

        // Uzimam User-a iz baze kako bih mogao da ubacim role u JWT token
        User user = userService.getUser(loginRequestForm.getUsername());
        return ResponseEntity.ok(new LoginResponseForm(jwtUtil.generateToken(user)));
    }

}