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

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException implements MessageSourceAware {
    private static final long serialVersionUID = 6544184571154412143L;

    private final Object notFound;

    @NonNls
    @Resource(name = "messageSource")
    private MessageSource messageSource;

    public ResourceNotFoundException(String msg, Object notFound) {
        super(msg);
        this.notFound = notFound;
    }

    public ResourceNotFoundException(String msg, Object notFound, Throwable t) {
        super(msg, t);
        this.notFound = notFound;
    }

    @Override
    public String getLocalizedMessage() {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(getMessage(), new Object[]{notFound}, locale);
        } catch (Exception e) {
            return getMessage();
        }
    }

    @Override
    public void setMessageSource(@NotNull MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
