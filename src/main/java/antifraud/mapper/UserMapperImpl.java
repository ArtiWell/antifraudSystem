package antifraud.mapper;

import antifraud.controller.user.UserDTO;
import antifraud.controller.user.UserResponse;
import antifraud.dao.User.UserEntity;
import antifraud.enums.UserRolesEnum;

public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        Long id = null;
        String name = null;
        String username = null;
        UserRolesEnum role = null;

        id = entity.getId();
        name = entity.getName();
        username = entity.getUsername();
        role = entity.getRole();

        UserResponse userResponse = new UserResponse(id, name, username, role);

        return userResponse;
    }

    @Override
    public UserEntity toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setName(userDTO.name());
        userEntity.setUsername(userDTO.username());
        userEntity.setPassword(userDTO.password());
        return userEntity;
    }

}