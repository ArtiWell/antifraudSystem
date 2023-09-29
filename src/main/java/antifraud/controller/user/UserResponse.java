package antifraud.controller.user;

import antifraud.enums.UserRolesEnum;

public record UserResponse(Long id, String name, String username, UserRolesEnum role) {


}
