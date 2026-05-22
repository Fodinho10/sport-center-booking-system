package hu.dani.nhf.springnhf.security;

import hu.dani.nhf.springnhf.entity.User;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
@Nonnull
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Override
    @Nonnull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // A Spring Security elvárja a "ROLE_" előtagot a szerepkörök nevei előtt
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @Nonnull
    public String getUsername() {
        return user.getEmail(); // E-mail alapú azonosítás
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}