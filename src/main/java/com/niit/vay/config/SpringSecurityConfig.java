package com.niit.vay.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.niit.vay.models.*;
import com.niit.vay.repositories.CartRepository;
import com.niit.vay.repositories.UserRepository;
import com.niit.vay.services.CartService;
import com.niit.vay.services.CustomOAuth2UserService;
import com.niit.vay.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final MyUserDetailsService myUserDetailsService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;


    @Autowired
    OAuth2AuthorizedClientService auth2AuthorizedClientService;

    @Autowired
    private CustomOAuth2UserService oAuth2UserService;

    @Autowired
    DataSource dataSource;

    public SpringSecurityConfig(UserDetailsService userDetailsService, PasswordEncoder bCryptPasswordEncoder, MyUserDetailsService myUserDetailsService, UserRepository userRepository, OAuth2AuthorizedClientService oAuth2AuthorizedClientService, CartRepository cartRepository, CartService cartService, HttpSession httpSession) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.myUserDetailsService = myUserDetailsService;
        this.userRepository = userRepository;

        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.inMemoryAuthentication().withUser("admined").password(bCryptPasswordEncoder.encode("admined")).authorities("ROLE_ADMIN", "ROLE_USER");
//        auth.inMemoryAuthentication().withUser("usernamed").password(bCryptPasswordEncoder.encode("usernamed")).authorities("ROLE_USER");
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/smart-accessories/login");
        httpSecurity.csrf().disable();
        httpSecurity.formLogin()
                .loginPage("/login")
                .and()
                .oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {

                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        String[] toSplit = httpServletRequest.getServletPath().split("/");
                        String provider = toSplit[toSplit.length - 1];
                        String origin = (String)httpServletRequest.getSession().getAttribute("origin");
                        System.out.println(origin + " and " + provider);
                        String sessionId = (String)httpServletRequest.getSession().getAttribute("sessionId");
                        Authentication request_authentication = (Authentication)httpServletRequest.getSession().getAttribute("principal");

                        if (origin.equals("register")) {
                            switch (provider) {
                                case "facebook":
//                                    CustomOAuth2User oAuth2User_facebook = (CustomOAuth2User) authentication.getPrincipal();
//                                    Map<String, Object> claims_facebook = oAuth2User_facebook.getAttributes();

                                    OAuth2AuthenticationToken facebookOAuthToken = (OAuth2AuthenticationToken) authentication;
                                    OAuth2AuthorizedClient client = auth2AuthorizedClientService.loadAuthorizedClient(facebookOAuthToken.getAuthorizedClientRegistrationId(), facebookOAuthToken.getName());

                                    if(!myUserDetailsService.doesUserExist((String)facebookOAuthToken.getPrincipal().getAttribute("email"))) {
                                        DaoUser facebookOAuthUser = new DaoUser();
                                        facebookOAuthUser.setUsername((String)facebookOAuthToken.getPrincipal().getAttribute("name"));
                                        facebookOAuthUser.setPassword(bCryptPasswordEncoder.encode("FacebookProvided"));
                                        facebookOAuthUser.setEmail((String)facebookOAuthToken.getPrincipal().getAttribute("email"));
                                        facebookOAuthUser.setEnabled(true);
                                        facebookOAuthUser.setProvider(Provider.FACEBOOK);
                                        userRepository.save(facebookOAuthUser);
                                        myUserDetailsService.addRoleToUser(facebookOAuthUser.getUsername(), "ROLE_USER");
                                        userRepository.save(facebookOAuthUser);

                                        Cart ssCart = cartService.lastUserCart(myUserDetailsService.getUserByEmail(sessionId));
                                        ssCart.setUser(facebookOAuthUser);
                                        cartRepository.save(ssCart);

                                        String issuer = client.getClientRegistration().getProviderDetails().getTokenUri();

                                        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                                        String access_token = JWT.create()
                                                .withSubject((String)facebookOAuthToken.getPrincipal().getName())
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .withClaim("roles", userRepository.findTop1ByEmail(facebookOAuthUser.getEmail()).getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                                                .sign(algorithm);

                                        String refresh_token = JWT.create()
                                                .withSubject((String)facebookOAuthToken.getPrincipal().getName())
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .sign(algorithm);

                                        Map<String, String> tokens = new HashMap<>();

                                        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                                        httpServletResponse.setHeader(AUTHORIZATION, "Bearer " + access_token);

                                        cartRepository.delete(cartService.lastUserCart(myUserDetailsService.getUserByEmail(sessionId)));
                                        userRepository.delete(myUserDetailsService.getUserByEmail(sessionId));

                                        httpServletRequest.getSession().invalidate();
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        httpServletResponse.sendRedirect("/");

                                    } else {

                                        DaoUser facebook_oauth2User = myUserDetailsService.getUserByEmail((String)facebookOAuthToken.getPrincipal().getAttribute("email"));
                                        String issuer = client.getClientRegistration().getProviderDetails().getTokenUri();

                                        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                                        String access_token = JWT.create()
                                                .withSubject(facebook_oauth2User.getUsername())
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .withClaim("roles", userRepository.findTop1ByEmail( facebook_oauth2User.getEmail() ).getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                                                .sign(algorithm);


                                        String refresh_token = JWT.create()
                                                .withSubject(facebook_oauth2User.getUsername())
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .sign(algorithm);

                                        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                                        httpServletResponse.setHeader(AUTHORIZATION, "Bearer " + access_token);
                                        httpServletRequest.getSession().invalidate();
//                                        cartRepository.delete(cartService.lastUserCart(myUserDetailsService.getUserByEmail(sessionId)));
//                                        userRepository.delete(myUserDetailsService.getUserByEmail(sessionId));
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        httpServletResponse.sendRedirect("/");

                                    }




                                    break;

                                case "github":
//              issuer : claims.get("url) -
                                    CustomOAuth2User oAuth2User_github = (CustomOAuth2User) authentication.getPrincipal();
                                    Map<String, Object> claims_github = oAuth2User_github.getAttributes();


                                    System.out.println("This is the intel that we need:");
                                    System.out.println("Username: " + (String)claims_github.get("login") + " - Email: " + (String) claims_github.get("email") + " url(iss): " + (String)claims_github.get("url"));

                                    if(!myUserDetailsService.doesUserExist((String) claims_github.get("email"))) {
                                        DaoUser githubOAuthUser = new DaoUser();
                                        githubOAuthUser.setUsername((String)claims_github.get("login"));
                                        githubOAuthUser.setPassword(bCryptPasswordEncoder.encode("GithubProvided"));
                                        githubOAuthUser.setEmail((String) claims_github.get("email"));
                                        githubOAuthUser.setEnabled(true);
                                        githubOAuthUser.setProvider(Provider.GITHUB);
                                        userRepository.save(githubOAuthUser);
                                        myUserDetailsService.addRoleToUser(githubOAuthUser.getUsername(), "ROLE_USER");
                                        userRepository.save(githubOAuthUser);

                                        Cart ssCart = cartService.lastUserCart(myUserDetailsService.getUserByEmail(sessionId));
                                        ssCart.setUser(githubOAuthUser);
                                        cartRepository.save(ssCart);

                                        String issuer = (String)claims_github.get("url");

                                        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                                        String access_token = JWT.create()
                                                .withSubject((String)claims_github.get("login"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .withClaim("roles", userRepository.findTop1ByEmail(githubOAuthUser.getEmail()).getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                                                .sign(algorithm);

                                        String refresh_token = JWT.create()
                                                .withSubject((String) claims_github.get("login"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .sign(algorithm);
                                        Map<String, String> tokens = new HashMap<>();

                                        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                                        httpServletResponse.setHeader(AUTHORIZATION, "Bearer " + access_token);
//                                        userRepository.delete(myUserDetailsService.getUserByEmail(httpServletRequest.getSession().getId()));
                                        httpServletRequest.getSession().invalidate();
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        httpServletResponse.sendRedirect("/");

                                    } else {
                                        DaoUser github_oAuthUser = myUserDetailsService.getUser((String) claims_github.get("email"));

                                        String issuer = (String)claims_github.get("url");

                                        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                                        String access_token = JWT.create()
                                                .withSubject((String) claims_github.get("login"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .withClaim("roles", userRepository.findTop1ByEmail( (String) claims_github.get("email")).getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                                                .sign(algorithm);


                                        String refresh_token = JWT.create()
                                                .withSubject((String) claims_github.get("login"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .sign(algorithm);
                                        Map<String, String> tokens = new HashMap<>();
//                                        tokens.put("access_token", access_token);
//                                        tokens.put("refresh_token", refresh_token);
                                        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                                        httpServletResponse.setHeader(AUTHORIZATION, "Bearer " + access_token);
                                        httpServletRequest.getSession().invalidate();
//                                        userRepository.delete(myUserDetailsService.getUserByEmail(sessionId));
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        httpServletResponse.sendRedirect("/");
                                    }

                                    break;

                                case "google":
                                    DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
                                    Map<String, Object> claims_google = oAuth2User.getIdToken().getClaims();

                                    if (!myUserDetailsService.doesUserExist((String) claims_google.get("email"))) {
                                        DaoUser googleOAuthUser = new DaoUser();
                                        googleOAuthUser.setUsername((String) claims_google.get("name"));
                                        googleOAuthUser.setPassword(bCryptPasswordEncoder.encode("GoogleProvided"));
                                        googleOAuthUser.setEmail((String) claims_google.get("email"));
                                        googleOAuthUser.setEnabled(true);
                                        googleOAuthUser.setProvider(Provider.GOOGLE);
                                        userRepository.save(googleOAuthUser);
                                        myUserDetailsService.addRoleToUser(googleOAuthUser.getUsername(), "ROLE_USER");
                                        userRepository.save(googleOAuthUser);

                                        Cart ssCart = cartService.lastUserCart(myUserDetailsService.getUserByEmail(sessionId));

                                        ssCart.setUser(googleOAuthUser);
                                        cartRepository.save(ssCart);
                                        //                            cartRepository.save(new Cart(newOAuthUser, true));

                                        String issuer = oAuth2User.getClaimAsString("iss");

                                        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                                        String access_token = JWT.create()
                                                .withSubject((String) claims_google.get("name"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .withClaim("roles", userRepository.findTop1ByEmail((String) claims_google.get("email")).getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                                                .sign(algorithm);


                                        String refresh_token = JWT.create()
                                                .withSubject((String) claims_google.get("name"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .sign(algorithm);
                                        Map<String, String> tokens = new HashMap<>();

                                        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                                        httpServletResponse.setHeader(AUTHORIZATION, "Bearer " + access_token);
                                        httpServletRequest.getSession().invalidate();
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        httpServletResponse.sendRedirect("/");
//                                        new ObjectMapper().writeValue(httpServletResponse.getOutputStream(), tokens);


                                    } else {
                                        // The user already exists in the database so i'm just going to give him the tokens
                                        DaoUser googleOAuthUser = myUserDetailsService.getUserByEmail((String)claims_google.get("email"));

                                        String issuer = oAuth2User.getIdToken().getClaimAsString("iss");

                                        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                                        String access_token = JWT.create()
                                                .withSubject((String) claims_google.get("name"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .withClaim("roles", userRepository.findTop1ByEmail((String) claims_google.get("email")).getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                                                .sign(algorithm);


                                        String refresh_token = JWT.create()
                                                .withSubject((String) claims_google.get("name"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .sign(algorithm);
                                        Map<String, String> tokens = new HashMap<>();
                                        tokens.put("access_token", access_token);
                                        tokens.put("refresh_token", refresh_token);

                                        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                                        httpServletResponse.setHeader(AUTHORIZATION, "Bearer " + access_token);
                                        httpServletRequest.getSession().invalidate();
//                                        userRepository.delete(myUserDetailsService.getUserByEmail(sessionId));
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        httpServletResponse.sendRedirect("/");

                                    }


                                    break;

                            }
                        } else if(origin.equals("login")) {

                            switch (provider) {
                                case "facebook":

                                    OAuth2AuthenticationToken facebookOAuthToken = (OAuth2AuthenticationToken) authentication.getPrincipal();
                                    OAuth2AuthorizedClient client = auth2AuthorizedClientService.loadAuthorizedClient(facebookOAuthToken.getAuthorizedClientRegistrationId(), facebookOAuthToken.getName());
                                    if (myUserDetailsService.doesUserExist((String)facebookOAuthToken.getPrincipal().getAttribute("email"))) {

                                        DaoUser facebook_oauth2User = myUserDetailsService.getUserByEmail((String)facebookOAuthToken.getPrincipal().getAttribute("email"));
                                        Cart cart = cartService.lastUserCart(facebook_oauth2User);
                                        String issuer = client.getClientRegistration().getProviderDetails().getTokenUri();
                                        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                                        String access_token = JWT.create()
                                                .withSubject(facebook_oauth2User.getUsername())
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .withClaim("roles", facebook_oauth2User.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                                                .sign(algorithm);

                                        String refresh_token = JWT.create()
                                                .withSubject(facebook_oauth2User.getUsername())
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .sign(algorithm);

                                        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                                        httpServletResponse.setHeader(AUTHORIZATION, "Bearer " + access_token);
                                        httpServletRequest.getSession().invalidate();
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        httpServletResponse.sendRedirect("/");

                                    } else {

                                        // The user is trying to log into an non-existing account
                                        //redirect him to the homepage with a status indicating that he needs to register first!
                                        System.out.println("Unregistered Facebook User - Redirect to homepage with status! - " + request_authentication.getPrincipal());
                                        SecurityContextHolder.getContext().setAuthentication(request_authentication);
                                        httpServletRequest.getSession().setAttribute("status", String.valueOf(Status.OAUTH2_USER_NOT_REGISTERED));
                                        httpServletResponse.sendRedirect("/login");

                                    }
                                    break;

                                case "github":
                                    CustomOAuth2User oAuth2User_github = (CustomOAuth2User) authentication.getPrincipal();
                                    Map<String, Object> claims_github = oAuth2User_github.getAttributes();

                                    if (myUserDetailsService.doesUserExist(oAuth2User_github.getEmail())) {

                                        DaoUser github_oauth2User = myUserDetailsService.getUserByEmail(oAuth2User_github.getEmail());
                                        Cart cart = cartService.lastUserCart(github_oauth2User);
                                        String issuer = (String)claims_github.get("url");
                                        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                                        String access_token = JWT.create()
                                                .withSubject((String)claims_github.get("login"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .withClaim("roles", github_oauth2User.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                                                .sign(algorithm);

                                        String refresh_token = JWT.create()
                                                .withSubject((String) claims_github.get("login"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .sign(algorithm);

                                        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                                        httpServletResponse.setHeader(AUTHORIZATION, "Bearer " + access_token);
                                        httpServletRequest.getSession().invalidate();
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        httpServletResponse.sendRedirect("/");

                                    } else {

                                        // The user is trying to log into an non-existing account
                                        //redirect him to the homepage with a status indicating that he needs to register first!
                                        System.out.println("Unregistered Github User - Redirect to homepage with status! - " + request_authentication.getPrincipal());
                                        SecurityContextHolder.getContext().setAuthentication(request_authentication);
                                        httpServletRequest.getSession().setAttribute("status", String.valueOf(Status.OAUTH2_USER_NOT_REGISTERED));
                                        httpServletResponse.sendRedirect("/login");

                                    }
                                    break;

                                case "google":

                                    DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
                                    Map<String, Object> claims = oAuth2User.getIdToken().getClaims();
                                    System.out.println(claims);
//let me recap fo you what you have to do okay ? if the user does not exist, you send him to the home page with the status
//if he does exist, you just fetch him from the db, give him the tokens, put in session the authentication alright ? toooo tireeeeed...
//                                    if done or not please say it
                                    if (myUserDetailsService.doesUserExist((String) claims.get("email"))) {
                                        DaoUser google_oauth2User = myUserDetailsService.getUser((String)claims.get("email"));

                                        Cart cart = cartService.lastUserCart(google_oauth2User);

                                        //                            cartRepository.save(new Cart(newOAuthUser, true));

                                        String issuer = oAuth2User.getClaimAsString("iss");

                                        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

                                        String access_token = JWT.create()
                                                .withSubject((String) claims.get("name"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .withClaim("roles", google_oauth2User.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                                                .sign(algorithm);


                                        String refresh_token = JWT.create()
                                                .withSubject((String) claims.get("name"))
                                                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                                .withIssuer(issuer)
                                                .sign(algorithm);

                                        httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                                        httpServletResponse.setHeader(AUTHORIZATION, "Bearer " + access_token);
                                        httpServletRequest.getSession().invalidate();
                                        SecurityContextHolder.getContext().setAuthentication(authentication);
                                        httpServletResponse.sendRedirect("/");


                                    } else {

                                        // The user is trying to log into an non-existing account
                                        //redirect him to the homepage with a status indicating that he needs to register first!
                                        System.out.println("Unregistered Google User - Redirect to homepage with status! - " + request_authentication.getPrincipal());
                                        SecurityContextHolder.getContext().setAuthentication(request_authentication);
                                        httpServletRequest.getSession().setAttribute("status", String.valueOf(Status.OAUTH2_USER_NOT_REGISTERED));
                                        httpServletResponse.sendRedirect("/login");

                                    }


                                    break;

                            }
                        }


                    }


                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        exception.printStackTrace();
                    }
                });

        httpSecurity.requiresChannel(channel -> channel.anyRequest().requiresSecure());

        httpSecurity.authorizeRequests().antMatchers(
                "/login/**",
                            "/oauth2/authorization/**",
                            "/register**",
                            "/smart-accessories/login**",
                            "/users/token/refresh/**",
                            "/oauth/**",
                            "/contact/**",
                            "/resources/static/**",
                            "/about/**",
                            "/privacy-policy/**").permitAll();
        httpSecurity.authorizeRequests().
                antMatchers("/users/**",
                                        "/shipOrders/create-checkout-session/**"
                ).hasAuthority("ROLE_USER");
        httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority("ROLE_ADMIN");
//        httpSecurity.authorizeRequests().anyRequest().authenticated();
        httpSecurity.addFilter(customAuthenticationFilter);
        httpSecurity.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.logout().logoutSuccessUrl("/");
        httpSecurity.exceptionHandling().accessDeniedPage("/error");


    }


}
