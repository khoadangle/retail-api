package com.trilogyed.retailapiservice.util.feign;

import com.trilogyed.retailapiservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping(value = "/products")
    List<Product> getAllProducts();

    @GetMapping(value = "/products/{id}")
    Product getProduct(@PathVariable int id);

}
