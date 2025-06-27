package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;
import java.util.List;


@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController
{
    private final CategoryDao categoryDao;
    private final ProductDao productDao;

    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    /**
     * Retrieves all categories.
     * Accessible to all users.
     * @return a list of all categories.
     */
    @GetMapping()
    public List<Category> getAll()
    {
        return categoryDao.getAllCategories();
    }
    /**
     * Retrieves a category by its ID.
     * Accessible to all users.
     * @param id the category ID.
     * @return the Category if found.
     * @throws ResponseStatusException if the category does not exist.
     */
    @GetMapping("/{id}")
    public Category getById(@PathVariable int id)
    {
        Category category = categoryDao.getById(id);
        if(category == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category " + id + " not found");
        }
        return category;
    }

    /**
     * Retrieves all products in a given category by ID.
     * Accessible to all users.
     * @param categoryId the ID of the category.
     * @return a list of products in the category.
     * @throws ResponseStatusException if the category does not exist.
     */
    @GetMapping("/{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        if (categoryDao.getById(categoryId) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category " + categoryId + " not found");
        }
        return productDao.listByCategoryId(categoryId);
    }

    /**
     * Creates a new category. Only accessible to admins.
     * @param category the Category object to create.
     * @return the created Category with its generated ID.
     * @throws ResponseStatusException if the category cannot be created.
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category)
    {
        try{
            return categoryDao.create(category);
        }
        catch (RuntimeException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't create a category", e);
        }

    }

    /**
     * Updates an existing category by ID. Only accessible to admins.
     * @param id the ID of the category to update.
     * @param category the updated Category data.
     * @throws ResponseStatusException if the category does not exist.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCategory(@PathVariable int id, @RequestBody Category category)
    {

        boolean success = categoryDao.update(id, category);
        if( !success){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category " + id + " not found");
        }

    }

    /**
     * Deletes a category by ID. Only accessible to admins.
     * @param id the ID of the category to delete.
     * @throws ResponseStatusException if the category does not exist.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id)
    {
        boolean success = categoryDao.delete(id);
        if(!success){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category " + id + " not found");
        }
    }
}
