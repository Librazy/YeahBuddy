package cn.edu.xmu.yeahbuddy.domain;

import org.jetbrains.annotations.Contract;

import javax.persistence.*;
import java.util.Collection;

@Entity
public final class Administrator {

    @Id
    @GeneratedValue
    @Column(name = "AdministratorId", updatable = false, nullable = false)
    private int id;

    @Column(name = "AdministratorPassword", nullable = false)
    private String password;

    @Column(name = "AdministratorSalt", nullable = false)
    private String salt;

    @Column(name = "AdministratorName", nullable = false)
    private String name;

    @ElementCollection(targetClass = AdministratorPermission.class)
    @JoinTable(name = "AdministratorPermissions", joinColumns = @JoinColumn(name = "AdministratorId"))
    @Column(name = "AdministratorPermission", nullable = false)
    @Enumerated(EnumType.STRING)
    private Collection<AdministratorPermission> permissions;

    public Administrator() {
    }

    public Administrator(String name, String password, String salt) {
        this.name = name;
        this.password = password;
        this.salt = salt;
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

    public Collection<AdministratorPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<AdministratorPermission> permissions) {
        this.permissions = permissions;
    }
}

