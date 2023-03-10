package rs.edu.raf.si.user_service.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestForm {

    private String username;
    private String password;

}
