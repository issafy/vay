package com.niit.vay.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.niit.vay.models.Cart;
import com.niit.vay.models.DaoUser;
import com.niit.vay.models.Provider;
import com.niit.vay.models.Role;
import com.niit.vay.repositories.CartRepository;
import com.niit.vay.repositories.UserRepository;
import com.niit.vay.services.CartService;
import com.niit.vay.services.MyUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MyUserDetailsService myUserDetailsService;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;

    public CustomAuthenticationSuccessHandler(
            MyUserDetailsService myUserDetailsService,
            UserRepository userRepository,
            CartService cartService,
            CartRepository cartRepository
    ) {
        this.myUserDetailsService = myUserDetailsService;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        DefaultOidcUser oAuth2User = (DefaultOidcUser) authentication.getPrincipal();
        Map<String, Object> claims = oAuth2User.getIdToken().getClaims();
        if (!myUserDetailsService.doesUserExist((String)claims.get("email"))) {
            DaoUser newOAuthUser = new DaoUser();
            newOAuthUser.setUsername((String)claims.get("name"));
            newOAuthUser.setPassword("GoogleProvided");
            newOAuthUser.setEmail((String)claims.get("email"));
            newOAuthUser.setEnabled(true);
            newOAuthUser.setProvider(Provider.GOOGLE);
            userRepository.save(newOAuthUser);
            myUserDetailsService.addRoleToUser(newOAuthUser.getUsername(), "ROLE_USER");
            userRepository.save(newOAuthUser);
            Cart ssCart = cartService.lastUserCart(myUserDetailsService.getUserByEmail("session_variable"));
            ssCart.setUser(newOAuthUser);
            cartRepository.save(ssCart);
//                            cartRepository.save(new Cart(newOAuthUser, true));

            String issuer = oAuth2User.getClaimAsString("iss");

            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

            String access_token = JWT.create()
                    .withSubject((String)claims.get("name"))
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .withIssuer(issuer)
                    .withClaim("roles", userRepository.findTop1ByEmail((String)claims.get("email")).getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                    .sign(algorithm);


            String refresh_token = JWT.create()
                    .withSubject((String)claims.get("name"))
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .withIssuer(issuer)
                    .sign(algorithm);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", access_token);
            tokens.put("refresh_token", refresh_token);
            httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
            long olCartId = (long)httpServletRequest.getSession().getAttribute("cartId");

            httpServletRequest.getSession().invalidate();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            httpServletRequest.getSession().setAttribute("cartId", cartService.lastUserCart(newOAuthUser).getCartId());
            httpServletRequest.getSession().setAttribute("olCartId", olCartId);
            httpServletRequest.getSession().setAttribute("username", newOAuthUser.getUsername());
            httpServletResponse.sendRedirect("/");
//                            new ObjectMapper().writeValue(httpServletResponse.getOutputStream(), tokens);


        } else {
            // The user already exists in the database so i'm just going to give him the tokens
            DaoUser logOAuthUser = myUserDetailsService.getUser((String)claims.get("name"));

            String issuer = oAuth2User.getIdToken().getClaimAsString("iss");

            Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

            String access_token = JWT.create()
                    .withSubject((String)claims.get("name"))
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .withIssuer(issuer)
                    .withClaim("roles", userRepository.findTop1ByEmail((String)claims.get("email")).getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                    .sign(algorithm);


            String refresh_token = JWT.create()
                    .withSubject((String)claims.get("name"))
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .withIssuer(issuer)
                    .sign(algorithm);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", access_token);
            tokens.put("refresh_token", refresh_token);
            httpServletResponse.setHeader("access_token", access_token);
            httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
            httpServletRequest.getSession().invalidate();
//                            userRepository.delete(myUserDetailsService.getUser(httpServletRequest.getSession().getId()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            httpServletRequest.getSession().setAttribute("cartId", cartService.lastUserCart(logOAuthUser).getCartId());
//                            httpServletRequest.getSession().setAttribute("olCartId", olCartId);
            httpServletRequest.getSession().setAttribute("username", logOAuthUser.getUsername());
            httpServletResponse.sendRedirect("/");

        }
    }
}
