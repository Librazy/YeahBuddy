package cn.edu.xmu.yeahbuddy.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TeamDto implements Serializable {

    private static final long serialVersionUID = -9091761778673434209L;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String projectName;

    @NotNull
    @NotEmpty
    private String phone;

    @NotNull
    @NotEmpty
    private String email;

    public String getPassword() {
        return password;
    }

    public TeamDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return name;
    }

    public TeamDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }

    public TeamDto setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public TeamDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public TeamDto setEmail(String email) {
        this.email = email;
        return this;
    }
}
