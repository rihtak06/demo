/*******************************************************************************
 * Copyright (C) 2018 Steerwise Inc. All Rights Reserved.
 *******************************************************************************/
package com.steerwise.sat.junit;


import com.ensat.SpringBootWebApplication;
import com.ensat.entities.Product;
import com.ensat.repositories.ProductRepository;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Test class for the UserResource REST controller.
 *
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootWebApplication.class)
@Transactional
public class ProductIntTest {

   

    private Product product;
    private Product product1;
    @Autowired
    private ProductRepository productRepository;

    @Before
    public void init() {
        product = new Product();
        product1 = new Product();
        product.setName("johndoe");
        product.setProductId("34");
        product.setPrice(100.000000000000);
        product.setId(31);
        product.setName("johndoe");
        product1.setProductId("34");
        product1.setPrice(100.000000000000);
        product1.setId(89);
        
        
        
        
    }

    @Test    
    public void deleteid() {
    	System.out.println("----->"+product.getId()+"---->");
//    	productRepository=new ProductRepository();
    	productRepository.save(product);
    	System.out.println(productRepository.findAll());
    	
    	productRepository.delete(product.getId());
    	
    
               System.out.println("********************************************************");

    }

  

}
