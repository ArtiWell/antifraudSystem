package antifraud.mapper;

import antifraud.controller.user.UserDTO;
import antifraud.controller.user.UserResponse;
import antifraud.dao.User.UserEntity;


public interface UserMapper {
    UserMapper USER_MAPPER = new UserMapperImpl();

    UserResponse toResponse(UserEntity entity);

    UserEntity toEntity(UserDTO userDTO);


}
