package cn.edu.xmu.yeahbuddy.model;

import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdministratorDto implements Serializable {

    private static final long serialVersionUID = -4242984414905004337L;

    private String password;

    private String username;

    private String displayName;

    private Collection<AdministratorPermission> authorities;

    @Contract(pure = true)
    public String getDisplayName() {
        return displayName;
    }

    public AdministratorDto setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Contract(pure = true)
    public String getPassword() {
        return password;
    }

    public AdministratorDto setPassword(String password) {
        this.password = password;
        return this;
    }

    @Contract(pure = true)
    public String getUsername() {
        return username;
    }

    public AdministratorDto setUsername(String username) {
        this.username = username;
        return this;
    }

    @Contract(pure = true)
    public Collection<AdministratorPermission> getAuthorities() {
        return authorities;
    }

    public AdministratorDto setAuthorities(Collection<String> authorities) {
        this.authorities = authorities.stream().map(AdministratorPermission::valueOf).collect(Collectors.toSet());
        return this;
    }

    public boolean ready() {
        return Stream.of(password, username, displayName, authorities)
                     .allMatch(Objects::nonNull);
    }
}
