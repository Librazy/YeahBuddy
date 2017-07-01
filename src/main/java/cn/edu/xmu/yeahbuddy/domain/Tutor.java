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
public class Tutor implements UserDetails {

    private static final long serialVersionUID = -3232545785012376249L;

    @NonNls
    private static final String ROLE_TUTOR = "ROLE_TUTOR";

    @Id
    @GeneratedValue
    @Column(name = "TutorId", unique = true, updatable = false, nullable = false)
    private int id;

    @Column(name = "TutorPassword", nullable = false)
    private String password;

    @NonNls
    @NaturalId
    @Column(name = "TutorName", unique = true, nullable = false)
    private String name;

    @Column(name = "TutorPhone")
    private String phone;

    @Column(name = "TutorEmail")
    private String email;

    public Tutor() {
    }

    public Tutor(String name, String password) {
        this.password = password;
        this.name = name;
    }

    @Contract(pure = true)
    public int getId() {
        return id;
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
    @Contract(pure = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(ROLE_TUTOR));
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
        return getName();
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Contract(pure = true)
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(name).
                toHashCode();
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof Tutor && name.equals(((Tutor) rhs).getName());
    }
}
