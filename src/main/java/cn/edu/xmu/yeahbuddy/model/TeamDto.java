package cn.edu.xmu.yeahbuddy.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

public class TeamDto implements Serializable {

    private static final long serialVersionUID = -9091761778673434209L;

    private String password;

    private String username;

    private String displayName;

    private String projectName;

    private String phone;

    private String email;

    public String getDisplayName() {
        return displayName;
    }

    public TeamDto setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Contract(pure = true)
    public String getPassword() {
        return password;
    }

    public TeamDto setPassword(String password) {
        this.password = password;
        return this;
    }

    @NonNls
    @Contract(pure = true)
    public String getUsername() {
        return username;
    }

    public TeamDto setUsername(String username) {
        this.username = username;
        return this;
    }

    @Contract(pure = true)
    public String getProjectName() {
        return projectName;
    }

    public TeamDto setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    @Contract(pure = true)
    public String getPhone() {
        return phone;
    }

    public TeamDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Contract(pure = true)
    public String getEmail() {
        return email;
    }

    public TeamDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public boolean ready(){
        return Stream.of(password, username, displayName, projectName)
                     .allMatch(Objects::nonNull);
    }
}
