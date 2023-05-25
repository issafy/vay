package com.niit.vay.services;

import com.niit.vay.dto.UserDto;
import com.niit.vay.models.*;
import com.niit.vay.repositories.CartRepository;
import com.niit.vay.repositories.RoleRepository;
import com.niit.vay.repositories.UserRepository;
import com.niit.vay.repositories.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleRepository roleRepository;
    private final MyUserDetailsService userDetailsService;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final HttpSession httpSession;

    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, MailService mailService, VerificationTokenRepository verificationTokenRepository, RoleRepository roleRepository, MyUserDetailsService userDetailsService, CartRepository cartRepository, CartService cartService, HttpSession httpSession) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.roleRepository = roleRepository;
        this.userDetailsService = userDetailsService;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.httpSession = httpSession;
    }

    public Integer signup(UserDto userDto) {
        DaoUser user = new DaoUser();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setProvider(Provider.LOCAL);
        user.setEnabled(false);

        if (userRepository.findByUsername(user.getUsername()) == null && userRepository.findTop1ByEmail(user.getEmail()) == null) {
            userRepository.save(user);
            Cart ssCart = cartService.lastUserCart(userDetailsService.getUserByEmail(httpSession.getId()));
            ssCart.setUser(user);
            cartRepository.save(ssCart);
        }
        else if (userRepository.findByUsername(user.getUsername()) != null && userRepository.findTop1ByEmail(user.getEmail()) == null) {
            System.out.println("Username taken!");
            return -2;
        } else {
            System.out.println("User already exists!");
            return -1;
        }

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please activate your account",
                user.getEmail(),
                "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css' rel='stylesheet'/>"+
                "<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js'></script>"+
                        "<div class='col-12 row d-flex align-items-center justify-centent-center'>"+
                        "<hr class='col-12 text-center'>"+
                        "<div class='text-center'>Thank you for signing to Vay.<br>"+
                "Please click on this <a href='"+ "https://vay.com:8099/accountVerification/" + token +"'>this link</a> to activate your account."+
        "</div>"+
                "</div>"
                )
        );
        return 0;
    }

    public Integer resetPassword(String email) {
        DaoUser user = userDetailsService.getUserByEmail(email);
        if (user == null) {
//            throw new UserNotFoundException("User does not exist!");
            return -1;
        } else if( !user.getEnabled() )
            return -2;

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Vay - Password Reset",
                        user.getEmail(),
                        "<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css' rel='stylesheet'/>"+
                                "<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/js/bootstrap.bundle.min.js'></script>"+
                                "<div class='col-12 row d-flex align-items-center justify-centent-center'>"+

                                "Click on this <a href='"+ "https://vay.com:8099/set-password/" + token +"'>this link</a> to reset your password."+
                                "</div>"+
                                "</div>"
                )
        );
//        System.out.println("Token: " + token + "###DEBUG###");

        return 0;
    }

    public String resetUserPassword(String token) throws Exception {
        try {
            VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
            String username = verificationToken.getUser().getUsername();
            DaoUser user = userRepository.findByUsername(username);
            if (user == null)
                throw new Exception("User does not exist!");
            user.setPassword("PASSWORDN0TGUESSABLE");
            userRepository.save(user);
            return user.getEmail();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }


    public String generateVerificationToken(DaoUser user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(Instant.ofEpochMilli(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) throws Exception {
        String username = verificationToken.getUser().getUsername();
        DaoUser user = userRepository.findByUsername(username);
        if (user == null)
            throw new Exception("User does not exist!");
        user.setEnabled(true);
        userDetailsService.addRoleToUser(user.getUsername(), "ROLE_USER");
        userRepository.save(user);
    }


    public void verifyAccount(String token) throws Exception {
        try {
            VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
            fetchUserAndEnable(verificationToken);
        } catch (Exception e) {
            System.out.println("Invalid Token!");
        }
    }

}
