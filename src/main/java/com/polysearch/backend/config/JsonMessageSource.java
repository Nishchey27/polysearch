package com.polysearch.backend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.env.Environment; // NEW IMPORT
import org.springframework.context.EnvironmentAware; // NEW IMPORT
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonMessageSource extends AbstractMessageSource implements EnvironmentAware { // IMPLEMENTS INTERFACE

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Locale, Map<String, String>> messagesCache = new ConcurrentHashMap<>();
    
    private Environment environment; // NEW FIELD
    private Path projectRootPath; // NEW FIELD
    
    // NEW METHOD: Spring calls this immediately to give us the environment
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        // 1. Get the current working directory from the environment
        String cwd = environment.getProperty("user.dir");
        // 2. Combine the working directory with the i18n folder name
        this.projectRootPath = Paths.get(cwd, "i18n");
        
        System.out.println("--- i18n FINAL DIAGNOSTIC --- Base Path Resolved To: " + this.projectRootPath.toAbsolutePath());
    }

    private Map<String, String> getMessages(Locale locale) {
    // This method checks the cache first (messagesCache)
    // If the locale is missing, it calls loadMessages to read the file from disk.
    return messagesCache.computeIfAbsent(locale, this::loadMessages);
}

    private Map<String, String> loadMessages(Locale locale) {
        // Use the reliably resolved projectRootPath to build the full path
        Path jsonPath = projectRootPath.resolve(locale.getLanguage() + ".json");
        
        // This is the new, clean diagnostic check
        // System.out.println("Attempting to load: " + jsonPath.toAbsolutePath());

        if (!Files.exists(jsonPath)) { 
            // ... (rest of the fallback code remains the same)
            if (!locale.equals(Locale.ENGLISH)) {
                return getMessages(Locale.ENGLISH);
            }
            return new HashMap<>();
        }

        try {
            String jsonContent = new String(Files.readAllBytes(jsonPath));
            return objectMapper.readValue(jsonContent, new TypeReference<Map<String, String>>() {});
        
        } catch (Exception e) {
            System.err.println("Could not load i18n file: " + jsonPath);
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    // (Other methods like resolveCode, getMessages remain the same)
    protected MessageFormat resolveCode(String code, Locale locale) {
        Map<String, String> localeMessages = getMessages(locale);
        String message = localeMessages.get(code);

        if (message == null) {
            Map<String, String> defaultMessages = getMessages(Locale.ENGLISH);
            message = defaultMessages.get(code);
        }

        return (message != null) ? new MessageFormat(message, locale) : null;
    }
}