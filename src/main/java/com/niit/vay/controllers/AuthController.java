package com.niit.vay.controllers;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.niit.vay.config.Status;
import com.niit.vay.dto.*;
import com.niit.vay.models.*;
import com.niit.vay.repositories.CartRepository;
import com.niit.vay.repositories.UserRepository;
import com.niit.vay.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class AuthController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final AuthService authService;
    private final ReviewService reviewService;
    private final CartService cartService;
    private final MyUserDetailsService userDetailsService;
    private final BrandService brandService;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final StockProviderService stockProviderService;
    private final StockService stockService;
    private final SuperCategoryService superCategoryService;
    private final ShipOrderService shipOrderService;
    private final VayJdbcService vayJdbcService;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    ServletContext servletContext;

    public AuthController(
            ProductService productService,
            CategoryService categoryService,
            AuthService authService,
            ReviewService reviewService,
            CartService cartService,
            MyUserDetailsService userDetailsService,
            BrandService brandService,
            CartRepository cartRepository,
            UserRepository userRepository,
            StockProviderService stockProviderService,
            SuperCategoryService superCategoryService,
            ShipOrderService shipOrderService,
            StockService stockService,
            VayJdbcService vayJdbcService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.authService = authService;
        this.reviewService = reviewService;
        this.cartService = cartService;
        this.userDetailsService = userDetailsService;
        this.brandService = brandService;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.stockProviderService = stockProviderService;
        this.superCategoryService = superCategoryService;
        this.shipOrderService = shipOrderService;
        this.stockService = stockService;
        this.vayJdbcService = vayJdbcService;
    }

    @GetMapping("/")
    public ModelAndView getHomePage(HttpSession session, HttpServletRequest request){
        ModelAndView mav = new ModelAndView("/template/index");

        setSessionUser(session, request);
        if ((boolean)session.getAttribute("loggedIn"))
            session.removeAttribute("status");

//        System.out.println(session.getAttribute("username"));
        mav.addObject("productService", productService);
        mav.addObject("reviewService", reviewService);
        mav.addObject("categoryService", categoryService);
        mav.addObject("userDetailsService", userDetailsService);
        mav.addObject("shipOrderService", shipOrderService);
        mav.addObject("cartService", cartService);
        mav.addObject("httpSession", session);
        mav.addObject("vayJdbcService", vayJdbcService);

        return mav;
    }

    @RequestMapping("/log-out")
    public void logOut(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IOException {
        SecurityContextHolder.getContext().setAuthentication((Authentication) request.getSession().getAttribute("anonymousAuthentication"));
        session.setAttribute("username", "G" + session.getId());
        session.setAttribute("loggedIn", false);
        response.sendRedirect("/");
    }


    @GetMapping("/about")
    public ModelAndView getAboutPage(HttpSession session, HttpServletRequest request){
        ModelAndView mav = new ModelAndView("/template/about");
        setSessionUser(session, request);

        mav.addObject("httpSession", session);
        mav.addObject("cartService", cartService);
        mav.addObject("userDetailsService", userDetailsService);
        mav.addObject("brandService", brandService);
        mav.addObject("categoryService", categoryService);
        mav.addObject("vayJdbcService", vayJdbcService);
        mav.addObject("brandService", brandService);
        return mav;
    }

    @GetMapping("/contact")
    public ModelAndView getContactPage(HttpSession session, HttpServletRequest request){
        ModelAndView mav = new ModelAndView("/template/contact");
        setSessionUser(session, request);
        mav.addObject("httpSession", session);
        mav.addObject("cartService", cartService);
        mav.addObject("userDetailsService", userDetailsService);
        mav.addObject("brandService", brandService);
        mav.addObject("categoryService", categoryService);
        mav.addObject("vayJdbcService", vayJdbcService);
        mav.addObject("brandService", brandService);

        return mav;
    }

    @GetMapping("/shop")
    public ModelAndView getShop(
            Optional<Integer> page,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException{
        ModelAndView mav = new ModelAndView("/template/shop");

        setSessionUser(session, request);
        long cartId3 = 0;
        mav.addObject("page", page.orElse(0));
        mav.addObject("categoryService", categoryService);
        mav.addObject("superCategoryService", superCategoryService);
        mav.addObject("productService", productService);
        mav.addObject("cartService", cartService);
        mav.addObject("reviewService", reviewService);
        mav.addObject("brandService", brandService);
        mav.addObject("vayJdbcService", vayJdbcService);
        mav.addObject("stockService", stockService);
        mav.addObject("cartId3", cartId3);
        mav.addObject("httpSession", session);
        return mav;
    }

    @GetMapping("/account/{username}")
    public ModelAndView getUserAccount(@PathVariable String username, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
        ModelAndView mav = new ModelAndView("/template/account");

        setSessionUser(session, request);
        long cartId3 = 0;
        mav.addObject("categoryService", categoryService);
        mav.addObject("superCategoryService", superCategoryService);
        mav.addObject("userDetailsService", userDetailsService);
        mav.addObject("productService", productService);
        mav.addObject("cartService", cartService);
        mav.addObject("shipOrderService", shipOrderService);
        mav.addObject("brandService", brandService);
        mav.addObject("vayJdbcService", vayJdbcService);
        mav.addObject("stockService", stockService);
        mav.addObject("cartId3", cartId3);
        mav.addObject("httpSession", session);
        return mav;
    }

    @GetMapping("/shop-search/")
    public ModelAndView getShopSearch(
//            Optional<Integer> page,
            String search,
            HttpServletRequest request,
            HttpSession session
    ) {
        ModelAndView mav = new ModelAndView("/template/shop-search");
        setSessionUser(session, request);
        String username = (String)request.getSession().getAttribute("username");
        List<Product> products = productService.getSearchProducts(search);

        mav.addObject("superCategoryService", superCategoryService);
        mav.addObject("categoryService", categoryService);
        mav.addObject("username", username);
        mav.addObject("search", search);
        mav.addObject("productService", productService);
        mav.addObject("cartService", cartService);
        mav.addObject("reviewService", reviewService);
        mav.addObject("vayJdbcService", vayJdbcService);
        mav.addObject("httpSession", session);
        mav.addObject("search_products", products);
        return mav;
    }

    @GetMapping("/shop-single/{productId}")
    public ModelAndView getShopSingle(@PathVariable long productId, HttpSession session, HttpServletRequest request){
        ModelAndView mav = new ModelAndView("/template/shop-single");
        int quantity = 0;
        ReviewDto reviewDto = new ReviewDto();
        setSessionUser(session, request);
        Product product = productService.getProduct(productId);
        String username = (String)request.getSession().getAttribute("username");
        DaoUser daoUser = userDetailsService.getUser(username);
        boolean canUserReviewProduct = reviewService.canUserReviewProduct(daoUser, product);
        System.out.println("Can User Review Product:" + canUserReviewProduct);
        mav.addObject("canUserReviewProduct", canUserReviewProduct);
        mav.addObject("product", product);
        mav.addObject("reviewDto", reviewDto);
        mav.addObject("quantity", quantity);
        mav.addObject("productService", productService);
        mav.addObject("reviewService", reviewService);
        mav.addObject("productId", productId);
        mav.addObject("daoUser", daoUser);
        mav.addObject("categoryService", categoryService);
        mav.addObject("vayJdbcService", vayJdbcService);
        mav.addObject("cartService", cartService);
        mav.addObject("httpSession", session);
        mav.addObject("stockService", stockService);
        return mav;
    }

    @GetMapping("/register")
    public ModelAndView signin(HttpServletRequest request, HttpSession session) {
        ModelAndView mav = new ModelAndView("/template/register");
        setSessionUser(session, request);
        session.setAttribute("sessionId", session.getId());
        session.setAttribute("origin", "register");
        UserDto userDto = new UserDto();
        mav.addObject("userDto", userDto);
        mav.addObject("httpSession", session);

        return mav;
    }

    @PostMapping(value = "/smart-accessories/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void signup(UserDto userDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(userDto.getPassword());
        if(!matcher.matches()){
            request.getSession().setAttribute("status", Status.INVALID_PASSWORD);
            response.sendRedirect("/register");
        } else {

            int result = authService.signup(userDto);
            switch (result) {
                case -2:
                    request.getSession().setAttribute("status", Status.USERNAME_TAKEN);
                    response.sendRedirect("/register");
                    break;
                case -1:
                    request.getSession().setAttribute("status", Status.ALREADY_REGISTERED);
                    response.sendRedirect("/login");
                    break;
                case 0:
                    request.getSession().setAttribute("status", Status.ACCOUNT_TO_ACTIVATE);
                    response.sendRedirect("/login");
                    break;
            }
        }

    }

    @GetMapping("accountVerification/{token}")
    public void verifyAccount(@PathVariable String token, HttpServletRequest request, HttpServletResponse response) throws Exception {
        authService.verifyAccount(token);
        request.getSession().setAttribute("status", Status.ACCOUNT_ACTIVATED_SUCCESSFULLY);
        response.sendRedirect("/login");
    }

    @RequestMapping("/users/{username}/delete")
    public ResponseEntity<String> deleteAccount(@PathVariable String username) {
        userDetailsService.deleteUser(username);
        return new ResponseEntity<>("User " + username + " deletion successful!", HttpStatus.OK);
    }

    @RolesAllowed("ROLE_ADMIN")
    @RequestMapping("/admin")
    public ModelAndView getAdmin(HttpSession session, HttpServletRequest request) throws SQLException {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        ModelAndView mav = new ModelAndView("pages/dashboard");

        CategoryDto categoryDto = new CategoryDto();
        ProductDto productDto = new ProductDto();
        MultipartFile file = null;
        mav.addObject("productService", productService);
        mav.addObject("userCount", userDetailsService.getUsers().size());
        mav.addObject("categoryService", categoryService);
        mav.addObject("shipOrderService", shipOrderService);
        mav.addObject("productDto", productDto);
        mav.addObject("file", file);
        mav.addObject("username", username);
        mav.addObject("vayJdbcService", vayJdbcService);
        return mav;
    }

    @RolesAllowed("ROLE_ADMIN")
    @RequestMapping("/admin/products")
    public ModelAndView getProducts(HttpSession session, HttpServletRequest request) {
        setSessionUser(session, request);
        ModelAndView mav = new ModelAndView("pages/products");
        CategoryDto categoryDto = new CategoryDto();
        SuperCategoryDto superCategoryDto = new SuperCategoryDto();
        SuperCategoryDto superCategoryEditDto = new SuperCategoryDto();
        CategoryDto categoryEditDto = new CategoryDto();
        ProductDto productDto = new ProductDto();
        ProductDto productEditDto = new ProductDto();
        StockProviderDto stockProviderDto = new StockProviderDto();
        StockProviderDto stockProviderEditDto = new StockProviderDto();
        mav.addObject("httpSession", session);
        mav.addObject("userDetailsService", userDetailsService);
        mav.addObject("superCategoryDto", superCategoryDto);
        mav.addObject("superCategoryEditDto", superCategoryEditDto);
        mav.addObject("categoryService", categoryService);
        mav.addObject("productService", productService);
        mav.addObject("categoryDto", categoryDto);
        mav.addObject("stockProviderDto", stockProviderDto);
        mav.addObject("categoryEditDto", categoryEditDto);
        mav.addObject("stockProviderEditDto", stockProviderEditDto);
        mav.addObject("productDto", productDto);
        mav.addObject("productEditDto", productEditDto);
        mav.addObject("stockProviderService", stockProviderService);
        mav.addObject("brandService", brandService);
        mav.addObject("superCategoryService", superCategoryService);
        return mav;
    }

    @RolesAllowed("ROLE_ADMIN")
    @RequestMapping("/admin/inventory")
    public ModelAndView getInventory(
            Optional<Integer> page,
            HttpSession session,
            HttpServletRequest request
    ) {
        setSessionUser(session, request);
        ModelAndView mav = new ModelAndView("pages/stock");
        StockDto stockDto = new StockDto();
        StockDto stockEditDto = new StockDto();
        mav.addObject("page", page.orElse(0));
        mav.addObject("httpSession", session);
        mav.addObject("stockDto", stockDto);
        mav.addObject("stockEditDto", stockEditDto);
        mav.addObject("stockService", stockService);
        mav.addObject("productService", productService);
        mav.addObject("superCategoryService", superCategoryService);
        mav.addObject("categoryService", categoryService);
        mav.addObject("stockProviderService", stockProviderService);
        return mav;
    }

    @RolesAllowed("ROLE_ADMIN")
    @RequestMapping("/admin/inventory/providers")
    public ModelAndView getStockProviders(
            Optional<Integer> page,
            HttpSession session,
            HttpServletRequest request
    ) {
        setSessionUser(session, request);
        ModelAndView mav = new ModelAndView("pages/stock-providers");
        StockProviderDto stockProviderDto = new StockProviderDto();
        StockProviderDto stockProviderEditDto = new StockProviderDto();
        mav.addObject("httpSession", session);
        mav.addObject("page", page);
        mav.addObject("stockProviderDto", stockProviderDto);
        mav.addObject("stockProviderEditDto", stockProviderEditDto);
        mav.addObject("stockService", stockService);
        mav.addObject("productService", productService);
        mav.addObject("superCategoryService", superCategoryService);
        mav.addObject("categoryService", categoryService);
        mav.addObject("stockProviderService", stockProviderService);
        return mav;
    }

    @RolesAllowed("ROLE_ADMIN")
    @RequestMapping("/admin/customers")
    public ModelAndView getCustomers(
            Optional<Integer> page,
            HttpSession session,
            HttpServletRequest request
    ) {
        setSessionUser(session, request);
        ModelAndView mav = new ModelAndView("pages/customers");
        mav.addObject("page", page);
        mav.addObject("httpSession", session);
        mav.addObject("userDetailsService", userDetailsService);
        mav.addObject("cartService", cartService);
        mav.addObject("shipOrderService", shipOrderService);
        return mav;
    }

    @RolesAllowed("ROLE_ADMIN")
    @RequestMapping("/admin/orders")
    public ModelAndView getOrders(
            Optional<Integer> page,
            HttpSession session,
            HttpServletRequest request
    ) {
        setSessionUser(session, request);
        ModelAndView mav = new ModelAndView("pages/orders");
        mav.addObject("httpSession", session);
        mav.addObject("page", page);
        mav.addObject("shipOrderService", shipOrderService);
        mav.addObject("cartService", cartService);
        mav.addObject("userDetailsService", userDetailsService);
        return mav;
    }

    @RolesAllowed("ROLE_ADMIN")
    @RequestMapping("/admin/orders/report/pdf")
    public ResponseEntity<?> getOrdersPdfReport(Long old_cart_id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        WebContext webContext = new WebContext(request, response, servletContext);
        webContext.setVariable("shipOrderService", shipOrderService);
        webContext.setVariable("cartService", cartService);
        webContext.setVariable("userDetailsService", userDetailsService);
        String orders = templateEngine.process("/pages/ordersReport", webContext);

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("https://localhost:8099");
        /* Call convert method */
        HtmlConverter.convertToPdf(orders, target, converterProperties);

        /* extract output as bytes */
        byte[] bytes = target.toByteArray();


        /* Send the response as downloadable PDF */

        return ResponseEntity.ok()

                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);

    }

    @RolesAllowed("ROLE_ADMIN")
    @RequestMapping("/admin/orders/report")
    public ModelAndView getOrdersReport(
            HttpSession session,
            HttpServletRequest request
    ) {
        setSessionUser(session, request);
        ModelAndView mav = new ModelAndView("pages/ordersReport");
        mav.addObject("shipOrderService", shipOrderService);
        mav.addObject("cartService", cartService);
        mav.addObject("userDetailsService", userDetailsService);
        return mav;
    }

    @RequestMapping("/privacy-policy")
    public ModelAndView getPrivacyPolicy(HttpSession session, HttpServletRequest request) {
        setSessionUser(session, request);
        return new ModelAndView("/template/privacy-policy");
    }

    @RequestMapping("/reset-password")
    public ModelAndView getPasswordReset(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/template/reset");
        boolean lock = false;
        if (request.getSession() == null)
            request.getSession().setAttribute("status", "");
        else if (String.valueOf(request.getSession().getAttribute("status")) == "PASSWORD_RESET_MAIL_SENT")
            lock = true;

        mav.addObject("httpSession", request.getSession());
        mav.addObject("lock", lock);
        return mav;
    }

    @RequestMapping(value = "/password-reset", method = RequestMethod.POST)
    public void resetPassword(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer reset_status = authService.resetPassword(email);
        switch (reset_status) {
            case -1:
                response.sendRedirect("/register");
                break;

            case 0:
                request.getSession().setAttribute("status", Status.PASSWORD_RESET_MAIL_SENT);
                response.sendRedirect("/reset-password");
                break;
        }
    }

    @RequestMapping("/set-password/{token}")
    public ModelAndView getNewPassword(@PathVariable String token, HttpSession session, HttpServletRequest request) throws Exception {
        String reset_email = authService.resetUserPassword(token);
        PasswordResetDto passwordResetDto = new PasswordResetDto();
        if(reset_email.isBlank())
            throw new RuntimeException("An error has occurred");

        ModelAndView mav = new ModelAndView("/template/new-password");
        mav.addObject("passwordResetDto", passwordResetDto);
        mav.addObject("reset_email", reset_email);

        return mav;
    }

    @PostMapping(value = "/update-password", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updatePassword(PasswordResetDto passwordResetDto, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(passwordResetDto.getPassword());
        if(!matcher.matches()){
            request.getSession().setAttribute("status", Status.INVALID_PASSWORD);
            response.sendRedirect("/set-password/" + authService.generateVerificationToken(userDetailsService.getUserByEmail(passwordResetDto.getEmail())));
        } else {
            System.out.println("Form Email: " + passwordResetDto.getEmail());

            userDetailsService.updateUserPassword(passwordResetDto);
            response.sendRedirect("/login");
        }
    }

    @RequestMapping("/login")
    public ModelAndView login(HttpSession session, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/template/login");
        String username = "";
        String password = "";
        mav.addObject("username", username);
        mav.addObject("password", password);
        mav.addObject("httpSession", session);
        session.setAttribute("registrationStatus", true);
        session.setAttribute("principal", SecurityContextHolder.getContext().getAuthentication());
        session.setAttribute("origin", "login");
        return mav;
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/payment/invoice")
    public String success(Model mav, HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IOException {
//        ModelAndView mav = new ModelAndView("payment/success");
//        System.out.println("The user: " + request.getSession().getAttribute("username"));
//        System.out.println("The authentication principal instance: " + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("The old cart: " + session.getAttribute("oldCart"));
        setSessionUser(session, request);
        Cart oldCart = (Cart)session.getAttribute("oldCart");
        mav.addAttribute("oldCart", oldCart);
        mav.addAttribute("cartService", cartService);
        mav.addAttribute("shipOrderService", shipOrderService);
        return "payment/success";
    }

    @RequestMapping(path = "/payment/invoice/pdf")
    public ResponseEntity<?> getPdf(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        setSessionUser(session, request);
        Cart oldCart = (Cart)session.getAttribute("oldCart");

        WebContext webContext = new WebContext(request, response, servletContext);
        webContext.setVariable("oldCart", oldCart);
        webContext.setVariable("cartService", cartService);
        webContext.setVariable("shipOrderService", shipOrderService);
        String payment = templateEngine.process("payment/success", webContext);

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("https://localhost:8099");
        /* Call convert method */
        HtmlConverter.convertToPdf(payment, target, converterProperties);

        /* extract output as bytes */
        byte[] bytes = target.toByteArray();


        /* Send the response as downloadable PDF */

        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    @RequestMapping(path = "/payment/invoice/generated")
    public ResponseEntity<?> getGeneratedPdf(Long old_cart_id, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        setSessionUser(session, request);
        Cart oldCart = cartService.getCartbyCartId(old_cart_id);
        WebContext webContext = new WebContext(request, response, servletContext);
        webContext.setVariable("oldCart", oldCart);
        webContext.setVariable("cartService", cartService);
        webContext.setVariable("shipOrderService", shipOrderService);
        String payment = templateEngine.process("payment/success", webContext);

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("https://localhost:8099");
        /* Call convert method */
        HtmlConverter.convertToPdf(payment, target, converterProperties);

        /* extract output as bytes */
        byte[] bytes = target.toByteArray();


        /* Send the response as downloadable PDF */

        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    public boolean isUserLoggedId() {
        return !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    public void setSessionUser(HttpSession session, HttpServletRequest request) {
//        System.out.println(request.getHeaderNames());
        if (isUserLoggedId()) {
            System.out.println("Logged in user!");
            if (SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken) {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                DaoUser daoUser = userDetailsService.getUser(user.getUsername());
                Cart cart = cartService.lastUserCart(daoUser);
                session.setAttribute("cart", cart);
                session.setAttribute("username", daoUser.getUsername());
                session.setAttribute("loggedIn", true);
                session.setAttribute("isCartEmpty", cartService.isCartEmpty(cart));
                session.setAttribute("authentication", SecurityContextHolder.getContext().getAuthentication());
            } else {
                try {
                    CustomOAuth2User user = (CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    DaoUser daoUser = userDetailsService.getUserByEmail(user.getEmail());
                    Cart cart = cartService.lastUserCart(daoUser);
                    session.setAttribute("cart", cart);
                    session.setAttribute("username", daoUser.getUsername());
                    session.setAttribute("loggedIn", true);
                    session.setAttribute("isCartEmpty", cartService.isCartEmpty(cart));
                } catch (ClassCastException e) {
                    DefaultOidcUser user = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    DaoUser daoUser = userDetailsService.getUserByEmail(user.getEmail());
                    Cart cart = cartService.lastUserCart(daoUser);
                    session.setAttribute("cart", cart);
                    session.setAttribute("username", daoUser.getUsername());
                    session.setAttribute("loggedIn", true);
                    session.setAttribute("isCartEmpty", cartService.isCartEmpty(cart));
                } finally {
                    OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
                    DaoUser daoUser = userDetailsService.getUserByEmail((String)authenticationToken.getPrincipal().getAttribute("email"));
                    Cart cart = cartService.lastUserCart(daoUser);
                    session.setAttribute("cart", cart);
                    session.setAttribute("username", daoUser.getUsername());
                    session.setAttribute("loggedIn", true);
                    session.setAttribute("isCartEmpty", cartService.isCartEmpty(cart));
                }

            }

        } else {
            System.out.println("Not logged in!");
            if (request.getSession().getAttribute("username") == null ) {
                DaoUser sessionUser = new DaoUser("G" + session.getId(), session.getId(), "session_variable");
                sessionUser.setEnabled(false);
                System.out.println(userRepository.save(sessionUser));
                Cart cart = cartRepository.save(new Cart(userDetailsService.getUserByEmail(session.getId()), true));
                session.setAttribute("cart", cart);
                session.setAttribute("username", sessionUser.getUsername());
                session.setAttribute("loggedIn", false);
                session.setAttribute("isCartEmpty", cartService.isCartEmpty(cart));
                session.setAttribute("anonymousAuthentication", SecurityContextHolder.getContext().getAuthentication());
            } else {
                Cart cart = cartService.lastUserCart(userDetailsService.getUser("G" + session.getId()));
                session.setAttribute("cart", cart);
                session.setAttribute("loggedIn", false);
                session.setAttribute("isCartEmpty", cartService.isCartEmpty(cart));
            }

        }
    }



}
