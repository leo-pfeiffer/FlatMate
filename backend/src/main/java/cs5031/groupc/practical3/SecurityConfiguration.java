package cs5031.groupc.practical3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import com.google.gson.Gson;
import cs5031.groupc.practical3.vo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    final
    DataSource dataSource;

    @Autowired
    public SecurityConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select username,password, enabled from user where username=?")
                .authoritiesByUsernameQuery("select username, role from user where username=?");
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/api").permitAll()
                .antMatchers("/test").permitAll()
                .antMatchers("/swagger-ui**").permitAll()
                .antMatchers("/api/user/create").permitAll()
                .antMatchers("/api/group/add").hasAnyAuthority(UserRole.ADMIN.getRole())
                .antMatchers("/api/group/remove").hasAnyAuthority(UserRole.ADMIN.getRole())
                .antMatchers("/api/group/changeAdmin").hasAnyAuthority(UserRole.ADMIN.getRole())
                .antMatchers("/api/**").hasAnyAuthority(UserRole.USER.getRole(), UserRole.ADMIN.getRole())
                .and().httpBasic()
                .and().formLogin().successHandler(successHandler()).failureHandler(failureHandler())
                .and().cors().and().csrf().disable();

    }

    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {

            // return a json response to the user containing the list of authorities of the user
            HashMap<String, ArrayList<String>> res = new HashMap<>();

            ArrayList<String> roles = new ArrayList<>();
            for (GrantedAuthority a : authentication.getAuthorities()) {
                roles.add(a.getAuthority());
            }
            res.put("roles", roles);

            String json = new Gson().toJson(res);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
            response.setStatus(200);
        };
    }

    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            // user could not be authenticated
            response.getWriter().append("Authentication failed");
            response.setStatus(401);
        };
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        // NoOpPasswordEncoder is deprecated for security reasons but is fine for out purposes
        //noinspection deprecation
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}