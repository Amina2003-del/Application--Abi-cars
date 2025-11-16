package ma.abisoft.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

import location_voiture.service.CustomOAuth2UserService;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.security.CustomRememberMeServices;
import ma.abisoft.security.google2fa.CustomAuthenticationProvider;
import ma.abisoft.security.google2fa.CustomWebAuthenticationDetailsSource;

@Configuration
@ComponentScan(basePackages = { "ma.abisoft.security" })
// @ImportResource({ "classpath:webSecurityConfig.xml" })
@EnableWebSecurity
public class SecSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private LogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    @Lazy
    private CustomOAuth2UserService customOAuth2UserService;

    public SecSecurityConfig() {
        super();
    }

    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**","/webjars/**",
        		"/email_templates/**",
                "/resources/**",
                "/img/**",
                "/images/**",
                "/contactform/**",
                "/font/**",
                "/fonts/**",
                "/ico/**",
                "/css/**",
                "/css1/**",
                "/favicon.ico",
                "/vendor/**",
                "/img1/**",
                "/font-awesome/**",
                "/scss/**",
                "/js1/**",
                "/skins/**",
                "/js/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
    http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/portfolio","/services","/about-us","/blog","/project-item","/blog-item","/contact-us","/produits","/","/expiredAccount1*","/icons*","/icon-variations*","/animations*","/typography*","/components*","/table*","/index-alt3*","/index-alt2*","/","/404*","/testimonials*","/pricingbox*","/index-alt2*","/index*","/index", "/logout*", "/signin/**", "/signup/**", "/customLogin",
                		"/access-denied*","/post-right-sidebar*","/post-left-sidebar*","/blog-right-sidebar*","/blog-left-sidebar*","/contact*","/portfolio-detail*","/portfolio-4cols*","/portfolio-3cols*","/portfolio-2cols*","/testimonials*","/pricingbox*","/about*","/user/registration*", "/registrationConfirm*", "/expiredAccount*", "/registration*",
                        "/badUser*", "/user/resendRegistrationToken*" ,"/api/user","/profille","/proprofile","/forgetPassword*", "/user/resetPassword*","/Administrateur/ajoutervoiture","/ajoutervoiture*","/proprietaire/dashboard*","/proprietaire/Listevoiture*",
                        "/user/changePassword*", "/emailError*","/ajouter","/test","/Voitures/**","/Siteofficiel/**","/register","/visitor/**", "/register", "/login",
                        "/assets/**",  "/resources/**","/Siteoffeciel/**","/create*","/VoitureDesp","/Siteofficiel/index*","/ForgotPassword/**","/ForgotPassword/**","/factures/download","/villes/search","/reset-password","/Partenariat/**","/Admin/forgot_password","/Admin/**","/resources/static/**","/api/reservations/**","/paypal/checkout","/api/cars/updateWithImages/**","/api/cars/update/**","/api/cars/get/**","/api/cars/approve/**","/api/cars/approve/**","/api/cars/pending/**","/api/cars/reject/**","/api/cars","/message","/resources/static/Admin**","/old/user/registration*","/successRegister*","/qrcode*","/webjars/**").permitAll()
                .antMatchers("/index").anonymous()
                .antMatchers("/Administrateur/**").hasRole("ADMIN")
                .antMatchers("/Owner/**").hasRole("OWNER")
                .antMatchers("/Clientes/**").hasRole("CLIENT")

 // besoin du préfixe complet
               // .antMatchers("/Clientes/**","/Client/index*","/user/updatePassword*","/user/savePassword*","/updatePassword*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                .anyRequest().hasAuthority("READ_PRIVILEGE")
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/index", true) 
                .failureUrl("/login?error=true")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .authenticationDetailsSource(authenticationDetailsSource)
            .permitAll()

                .and()
  .oauth2Login()
                .loginPage("/login") // même page que formLogin
                .defaultSuccessUrl("/index", true)
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
            .and()
                .permitAll()
            .and()
            .sessionManagement()
                .invalidSessionUrl("/invalidSession.html")
                .maximumSessions(1).sessionRegistry(sessionRegistry()).and()
                .sessionFixation().migrateSession()
            .and()
            .logout()
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/logout.html?logSucc=true")
                .deleteCookies("JSESSIONID")
                .permitAll()
             .and()
                .rememberMe().rememberMeServices(rememberMeServices()).key("theKey");
    // @formatter:on
    }

    // beans

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        CustomRememberMeServices rememberMeServices = new CustomRememberMeServices("theKey", userDetailsService, new InMemoryTokenRepositoryImpl());
        return rememberMeServices;
    }
    

}