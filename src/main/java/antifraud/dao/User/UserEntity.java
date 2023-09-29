package antifraud.dao.User;

import antifraud.enums.UserRolesEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Users")
@Getter
@Setter
@ToString
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private UserRolesEnum role = UserRolesEnum.MERCHANT;
    @Column(name = "locked")
    private boolean isAccountNonLocked = false;

    public UserEntity setRole(UserRolesEnum role) {
        this.role = role;
        return this;
    }

}
