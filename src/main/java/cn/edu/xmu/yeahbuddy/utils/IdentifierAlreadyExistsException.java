package cn.edu.xmu.yeahbuddy.utils;

import org.springframework.security.core.AuthenticationException;

public class IdentifierAlreadyExistsException extends AuthenticationException {

    private static final long serialVersionUID = -2871246435916059680L;

    public IdentifierAlreadyExistsException(String msg) {
        super(msg);
    }

    public IdentifierAlreadyExistsException(String msg, Throwable t) {
        super(msg, t);
    }
}
