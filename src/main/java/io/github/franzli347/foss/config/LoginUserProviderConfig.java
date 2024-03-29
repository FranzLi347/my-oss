package io.github.franzli347.foss.config;

import io.github.franzli347.foss.support.userSupport.LoginUserProvider;
import io.github.franzli347.foss.support.userSupport.LoginSysUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author FranzLi
 */
@Configuration
public class LoginUserProviderConfig {
    @Bean
    public LoginUserProvider loginUserProvider() {
        return new LoginSysUserProvider();
    }
}
