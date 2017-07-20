package cn.edu.xmu.yeahbuddy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    private static final long serialVersionUID = -7619090069297505818L;

    @NonNls
    private static final String ROLE_TEAM = "ROLE_TEAM";

    @Id
    @GeneratedValue
    @Column(name = "TeamId", unique = true, updatable = false, nullable = false)
    private int id = Integer.MIN_VALUE;

    @Column(name = "TeamPassword", nullable = false)
    @JsonIgnore
    private String password;

    @NonNls
    @NaturalId(mutable = true)
    @Column(name = "TeamUsername", unique = true, nullable = false)
    private String username;

    @Column(name = "TeamDisplayName", nullable = false)
    private String displayName;

    @Column(name = "TeamProjectName", nullable = false)
    private String projectName;

    @Column(name = "TeamPhone")
    private String phone;

    @Column(name = "TeamEmail")
    private String email;

    public Team() {
    }

    public Team(String username, String password) {
        this.password = password;
        this.username = username;
        this.displayName = username;
        this.projectName = "";
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
    @Contract(pure = true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    @Contract(pure = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    @Contract(pure = true)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Contract(pure = true)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Contract(pure = true)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Contract(pure = true)
    public boolean isEnabled() {
        return true;
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
        return rhs instanceof Team && username.equals(((Team) rhs).getUsername());
    }
}
