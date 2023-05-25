package com.niit.vay.services;

import com.niit.vay.dto.PasswordResetDto;
import com.niit.vay.dto.UserDto;
import com.niit.vay.exceptions.UserNotFoundException;
import com.niit.vay.models.Cart;
import com.niit.vay.models.DaoUser;
import com.niit.vay.models.Provider;
import com.niit.vay.models.Role;
import com.niit.vay.repositories.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final CartRepository cartRepository;


    public MyUserDetailsService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder bCryptPasswordEncoder, CartRepository cartRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.cartRepository = cartRepository;

    }

    public DaoUser getUser(String username) {
        return userRepository.findByUsername(username);
    }
    public DaoUser getUserByEmail(String email) { return userRepository.findTop1ByEmail(email); }

    public List<DaoUser> getEnabledUsers() {
        return userRepository.findAllByEnabled(true);
    }

    public List<DaoUser> getPagedEnabledUsers(Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 10);
        List<DaoUser> users = userRepository.findAllByEnabled(true, pageable);
        return users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DaoUser user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("User not found in the database!");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            System.out.println("User found in the database: " + username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        });
        return new User(user.getUsername(), user.getPassword(), authorities);
    }

    public DaoUser save(UserDto userDto) {
        DaoUser newUser = new DaoUser();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        newUser.setEmail(userDto.getEmail());
        newUser.setEnabled(false);
        newUser.setProvider(Provider.LOCAL);
        userRepository.save(newUser);
        cartRepository.save(new Cart(newUser, true));
        userRepository.save(newUser);
        return newUser;
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public void addRoleToUser(String username, String roleName) {
        DaoUser user = userRepository.findByUsername(username);
        Role role = roleRepository.findByRoleName(roleName);
        if (user != null && role != null && user.getEnabled()) {
            user.getRoles().add(role);
            userRepository.save(user);
        } else
            throw new RuntimeException("Operation Failed!");
    }

    public Boolean doesUserExist(String email) {
        DaoUser user = (DaoUser) userRepository.findTop1ByEmail(email);
        if (user == null) {
//            System.out.println("User not found in the database!");
//            throw new UsernameNotFoundException("User not found in the database");
            return false;
        } else {
            return true;
        }
    }

    public void updateUserPassword(PasswordResetDto passwordResetDto) {
        DaoUser daoUser = this.getUserByEmail(passwordResetDto.getEmail());
        if (daoUser == null)
            throw new UserNotFoundException("User does not exist!");
        daoUser.setPassword(bCryptPasswordEncoder.encode(passwordResetDto.getPassword()));
        userRepository.save(daoUser);
    }

    public DaoUser getUserByUserId(Long userId) {
        return userRepository.findByUserId(userId);
    }

    public List<DaoUser> getUsers() {
        List<DaoUser> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public void deleteUser(String username) {
        DaoUser user = (DaoUser) userRepository.findByUsername(username);
        if (user == null)
            throw new RuntimeException("User does not exist!");


        userRepository.delete(user);
    }
}
