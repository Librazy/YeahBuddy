package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
public class Team implements UserDetails {

    @Id
    @Column(name = "TeamId", updatable = false, nullable = false)
    private int id;

    @Column(name = "TeamPassword", nullable = false)
    private String password;

    @Column(name = "TeamSalt", nullable = false)
    private String salt;

    @Column(name = "TeamName", nullable = false)
    private String name;

    @Column(name = "TeamProjectName", nullable = false)
    private String projectName;

    @Column(name = "TeamPhone")
    private String phone;

    @Column(name = "TeamEmail")
    private String email;

    public Team() {
    }

    public Team(int id, String password, String salt, String name) {
        this.id = id;
        this.password = password;
        this.salt = salt;
        this.name = name;
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

    @Contract(pure = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Contract(pure = true)
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Contract(pure = true)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Contract(pure = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEAM"));
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
        return rhs instanceof Team && id == ((Team) rhs).id;
    }
}
