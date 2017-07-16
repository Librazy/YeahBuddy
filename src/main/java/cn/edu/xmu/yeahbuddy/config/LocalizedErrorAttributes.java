package cn.edu.xmu.yeahbuddy.config;

/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 本地化错误标签
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LocalizedErrorAttributes
        implements ErrorAttributes, HandlerExceptionResolver, Ordered {

    @NonNls
    private static final String ERROR_ATTRIBUTE = LocalizedErrorAttributes.class.getName()
                                                          + ".ERROR";
    @NonNls
    private final MessageSource messageSource;

    /**
     * 构造LocalizedErrorAttributes
     *
     * @param messageSource Autowired
     */
    @Autowired
    public LocalizedErrorAttributes(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public ModelAndView resolveException(@NotNull HttpServletRequest request,
                                         @NotNull HttpServletResponse response, Object handler, @NotNull Exception ex) {
        storeErrorAttributes(request, ex);
        return null;
    }

    private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
        request.setAttribute(ERROR_ATTRIBUTE, ex);
    }

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest,
                                                  boolean includeStackTrace) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("timestamp", new Date());
        addStatus(errorAttributes, webRequest, locale);
        addErrorDetails(errorAttributes, webRequest, includeStackTrace, locale);
        addPath(errorAttributes, webRequest);
        return errorAttributes;
    }

    private void addStatus(Map<String, Object> errorAttributes,
                           RequestAttributes requestAttributes,
                           Locale locale) {
        Integer status = getAttribute(requestAttributes,
                "javax.servlet.error.status_code");
        if (status == null) {
            errorAttributes.put("status", 999);
            errorAttributes.put("error", messageSource.getMessage("http.status.none", new Object[]{}, locale));
            return;
        }
        errorAttributes.put("status", status);
        try {
            errorAttributes.put("error", messageSource.getMessage("http.status." + status, new Object[]{}, locale));
        } catch (Exception ex) {
            // Unable to obtain a reason
            errorAttributes.put("error", messageSource.getMessage("http.status.code", new Object[]{status}, locale));
        }
        try {
            errorAttributes.put("detail", messageSource.getMessage("http.status." + status + ".detail", new Object[]{}, locale));
        } catch (Exception ignored) {
        }
    }

    private void addErrorDetails(Map<String, Object> errorAttributes,
                                 WebRequest webRequest,
                                 boolean includeStackTrace,
                                 Locale locale) {
        Throwable error = getError(webRequest);
        if (error != null) {
            while (error instanceof ServletException && error.getCause() != null) {
                error = error.getCause();
            }
            addErrorMessage(errorAttributes, error, locale);
            if (includeStackTrace) {
                addStackTrace(errorAttributes, error);
            }
        }
        Object message = getAttribute(webRequest, "javax.servlet.error.message");
        if ((!StringUtils.isEmpty(message) || errorAttributes.get("message") == null)
                    && !(error instanceof BindingResult)) {
            if (errorAttributes.get("detail") != null) {
                errorAttributes.put("message", errorAttributes.get("detail"));
            } else {
                errorAttributes.put("message",
                        StringUtils.isEmpty(message) ? messageSource.getMessage("http.message.none", new Object[]{}, locale) : message);
            }
        }
    }

    private void addErrorMessage(Map<String, Object> errorAttributes,
                                 Throwable error,
                                 Locale locale) {
        BindingResult result = extractBindingResult(error);
        if (result == null) {
            if (error instanceof MessageSourceAware) {
                ((MessageSourceAware) error).setMessageSource(messageSource);
            }
            String message = error.getLocalizedMessage();
            errorAttributes.put("message", message);
            return;
        }
        if (result.getErrorCount() > 0) {
            errorAttributes.put("errors", result.getAllErrors());
            errorAttributes.put("message",
                    messageSource.getMessage("http.message.validation", new Object[]{result.getObjectName(), result.getErrorCount()}, locale));
        } else {
            errorAttributes.put("message", messageSource.getMessage("http.message.no_error", new Object[]{}, locale));
        }
    }

    private BindingResult extractBindingResult(Throwable error) {
        if (error instanceof BindingResult) {
            return (BindingResult) error;
        }
        if (error instanceof MethodArgumentNotValidException) {
            return ((MethodArgumentNotValidException) error).getBindingResult();
        }
        return null;
    }

    private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        errorAttributes.put("trace", stackTrace.toString());
    }

    private void addPath(Map<String, Object> errorAttributes,
                         RequestAttributes requestAttributes) {
        String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
        if (path != null) {
            errorAttributes.put("path", path);
        }
    }

    @Override
    public Throwable getError(WebRequest webRequest) {
        Throwable exception = getAttribute(webRequest, ERROR_ATTRIBUTE);
        if (exception == null) {
            exception = getAttribute(webRequest, "javax.servlet.error.exception");
        }
        return exception;
    }

    @SuppressWarnings("unchecked")
    private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

}
