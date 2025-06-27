package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

/**
 * ShoppingCartController exposes REST endpoints to manage the shopping cart
 * for authenticated users. Only users with ROLE_USER can access these endpoints.
 * Supported operations include viewing the cart, adding products, updating quantities,
 * clearing the cart, and deleting individual items.
 */
@RestController
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/cart")
public class ShoppingCartController
{
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao){
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    /**
     * Retrieves the current user's shopping cart.
     * @param principal the authenticated user's principal.
     * @return the ShoppingCart for the logged-in user.
     */
    @GetMapping()
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // get the currently logged username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not get the cart",  e);
        }
    }

    /**
     * Adds a product to the current user's cart or increases quantity if it already exists.
     * Quantity can be provided via either 'quantity' or 'qty' request parameter.
     * @param id the product ID.
     * @param quantityParam fallback quantity parameter (default=1).
     * @param qtyAlias optional quantity parameter alternative.
     * @param principal the authenticated user's principal.
     * @return the added ShoppingCartItem.
     */
    @PostMapping("/products/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartItem addProduct(@PathVariable int id,
                                       @RequestParam(name = "quantity", defaultValue = "1") int quantityParam,
                                       @RequestParam(name = "qty", required = false) Integer qtyAlias, Principal principal){
        int quantity = (qtyAlias != null) ? qtyAlias : quantityParam;
        if (quantity <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }

        try{
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            shoppingCartDao.addProduct(userId, id, quantity);
            ShoppingCartItem shoppingCartItem = shoppingCartDao.getSingleItem(userId, id);
            if(shoppingCartItem == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in cart");
            }
            return shoppingCartItem;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Product not added", e);
        }

    }



    /**
     * Updates the quantity of an existing product in the cart.
     * @param id the product ID.
     * @param body the ShoppingCartItem containing the new quantity.
     * @param principal the authenticated user's principal.
     */
    @PutMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateQuantity(@PathVariable int id, @RequestBody ShoppingCartItem body, Principal principal){
            //Check JSON
            if (body == null || body.getQuantity() <= 0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive whole number");
            }
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            ShoppingCartItem shoppingCartItem = shoppingCartDao.getSingleItem(userId, id);
            if (shoppingCartItem == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart has no product " + id);
            }
            try{
                shoppingCartDao.updateQuantity(userId,id, body.getQuantity());
            }

        catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database error updating the cart" + e);
        }
    }


    /**
     * Clears all products from the current user's shopping cart.
     * @param principal the authenticated user's principal.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Principal principal){
        try{
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            shoppingCartDao.delete(userId);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "error deleting the cart" , e);
        }
    }
    //Delete single cart item
    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable int id, Principal principal) {
        int userId = userDao.getByUserName(principal.getName()).getId();
        shoppingCartDao.deleteItem(userId, id);
    }

}
