package cs5031.groupc.practical3;

import javax.sql.DataSource;

import cs5031.groupc.practical3.vo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

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
                .antMatchers("/api/user/create").permitAll()
                .antMatchers("/api/group/add").hasAnyAuthority(UserRole.ADMIN.getRole())
                .antMatchers("/api/group/remove").hasAnyAuthority(UserRole.ADMIN.getRole())
                .antMatchers("/api/group/changeAdmin").hasAnyAuthority(UserRole.ADMIN.getRole())
                .antMatchers("/api/**").hasAnyAuthority(UserRole.USER.getRole(), UserRole.ADMIN.getRole())
                .and().formLogin()
                .and().cors().and().csrf().disable();
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
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}