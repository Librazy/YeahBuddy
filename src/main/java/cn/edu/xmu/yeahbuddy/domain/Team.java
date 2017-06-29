package cn.edu.xmu.yeahbuddy.domain;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NaturalId;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.Collections;

@Entity
public class Team implements UserDetails {

    @NonNls
    private static final String ROLE_TEAM = "ROLE_TEAM";

    private static final long serialVersionUID = -7619090069297505818L;

    @Id
    @GeneratedValue
    @Column(name = "TeamId", unique = true, updatable = false, nullable = false)
    private int id = Integer.MIN_VALUE;

    @Column(name = "TeamPassword", nullable = false)
    private String password;

    @NonNls
    @NaturalId
    @Column(name = "TeamName", unique = true, nullable = false)
    private String name;

    @Column(name = "TeamProjectName", nullable = false)
    private String projectName;

    @Column(name = "TeamPhone")
    private String phone;

    @Column(name = "TeamEmail")
    private String email;

    public Team() {
    }

    public Team(String name, String password) {
        this.password = password;
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
        return Collections.singletonList(new SimpleGrantedAuthority(ROLE_TEAM));
    }

    @Override
    public String getUsername() {
        return getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof Team && name.equals(((Team) rhs).getName());
    }

    @Contract(pure = true)
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                                                  append(name).
                                                  toHashCode();
    }
}
