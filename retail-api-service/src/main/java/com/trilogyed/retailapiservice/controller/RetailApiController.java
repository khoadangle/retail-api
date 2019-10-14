package com.trilogyed.retailapiservice.controller;

import com.trilogyed.retailapiservice.model.InvoiceViewModel;
import com.trilogyed.retailapiservice.model.InvoiceViewModelResponse;
import com.trilogyed.retailapiservice.model.Product;
import com.trilogyed.retailapiservice.service.RetailApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RefreshScope
@CacheConfig(cacheNames = "retail")
public class RetailApiController {

    @Autowired
    private RetailApiService service;

    @Autowired
    public RetailApiController(RetailApiService service) {
        this.service = service;
    }

    /**
    no cache because points changed frequently
     */
    @RequestMapping(value = "/levelups/customerId/{customerId}", method = RequestMethod.GET)
    public int getLevelUp(@PathVariable("customerId") int customerId) {
        return service.getPoints(customerId);
    }

    /**
    cache the result using invoice id
     */
    @CachePut(key = "#result.getInvoiceId()")
    @RequestMapping(value = "/invoices", method = RequestMethod.POST)
    public InvoiceViewModelResponse addInvoice(@RequestBody InvoiceViewModel ivm) {
        return service.addInvoice(ivm);
    }

    /**
    cache the result using invoice id
     */
    @Cacheable
    @RequestMapping(value = "/invoices/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public InvoiceViewModel getInvoice(@PathVariable("id") int id){
        return service.getInvoice(id);
    }

    /**
    no cache because result changed frequently
     */
    @RequestMapping(value = "/invoices", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<InvoiceViewModel> getAllInvoices() {
        return service.getAllInvoices();
    }

    /**
    no cache because result changed frequently as invoices added
     */
    @RequestMapping(value = "/invoices/customer/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<InvoiceViewModel> getInvoiceByCustomerId(@PathVariable("id") int id){
        return service.getInvoicesByCustomerId(id);
    }

    /**
    no cache because result changed frequently as inventory modified
     */
    @RequestMapping(value = "/products/inventory", method = RequestMethod.GET)
    public List<Product> getProductsInInventory() {
        return service.getProductsInInventory();
    }

    /**
    cache using product id
     */
    @Cacheable
    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Product getProduct(@PathVariable int id) {
        return service.getProduct(id);
    }

    /**
   cache using invoice id
    */
    @Cacheable
    @RequestMapping(value = "/products/invoice/{id}", method = RequestMethod.GET)
    public List<Product> getProductsByInvoiceId(@PathVariable int id) {
        return service.getProductsByInvoiceId(id);
    }

}
