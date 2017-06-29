package cn.edu.xmu.yeahbuddy.domain;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NaturalId;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
public final class Administrator implements UserDetails, Authentication {

    private static final long serialVersionUID = 6445115560332842675L;

    @Id
    @GeneratedValue
    @Column(name = "AdministratorId", unique = true, updatable = false, nullable = false)
    private int id = Integer.MIN_VALUE;

    @Column(name = "AdministratorPassword", nullable = false)
    private String password;

    @NonNls
    @NaturalId
    @Column(name = "AdministratorName", unique = true, nullable = false)
    private String name;

    @ElementCollection(targetClass = AdministratorPermission.class, fetch = FetchType.EAGER)
    @JoinTable(name = "AdministratorPermissions", joinColumns = @JoinColumn(name = "AdministratorId"))
    @Column(name = "AdministratorPermission", nullable = false)
    @Enumerated(EnumType.STRING)
    private Collection<AdministratorPermission> authorities;

    public Administrator() {
    }

    public Administrator(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Override
    @Contract(pure = true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    @Contract(pure = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @Contract(pure = true)
    public Collection<AdministratorPermission> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<AdministratorPermission> authorities) {
        this.authorities = authorities;
    }


    @Contract(pure = true)
    @Override
    public String getUsername() {
        return getName();
    }

    @Contract(pure = true)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Contract(pure = true)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Contract(pure = true)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Contract(pure = true)
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof Administrator && name.equals(((Administrator) rhs).getName());
    }

    @Contract(pure = true)
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                                                  append(name).
                                                  toHashCode();
    }

    @Contract(pure = true)
    @Override
    public String getCredentials() {
        return getName();
    }

    @Override
    public Object getDetails() {
        return getName();
    }

    @Contract(pure = true)
    @Override
    public String getPrincipal() {
        return getName();
    }

    @Contract(pure = true)
    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Contract("false -> fail")
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (!isAuthenticated) {
            throw new IllegalArgumentException();
        }
    }
}

