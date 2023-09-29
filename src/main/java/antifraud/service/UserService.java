package antifraud.service;

import antifraud.configuration.UserDetailsImpl;
import antifraud.controller.user.*;
import antifraud.dao.User.UserEntity;
import antifraud.dao.User.UserRepository;
import antifraud.enums.UserRolesEnum;
import antifraud.exception.ConflictException;
import antifraud.exception.WrongDataException;
import antifraud.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

import static antifraud.mapper.UserMapper.USER_MAPPER;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    private Consumer<UserEntity> firstUserRoleAdministrator;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        firstUserRoleAdministrator = user -> {
            if (userRepository.count() == 0) {
                user.setRole(UserRolesEnum.ADMINISTRATOR);
                user.setAccountNonLocked(true);
            }
            firstUserRoleAdministrator = u -> {
            };
        };
    }

    public UserResponse addUser(UserDTO userDTO) {
        if (userDTO.name() == null || userDTO.username() == null || userDTO.password() == null) {
            throw new WrongDataException();
        }
        if (userRepository.findByUsernameIgnoreCase(userDTO.username()).isPresent()) {
            throw new ConflictException();
        }
        UserEntity entity = USER_MAPPER.toEntity(new UserDTO(userDTO.name(), userDTO.username(), encoder.encode(userDTO.password())));
        firstUserRoleAdministrator.accept(entity);
        userRepository.save(entity);
        return USER_MAPPER.toResponse(entity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsernameIgnoreCase(username).orElseGet(UserEntity::new);
        return new UserDetailsImpl(user);
    }

    public List<UserResponse> list() {
        return userRepository.findAll(Sort.by("id")).stream().map(USER_MAPPER::toResponse).toList();
    }

    @Transactional
    public DeleteUserDTO deleteUser(String username) {
        if (userRepository.findByUsernameIgnoreCase(username).isEmpty()) {
            throw new EntityNotFoundException();
        }
        userRepository.delete(userRepository.findByUsernameIgnoreCase(username).orElseGet(UserEntity::new));
        return new DeleteUserDTO(username, "Deleted successfully!");
    }

    public UserResponse changeRole(RoleDTO role) {
        UserEntity userEntity = userRepository
                .findByUsernameIgnoreCase(role.getUsername())
                .orElseThrow(EntityNotFoundException::new);

        if (role.getRole().equals(userEntity.getRole())) {
            throw new ConflictException();
        }
        if (role.getRole().equals(UserRolesEnum.ADMINISTRATOR))
            throw new WrongDataException();
        return UserMapper.USER_MAPPER.toResponse(
                userRepository.save(userEntity.setRole(role.getRole())));
    }

    public AccessResponse unlockUser(AccessDTO accessDTO) {

        userRepository.save(userRepository
                .findByUsernameIgnoreCase(accessDTO.username())
                .map(e -> {
                    if (e.getRole().equals(UserRolesEnum.ADMINISTRATOR))
                        throw new WrongDataException();
                    e.setAccountNonLocked(accessDTO.operation().isLocked());
                    return e;
                })
                .orElseThrow(EntityNotFoundException::new));

        return new AccessResponse(String.format("User %s %s!", accessDTO.username(), accessDTO.operation().getResponseStatus()));

    }
}
