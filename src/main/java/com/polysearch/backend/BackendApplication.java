package com.polysearch.backend;

import com.polysearch.backend.config.JsonMessageSource; // Added for CLI i18n
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import java.util.Locale;

// Imports for the Language Switcher
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; 
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication implements WebMvcConfigurer { // Implements WebMvcConfigurer for interceptor

	// 1. BEAN: Registers our custom JSON MessageSource for static translations
    @Bean
    public MessageSource messageSource() {
        return new JsonMessageSource();
    }

	// 2. BEAN: Determines language based on the browser's Accept-Language header
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    // 3. BEAN: Creates the interceptor needed to process the ?lang= parameter from the switcher
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        // Tells Spring to look for "?lang=XX" in the URL
        lci.setParamName("lang"); 
        return lci;
    }

    // 4. METHOD: Registers the interceptor so Spring runs it on every request
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}