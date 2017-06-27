package cn.edu.xmu.yeahbuddy.utils;

import cn.edu.xmu.yeahbuddy.domain.AdministratorPermission;
import org.springframework.security.core.AuthenticationException;

import java.util.Collection;

public class AdministratorNoPermissionException extends AuthenticationException {

    public Collection<AdministratorPermission> getLackedPermission() {
        return lacks;
    }

    private final Collection<AdministratorPermission> lacks;

    public AdministratorNoPermissionException(String msg, Collection<AdministratorPermission> lacks) {
        super(msg);
        this.lacks = lacks;
    }


    public AdministratorNoPermissionException(String msg, Throwable t, Collection<AdministratorPermission> lacks) {
        super(msg, t);
        this.lacks = lacks;
    }
}
