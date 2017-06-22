package cn.edu.xmu.yeahbuddy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Team {

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

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
