package cs5031.groupc.practical3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class Practical3Application {

    /**
     * The main method of the program.
     *
     * @param args The arguments given at the call - not necessary.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Practical3Application.class, args);
    }


    /**
     * The cors configurer, necessary to enable cors and connect to the SPA frontend.
     *
     * @return A WebMvcConfigurer.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(final CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("POST", "GET");
            }
        };
    }
}
