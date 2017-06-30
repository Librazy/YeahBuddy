package cn.edu.xmu.yeahbuddy.utils;

import org.springframework.security.core.AuthenticationException;

public class UsernameAlreadyExistsException extends AuthenticationException {

    private static final long serialVersionUID = -2871246435916059680L;

    public UsernameAlreadyExistsException(String msg) {
        super(msg);
    }

    public UsernameAlreadyExistsException(String msg, Throwable t) {
        super(msg, t);
    }
}
