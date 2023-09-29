package antifraud.controller.user;

import antifraud.enums.UserRolesEnum;
import antifraud.exception.WrongDataException;

public class RoleDTO {
    private String username;
    private UserRolesEnum role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRolesEnum getRole() {
        return role;
    }

    public void setRole(String role) {
        try {
            this.role = UserRolesEnum.valueOf(role);
        } catch (Exception e) {
            throw new WrongDataException();
        }


    }
}
