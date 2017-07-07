package cn.edu.xmu.yeahbuddy.utils;


import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.Locale;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class IdentifierAlreadyExistsException extends RuntimeException implements MessageSourceAware {

    private static final long serialVersionUID = -2871246435916059680L;

    private final Object id;

    @NonNls
    @Resource(name = "messageSource")
    private MessageSource messageSource;

    public IdentifierAlreadyExistsException(String msg, Object id) {
        super(msg);
        this.id = id;
    }

    public IdentifierAlreadyExistsException(String msg, Object id, Throwable t) {
        super(msg, t);
        this.id = id;
    }

    @Override
    public String getLocalizedMessage(){
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(getMessage(), new Object[]{id}, locale);
        } catch (Exception e){
            return getMessage();
        }
    }

    @Override
    public void setMessageSource(@NotNull MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
