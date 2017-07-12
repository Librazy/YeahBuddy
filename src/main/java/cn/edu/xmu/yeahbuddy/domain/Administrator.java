package cn.edu.xmu.yeahbuddy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NaturalId;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Entity
public final class Administrator implements UserDetails, Authentication {

    private static final long serialVersionUID = 6445115560332842675L;

    @Id
    @GeneratedValue
    @Column(name = "AdministratorId", unique = true, updatable = false, nullable = false)
    private int id = Integer.MIN_VALUE;

    @Column(name = "AdministratorPassword", nullable = false)
    @JsonIgnore
    private String password;

    @NonNls
    @NaturalId(mutable = true)
    @Column(name = "AdministratorUsername", unique = true, nullable = false)
    private String username;

    @Column(name = "AdministratorDisplayName", nullable = false)
    private String displayName;

    @ElementCollection(targetClass = AdministratorPermission.class, fetch = FetchType.EAGER)
    @JoinTable(name = "AdministratorPermissions", joinColumns = @JoinColumn(name = "AdministratorId"))
    @Column(name = "AdministratorPermission", nullable = false)
    @Enumerated(EnumType.STRING)
    private Collection<AdministratorPermission> authorities;

    public Administrator() {
    }

    public Administrator(String username, String password) {
        this.username = username;
        this.password = password;
        this.displayName = username;
        this.authorities = new HashSet<>();
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Contract(pure = true)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
    @Contract(pure = true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @Override
    @Contract(pure = true)
    public String getName() {
        return username;
    }

    @Override
    @Contract(pure = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Contract(pure = true)
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                                                  append(username).
                                                                          toHashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof Administrator && username.equals(((Administrator) rhs).getUsername());
    }

    @Contract(pure = true)
    @Override
    public String getCredentials() {
        return getName();
    }

    @NotNull
    @Contract(pure = true)
    @Override
    public Object getDetails() {
        return getId();
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

