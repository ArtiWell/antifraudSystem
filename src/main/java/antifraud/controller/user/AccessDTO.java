package antifraud.controller.user;

import antifraud.enums.AccessUserEnum;

public record AccessDTO(String username, AccessUserEnum operation) {
}
