package in.andonsystem.v2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter implements EnvironmentAware{
    private static String REALM="MY_OAUTH_REALM";
    private static final String ENV_OAUTH2 = "security.oauth2.client.";
    
    private RelaxedPropertyResolver propertyResolver;
    
    @Autowired
    private TokenStore tokenStore;
 
    @Autowired
    private AuthenticationManager authenticationManager;
 
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
 
        clients.inMemory()
            .withClient(propertyResolver.getProperty("client1-id"))
            .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
            .scopes(propertyResolver.getProperty("client1-scope"))
            .secret(propertyResolver.getProperty("client1-secret"))
            .accessTokenValiditySeconds(propertyResolver.getProperty("access-token.validity.sec", Integer.class, 300))
            .refreshTokenValiditySeconds(propertyResolver.getProperty("refresh-token.validity.sec", Integer.class, 600));
    }
 
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager);
    }
 
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.realm(REALM+"/client");
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_OAUTH2);
    }
}
