package antifraud.controller.user;

import antifraud.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("user")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserResponse addUser(@RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @GetMapping("list")
    public List<UserResponse> list() {
        return userService.list();
    }

    @DeleteMapping("user/{username}")
    public DeleteUserDTO deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @PutMapping("role")
    public UserResponse changeRole(@RequestBody RoleDTO role) {
        return userService.changeRole(role);
    }

    @PutMapping("access")
    public AccessResponse unlockUser(@RequestBody AccessDTO accessDTO) {
        return userService.unlockUser(accessDTO);
    }


}
