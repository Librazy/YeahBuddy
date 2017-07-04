package cn.edu.xmu.yeahbuddy.model;

import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import org.jetbrains.annotations.Contract;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class AdministratorDto implements Serializable {

    private static final long serialVersionUID = -4242984414905004337L;

    private String password;

    private String name;

    @NotNull
    private Collection<AdministratorPermission> authorities = new HashSet<>();

    public String getPassword() {
        return password;
    }

    public AdministratorDto setPassword(String password) {
        this.password = password;
        return this;
    }

    @Contract(pure = true)
    public String getName() {
        return name;
    }

    public AdministratorDto setName(String name) {
        this.name = name;
        return this;
    }

    @NotNull
    @Contract(pure = true)
    public Collection<AdministratorPermission> getAuthorities() {
        return authorities;
    }

    public AdministratorDto setAuthorities(Collection<String> authorities) {
        this.authorities = authorities.stream().map(AdministratorPermission::valueOf).collect(Collectors.toSet());
        return this;
    }
}
