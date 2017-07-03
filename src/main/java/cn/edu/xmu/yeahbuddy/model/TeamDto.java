package cn.edu.xmu.yeahbuddy.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.jetbrains.annotations.Contract;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TeamDto implements Serializable {

    private static final long serialVersionUID = -9091761778673434209L;

    private String password;

    private String name;

    private String projectName;

    private String phone;

    private String email;

    @Contract(pure = true)
    public String getPassword() {
        return password;
    }

    public TeamDto setPassword(String password) {
        this.password = password;
        return this;
    }

    @Contract(pure = true)
    public String getName() {
        return name;
    }

    public TeamDto setName(String name) {
        this.name = name;
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
}
