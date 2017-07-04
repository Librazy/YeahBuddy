package cn.edu.xmu.yeahbuddy.model;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

public class TutorDto implements Serializable {

    private static final long serialVersionUID = -280720458367760627L;

    private String password;

    private String username;

    private String displayName;

    private String phone;

    private String email;

    @Contract(pure = true)
    public String getDisplayName() {
        return displayName;
    }

    public TutorDto setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Contract(pure = true)
    public String getPassword() {
        return password;
    }

    public TutorDto setPassword(String password) {
        this.password = password;
        return this;
    }

    @Contract(pure = true)
    public String getUsername() {
        return username;
    }

    public TutorDto setUsername(String username) {
        this.username = username;
        return this;
    }

    @Contract(pure = true)
    public String getPhone() {
        return phone;
    }

    public TutorDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Contract(pure = true)
    public String getEmail() {
        return email;
    }

    public TutorDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public boolean ready(){
        return Stream.of(password, username, displayName)
                     .allMatch(Objects::nonNull);
    }
}
