package org.yearup.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CategoriesControllerTest {
    /*
     *Mockito creates a mock(clone) for your class and gives developer
     * more flexiblity to test.
     */
    private CategoryDao categoryDao = Mockito.mock(CategoryDao.class);
    private ProductDao productDao = Mockito.mock(ProductDao.class);
    @Test
    public void getAllCategoriesEmptyListTest(){
        //arrange
        when(categoryDao.getAllCategories()).thenReturn(List.of());
        //act
        List<Category> categoryList = categoryDao.getAllCategories();
        //assert
        assertThat(categoryList).isEmpty();

    }

    @Test
    public void getAllCategoriesTest(){
        List<Category> expectedCategoryList = new ArrayList<>();
        expectedCategoryList.add(new Category(1,"charger","A phone charger"));
        expectedCategoryList.add(new Category(2, "iphone", "An iphone"));
        //arrange
        when(categoryDao.getAllCategories()).thenReturn(expectedCategoryList);
        //act
        List<Category> categoryList = categoryDao.getAllCategories();
        //assert
        assertThat(categoryList).isNotEmpty()
                .isEqualTo(expectedCategoryList)
                .hasSize(2);

    }
    @Test
    public void getCategoriesByIdTest(){
        Category charger = new Category(1, "charger", "A phone charger");
        Category iphone = new Category(2, "iphone", "An iPhone");
        //arrange
        when(categoryDao.getById(1)).thenReturn(charger);
        //act
        Category category = categoryDao.getById(1);
        //assert
        assertThat(category).isEqualTo(charger);
        verify(categoryDao).getById(1);

    }

}
