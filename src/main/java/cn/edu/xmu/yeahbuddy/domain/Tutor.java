package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;
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

    @Id
    @GeneratedValue
    @Column(name = "TutorId", updatable = false, nullable = false)
    private int id;

    @Column(name = "TutorPassword", nullable = false)
    private String password;

    @Column(name = "TutorSalt", nullable = false)
    private String salt;

    @Column(name = "TutorName", nullable = false)
    private String name;

    @Column(name = "TutorPhone")
    private String phone;

    @Column(name = "TutorEmail")
    private String email;

    public Tutor() {
    }

    public Tutor(String password, String salt, String name) {
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
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_TUTOR"));
    }

    @Override
    @Contract(pure = true)
    public String getUsername() {
        return getName();
    }

    @Override
    @Contract(pure = true)
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    @Contract(pure = true)
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    @Contract(pure = true)
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    @Contract(pure = true)
    public boolean isEnabled() {
        return true;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof Tutor && id == ((Tutor) rhs).id;
    }
}
