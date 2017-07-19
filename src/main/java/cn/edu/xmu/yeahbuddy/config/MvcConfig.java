package cn.edu.xmu.yeahbuddy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Spring MVC 配置
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 注入 LocalizedErrorAttributes 到 ErrorAttributes Bean
     *
     * @param messageSource Autowired
     * @return LocalizedErrorAttributes
     */
    @Bean
    @Autowired
    public ErrorAttributes errorAttributes(MessageSource messageSource) {
        return new LocalizedErrorAttributes(messageSource);
    }

    /**
     * 注入 CookieLocaleResolver 到 LocaleResolver Bean
     * <p>
     * 使用Cookie判断当前locale
     *
     * @return CookieLocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }

    /**
     * 注入 LocaleChangeInterceptor
     * <p>
     * 忽略无效Locale
     *
     * @return LocaleChangeInterceptor
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setIgnoreInvalidLocale(true);
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //静态资源路径映射
        registry
                .addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}