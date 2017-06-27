package cn.edu.xmu.yeahbuddy.utils;

import org.springframework.security.core.AuthenticationException;

public class UsernameAlreadyExistsException extends AuthenticationException {

    public UsernameAlreadyExistsException(String msg) {
        super(msg);
    }


    public UsernameAlreadyExistsException(String msg, Throwable t) {
        super(msg, t);
    }
}
