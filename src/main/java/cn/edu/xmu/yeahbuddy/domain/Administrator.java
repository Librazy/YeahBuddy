package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
public final class Administrator implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "AdministratorId", updatable = false, nullable = false)
    private int id;

    @Column(name = "AdministratorPassword", nullable = false)
    private String password;

    @Column(name = "AdministratorSalt", nullable = false)
    private String salt;

    @Column(name = "AdministratorName", nullable = false)
    private String name;

    @ElementCollection(targetClass = AdministratorPermission.class)
    @JoinTable(name = "AdministratorPermissions", joinColumns = @JoinColumn(name = "AdministratorId"))
    @Column(name = "AdministratorPermission", nullable = false)
    @Enumerated(EnumType.STRING)
    private Collection<AdministratorPermission> authorities;

    public Administrator() {
    }

    public Administrator(String name, String password, String salt) {
        this.name = name;
        this.password = password;
        this.salt = salt;
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

    @Contract(pure = true)
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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


    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof Administrator && id == ((Administrator) rhs).id;
    }
}

