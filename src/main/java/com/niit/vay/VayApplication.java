package com.niit.vay;

import com.niit.vay.models.*;
import com.niit.vay.repositories.*;
import com.niit.vay.services.MyUserDetailsService;
import com.niit.vay.services.StorageService;
import com.niit.vay.services.VayJdbcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import javax.annotation.Resource;

@SpringBootApplication
public class VayApplication implements CommandLineRunner {

    @Resource
    StorageService storageService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SuperCategoryRepository superCategoryRepository;
    @Autowired
    private StockProviderRepository stockProviderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private VayJdbcService vayJdbcService;




    @Bean
    PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }

    public static void main(String[] args) {
//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
//        applicationContext.getBean("authenticationManager", AuthenticationManager.class);
        SpringApplication.run(VayApplication.class, args);



    }

    @Override
    public void run(String... args) throws Exception {
        myUserDetailsService.saveRole(new Role("ROLE_USER"));
        myUserDetailsService.saveRole(new Role("ROLE_ADMIN"));
//        Admin Roles
        DaoUser admin = new DaoUser("admined", "admin@admin.com", "admined");
        admin.setEnabled(true);
        admin.setProvider(Provider.LOCAL);
        userRepository.save(admin);
        myUserDetailsService.addRoleToUser("admined", "ROLE_ADMIN");
        myUserDetailsService.addRoleToUser("admined", "ROLE_USER");
        cartRepository.save(new Cart(admin, true));
//        Admin Roles

//        Lambda Roles
        DaoUser user = new DaoUser("usernamed", "user@user.com", bCryptPasswordEncoder().encode("usernamed"));
        user.setEnabled(true);
        user.setProvider(Provider.LOCAL);
        userRepository.save(user);
        myUserDetailsService.addRoleToUser("usernamed", "ROLE_USER");
        cartRepository.save(new Cart(user, true));
//        Lambda Roles

        stockProviderRepository.save(new StockProvider("Unlisted Provider", "Unlisted Mail", "Unlisted Location", "000-000-000"));
        superCategoryRepository.save(new SuperCategory("Unlisted SuperCategory", "Super Category for the unlisted categories. This super category will not show on the homepage.", "/webapp/admin/assets/img/unlisted.png"));
        categoryRepository.save(new Category("Unlisted Category", superCategoryRepository.findBySuperCategoryId(1L), "Category for the unlisted products. This category will not show on the homepage.", "/webapp/admin/assets/img/unlisted.png"));
        brandRepository.save(new Brand("Levi's", "/webapp/admin/assets/img/brand_01.png"));
        brandRepository.save(new Brand("Adidas", "/webapp/admin/assets/img/brand_02.png"));
        brandRepository.save(new Brand("Nike", "/webapp/admin/assets/img/brand_03.png"));
        brandRepository.save(new Brand("H&M", "/webapp/admin/assets/img/brand_04.png"));
        brandRepository.save(new Brand("Cartier", "/webapp/admin/assets/img/brand_05.png"));
        brandRepository.save(new Brand("Dell", "/webapp/admin/assets/img/dell.png"));
        brandRepository.save(new Brand("Supreme", "/webapp/admin/assets/img/supreme.png"));
        brandRepository.save(new Brand("Under Armour", "/webapp/admin/assets/img/under-armour.png"));
        brandRepository.save(new Brand("HP", "/webapp/admin/assets/img/hp.png"));
        brandRepository.save(new Brand("Old Navy", "/webapp/admin/assets/img/old-navy.png"));
        brandRepository.save(new Brand("SkyXplorer", "/webapp/admin/assets/img/skyXplorer.png"));
        brandRepository.save(new Brand("Old Navy", "/webapp/admin/assets/img/old-navy.png"));
        brandRepository.save(new Brand("Generic", "/webapp/admin/assets/img/default_brand.png"));
        vayJdbcService.seed();
    }

}
