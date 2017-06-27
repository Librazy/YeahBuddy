package cn.edu.xmu.yeahbuddy.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

public class AdministratorDto implements Serializable {

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private Collection<String> authorities;

    public String getPassword() {
        return password;
    }

    public AdministratorDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return name;
    }

    public AdministratorDto setName(String name) {
        this.name = name;
        return this;
    }

    public Collection<String> getAuthorities() {
        return authorities;
    }

    public AdministratorDto setAuthorities(Collection<String> authorities) {
        this.authorities = authorities;
        return this;
    }
}
