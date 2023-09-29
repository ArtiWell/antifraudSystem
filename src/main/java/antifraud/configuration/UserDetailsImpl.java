package antifraud.configuration;

import antifraud.dao.User.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;
    private final boolean isAccountNonLocked;

    public UserDetailsImpl(UserEntity entity) {
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + entity.getRole().toString()));
        isAccountNonLocked = entity.isAccountNonLocked();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
