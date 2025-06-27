package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.exception.CustomDataException;
import org.yearup.exception.CustomResourceNotFoundException;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("products")
@CrossOrigin
public class ProductsController
{
    private final ProductDao productDao;

    @Autowired
    public ProductsController(ProductDao productDao)
    {
        this.productDao = productDao;
    }

    /**
     * Searches for products with optional filters for category, price range, and color.
     * Accessible to all users.
     * @param categoryId optional category ID to filter by.
     * @param minPrice optional minimum price filter.
     * @param maxPrice optional maximum price filter.
     * @param color optional color filter.
     * @return a list of matching products.
     * @throws CustomDataException on database or search errors.
     */
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Product> search(@RequestParam(name="cat", required = false) Integer categoryId,
                                @RequestParam(name="minPrice", required = false) BigDecimal minPrice,
                                @RequestParam(name="maxPrice", required = false) BigDecimal maxPrice,
                                @RequestParam(name="color", required = false) String color
                                )
    {
        try
        {
            return productDao.search(categoryId, minPrice, maxPrice, color);
        }
        catch(Exception e)
        {
            throw new CustomDataException("Error loading search product", e);
        }
    }

    /**
     * Retrieves a single product by ID.
     * Accessible to all users.
     * @param id the ID of the product to retrieve.
     * @return the product, if found.
     * @throws CustomResourceNotFoundException if the product does not exist.
     * @throws CustomDataException on database errors.
     */
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Product getById(@PathVariable int id )
    {
        try
        {
            var product = productDao.getById(id);

            if(product == null)
                throw new CustomResourceNotFoundException("Product " + id + " not found");


            return product;
        }
        catch(Exception e)
        {
            throw new CustomDataException("Error fetching product", e);
        }
    }

    /**
     * Adds a new product. Only accessible to users with ROLE_ADMIN.
     * @param product the product data to create.
     * @return the created product with its generated ID.
     * @throws CustomDataException on database errors.
     */
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Product addProduct(@RequestBody Product product)
    {
        try
        {
            return productDao.create(product);
        }
        catch(Exception e)
        {
            throw new CustomResourceNotFoundException("Product not found");
        }
    }

    /**
     * Updates an existing product by ID. Only accessible to users with ROLE_ADMIN.
     * @param id the ID of the product to update.
     * @param product the updated product data.
     * @throws CustomDataException on database errors.
     */
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateProduct(@PathVariable int id, @RequestBody Product product)
    {
        try
        {
            productDao.update(id, product);
        }
        catch(Exception e)
        {
            throw new CustomDataException("Error updating product " + id, e);
        }
    }

    /**
     * Deletes a product by ID. Only accessible to users with ROLE_ADMIN.
     * @param id the ID of the product to delete.
     * @throws CustomResourceNotFoundException if the product does not exist.
     * @throws CustomDataException on database errors.
     */
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteProduct(@PathVariable int id)
    {
        try
        {
            var product = productDao.getById(id);

            if(product == null)
                throw new CustomResourceNotFoundException("Product " + id + " not found");

            productDao.delete(id);
        }
        catch(Exception e)
        {
            throw new CustomDataException("Error deleting product", e);
        }
    }
}
