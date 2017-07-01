package cn.edu.xmu.yeahbuddy.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.jetbrains.annotations.Contract;

import javax.validation.constraints.NotNull;

public class TutorDto {

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String phone;

    @NotNull
    @NotEmpty
    private String email;

    @Contract(pure = true)
    public String getPassword() {
        return password;
    }

    public TutorDto setPassword(String password) {
        this.password = password;
        return this;
    }

    @Contract(pure = true)
    public String getName() {
        return name;
    }

    public TutorDto setName(String name) {
        this.name = name;
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
}
