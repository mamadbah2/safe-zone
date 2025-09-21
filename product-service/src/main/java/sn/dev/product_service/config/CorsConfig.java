//package sn.dev.product_service.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//
//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // Allow specific origins
//        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
//
//        // Allow specific HTTP methods
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//
//        // Allow specific headers
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//
//        // Allow credentials
//        configuration.setAllowCredentials(true);
//
//        // Expose headers
//        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
//
//        // Max age for preflight requests
//        configuration.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
//}
