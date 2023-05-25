package com.niit.vay.services;

import com.niit.vay.dto.CartDto;
import com.niit.vay.exceptions.CartNotFoundException;
import com.niit.vay.exceptions.UserNotFoundException;
import com.niit.vay.mapper.CartMapper;
import com.niit.vay.models.*;
import com.niit.vay.repositories.CartRepository;
import com.niit.vay.repositories.LineItemRepository;
import com.niit.vay.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final ProductService productService;
    private final LineItemRepository lineItemRepository;
    private final LineItemService lineItemService;
    private final MyUserDetailsService userDetailsService;
    private final StockService stockService;


    public CartService(CartRepository cartRepository, UserRepository userRepository, CartMapper cartMapper, ProductService productService, LineItemRepository lineItemRepository, LineItemService lineItemService, MyUserDetailsService userDetailsService, StockService stockService) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
        this.productService = productService;
        this.lineItemRepository = lineItemRepository;
        this.lineItemService = lineItemService;
        this.userDetailsService = userDetailsService;
        this.stockService = stockService;

    }

    public void save(CartDto cartDto) {
        DaoUser daoUser = userRepository.findById(cartDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(cartDto.getUserId() + ""));
        Cart cart = cartMapper.map(cartDto, daoUser);
        cartRepository.save(cart);

    }

    public List<LineItem> getCartLineItems(Cart cart) {
        return cart.getLineItems();
    }

    public long getCartTotal(long cartId) {
        Cart cart = this.getCartbyCartId(cartId);
        long total = 0;
        try {
            for (LineItem lineItem : cart.getLineItems())
                total += lineItem.getTotal();
        } catch (NullPointerException e) {
            return 0;
        }
        return total;
    }

    public List<Cart> getAllCartsByUserId(Long userId) {
        return cartRepository.getAllByDaoUserAndActive(userDetailsService.getUserByUserId(userId), false);
    }

    public Cart getCartbyCartId(long cartId){
        return cartRepository.getById(cartId);
    }

    public void addLineItemToCart(long cartId, long productId) {
        Product product = productService.getProduct(productId);
        Integer productStock = stockService.getProductStock(product);
        for (LineItem lineItem : this.getCartbyCartId(cartId).getLineItems())
            if (lineItem.getProduct().getProductName() == product.getProductName())
                if ((lineItem.getQuantity() + 1) < productStock)
                    this.addMultipleLineItemToCart(cartId, productId, 1);
    }


    public void addMultipleLineItemToCart(long cartId, long productId, int quantity) {
        Cart cart = this.getCartbyCartId(cartId);
        Integer productStock = stockService.getProductStock(productService.getProduct(productId));
//        We assume first that the product does not exist in the cart
        boolean doesNotExist = true;
        Product product = productService.getProduct(productId);
        LineItem li = new LineItem(cart, product, quantity);
        List<LineItem> actLineItems = new ArrayList<>();
        actLineItems.addAll(cart.getLineItems());
//        If the product does not exist, we can create a new LineItem in cart.
        for(LineItem li2 : actLineItems){
            if (li2.getProduct() == li.getProduct()){
                doesNotExist = false;
            }
        }
//        If it does, we must first get the quantity already in the cart then add it to the one up top
        if (doesNotExist) {
            if (quantity < productStock)
                actLineItems.add(lineItemRepository.save(li));
            else {
                li.setQuantity(productStock);
                actLineItems.add(lineItemRepository.save(li));
            }

        } else {
            for (LineItem li2: actLineItems) {
                if (li2.getProduct().getProductName() == li.getProduct().getProductName()){
                    if((li2.getQuantity() + quantity) > productStock || li2.getQuantity() > productStock) {
                        li2.setQuantity(productStock);
                        lineItemRepository.save(li2);
                    } else {
                        li2.setQuantity(li2.getQuantity() + quantity);
                        lineItemRepository.save(li2);
                    }
                }
            }
        }
        cart.setLineItems(actLineItems);
        cartRepository.save(cart);
    }

    public boolean isCartEmpty(Cart cart) {
        try {
            return cart.getLineItems().size() == 0 ? true : false;
        } catch (NullPointerException e) {
            return true;
        }
    }

    public Cart lastUserCart(DaoUser daoUser) {
        Cart cart = cartRepository.findTop1ByDaoUserAndActive(daoUser, true);
        return cart == null ? cartRepository.save(new Cart(daoUser, true)) : cart;
    }

    public int getProductCount(Cart cart) {
        try {
            return cart.getLineItems().size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public LineItem getProductLineItemFromCart(Cart cart, Product product) {
        LineItem target = new LineItem();
        for(LineItem lineItem : cart.getLineItems())
            if (lineItem.getProduct().getProductName() == product.getProductName())
                target = lineItem;
        return target;
    }

    public boolean doesProductExistOnCart(Cart cart, Product product) {
        for(LineItem lineItem : cart.getLineItems())
            if (lineItem.getProduct().getProductName() == product.getProductName())
                return true;
        return false;
    }

    public void removeLineItemFromCart(long cartId, long lineItemId) {
        Cart cart = this.getCartbyCartId(cartId);
        LineItem lineItem = lineItemService.findById(lineItemId);
        List<LineItem> actLineItems = new ArrayList<>();
        actLineItems.addAll(cart.getLineItems());
        if (actLineItems.contains(lineItem)){
            actLineItems.remove(lineItem);
            lineItem.setCart(cartRepository.findById(1L).orElseThrow(() -> new CartNotFoundException("Cart does not exist!")));
        }
        cart.setLineItems(actLineItems);
        cartRepository.save(cart);

    }


    public void deleteCart(long cartId) {
        Cart cartToDelete = cartRepository.findById(cartId).orElseThrow(() -> new CartNotFoundException("This cart does not exist!"));
        for(LineItem lineItem : cartToDelete.getLineItems()) {
            this.removeLineItemFromCart(cartToDelete.getCartId(), lineItem.getLineItemId());
        }
        cartRepository.delete(cartToDelete);
    }

    public void minusFromCart(long cartId, long productId, int i) throws SQLException {
        Cart cart = this.getCartbyCartId(cartId);
        Product product = productService.getProduct(productId);
        LineItem lineItem = new LineItem();
        for(LineItem lineItem1 : cart.getLineItems())
            if(lineItem1.getProduct().getProductName() == product.getProductName())
                lineItem = lineItem1;
        if (lineItem.getQuantity() == 1)
            this.removeLineItemFromCart(cartId, lineItem.getLineItemId());
        else {
            lineItem.setQuantity(lineItem.getQuantity() - 1);
            lineItemRepository.save(lineItem);
        }
        ;
    }

    public void validateCart(Cart cart) {
        Cart cart1 = cartRepository.findById(cart.getCartId()).orElseThrow(() -> new CartNotFoundException("This cart does not exist!"));
        DaoUser cartUser = cart1.getUser();
        cart1.setActive(false);
        cartRepository.save(cart1);
        Cart newCart = new Cart(cartUser, true);
        cartRepository.save(newCart);
    }
}
