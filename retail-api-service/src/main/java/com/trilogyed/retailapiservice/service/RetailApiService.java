package com.trilogyed.retailapiservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.trilogyed.retailapiservice.exception.InsufficientQuantityException;
import com.trilogyed.retailapiservice.exception.NotFoundException;
import com.trilogyed.retailapiservice.model.*;
import com.trilogyed.retailapiservice.util.feign.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class RetailApiService {

    private static final String EXCHANGE = "levelup-exchange";
    private static final String ROUTING_KEY = "levelup.create.retail.service";

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CustomerClient customerClient;
    @Autowired
    private InventoryClient inventoryClient;
    @Autowired
    private LevelUpClient levelUpClient;
    @Autowired
    private InvoiceClient invoiceClient;
    @Autowired
    private ProductClient productClient;

    private final RestTemplate restTemplate;

    public RetailApiService( RabbitTemplate rabbitTemplate, CustomerClient customerClient,
            InventoryClient inventoryClient, LevelUpClient levelUpClient,
            InvoiceClient invoiceClient, ProductClient productClient, RestTemplate restTemplate ) {

        this.customerClient = customerClient;
        this.inventoryClient = inventoryClient;
        this.invoiceClient = invoiceClient;
        this.levelUpClient = levelUpClient;
        this.productClient = productClient;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
    }

    /**
    add an Invoice
    @return InvoiceViewModelResponse (IVM with levelup points)
     */

    @Transactional
    public InvoiceViewModelResponse addInvoice(InvoiceViewModel ivm) {

        /**
        check if that customer valid
         */
        checkIfCustomer(ivm);

        /**
         * check if invoice item valid
         */
        validateInvoiceItems(ivm);

        InvoiceViewModelResponse ivmRes = new InvoiceViewModelResponse();
        ivmRes.setCustomerId(ivm.getCustomerId());
        ivmRes.setInvoiceItems(ivm.getInvoiceItems());

        /**
         * adding invoice to database to create invoiceId
         */
        InvoiceViewModel invoiceVM = invoiceClient.addInvoice(ivm);

        ivmRes.setInvoiceId(invoiceVM.getInvoiceId());
        ivmRes.setPurchaseDate(invoiceVM.getPurchaseDate());

        /**
         * use invoice id to set it in invoiceItem
         * get invoiceItemId from the List<InvoiceItem> to set it to invoiceItemId
         */
        List<InvoiceItem> invoiceItems = ivmRes.getInvoiceItems();
        for (int i = 0; i < ivmRes.getInvoiceItems().size(); i++) {
            invoiceItems.get(i).setInvoiceId(invoiceVM.getInvoiceId());
            invoiceItems.get(i).setInvoiceItemId(invoiceVM.getInvoiceItems().get(i).getInvoiceItemId());
        }

        /**
         * get and set points
         */
        Integer points = calculatePoints(ivm);

        ivmRes.setPoints(points);

        return ivmRes;
    }


    /**
     * HELPER METHODS TO ADD AN INVOICE
     */
    /**
     * Checks for an existing customer
     * @param ivm InvoiceViewModel
     */
    public void checkIfCustomer(InvoiceViewModel ivm) {
        if (customerClient.getCustomer(ivm.getCustomerId()) == null) {
            throw new IllegalArgumentException("There is no customer matching the given id");
        }
    }

    /**
     * Handle invoice item validation
     * @param ivm InvoiceViewModel
     */
    public void validateInvoiceItems(InvoiceViewModel ivm) {

        ivm.getInvoiceItems().forEach(ii -> {

            Inventory inventory = inventoryClient.getInventory(ii.getInventoryId());

            /**
             * checking valid inventoryId
             */
            if (inventory == null) {
                throw new IllegalArgumentException("Inventory id " + ii.getInventoryId() + " is not valid");
            }

            int quantityInStock = inventory.getQuantity();

            /**
             * checking valid quantity comparing to quantity in inventory
             */
            if (ii.getQuantity() > quantityInStock || ii.getQuantity() < 0) {
                throw new InsufficientQuantityException("We do not have that quantity.");
            }

            /**
             * using productId from inventory to check valid product
             */
            if (productClient.getProduct(inventory.getProductId()) == null) {
                throw new IllegalArgumentException("Product " + inventory.getProductId() + " does not exist");
            }

        });

    }

    /**
     * calculate points, send info to queue, gets info from circuit breaker
     * @param ivm
     * @return
     */
    public Integer calculatePoints(InvoiceViewModel ivm) {

        /**
         * calculate total
         */
        BigDecimal invoiceTotal = new BigDecimal("0");
        for (InvoiceItem ii : ivm.getInvoiceItems()) {
            BigDecimal itemTotal = ii.getUnitPrice().multiply(new BigDecimal(ii.getQuantity()));
            invoiceTotal = invoiceTotal.add(itemTotal);
        }

        /**
         * calculate points earned using FLOOR
         */
        Integer pointsEarned =  invoiceTotal.divide(new BigDecimal("50")
                .setScale(0, BigDecimal.ROUND_FLOOR), BigDecimal.ROUND_FLOOR).intValue();

        /**
         * get previous points
         */
        Integer previousPoints = getPoints(ivm.getCustomerId());

        int totalPoints = pointsEarned;

        if (previousPoints != null) {
            totalPoints += previousPoints;
        }

        /**
         * send to queue
         * add new member to levelup with earned points
         */
        if (previousPoints == null) {
            LevelUp levelUp = new LevelUp(ivm.getCustomerId(), pointsEarned, LocalDate.now());
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, levelUp);
        }
        /**
         * add points for existing customer
         */
        else {
            LevelUp levelUp = new LevelUp(
                    levelUpClient.getLevelUpByCustomerId(ivm.getCustomerId()).getLevelUpId(),
                    ivm.getCustomerId(),
                    totalPoints,
                    LocalDate.now());
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, levelUp);
        }

        return  totalPoints;

    }

    /**
     * LEVELUP METHODS
     */
    /**
     *
     * @param customerId
     * @return
     */
    @HystrixCommand(fallbackMethod = "getPointsFallback")
    public Integer getPoints(int customerId) {
        if (levelUpClient.getLevelUpByCustomerId(customerId) == null) {
            return null;
        } else {
            return levelUpClient.getLevelUpByCustomerId(customerId).getPoints();
        }
    }

    /**
     * Fallback method for circuit breaker
     * @param customerId
     * @return
     */
    public Integer getPointsFallback(int customerId){
        throw new NotFoundException("LevelUp could not be retrieved for customer id " + customerId);
    }

    /**
     * INVOICE METHODS
     *
     */

    public InvoiceViewModel getInvoice(int id) {
        return invoiceClient.getInvoice(id);
    }

    public List<InvoiceViewModel> getAllInvoices() {
        List<InvoiceViewModel> invoices = invoiceClient.getAllInvoices();
        return invoiceClient.getAllInvoices();
    }

    public List<InvoiceViewModel> getInvoicesByCustomerId(int id) {
        return invoiceClient.getInvoicesByCustomerId(id);
    }

    /**
     * PRODUCT METHODS
     *
     */

    public Product getProduct(int id) {
        return productClient.getProduct(id);
    }

    public List<Product> getProductsInInventory() {

        List<Product> products = new ArrayList<>();

        List<Inventory> inventory = inventoryClient.getAllInventory();

        /**
         * looping through inventory list to get product id, then get product, add to list
         */
        inventory.forEach(ii ->
            products.add(productClient.getProduct(ii.getProductId())));

        return products;

    }

    public List<Product> getProductsByInvoiceId(int id) {

        List<Product> products = new ArrayList<>();

        InvoiceViewModel invoice = invoiceClient.getInvoice(id);

        /**
         * looping through invoice items to get inventory id
         */
        invoice.getInvoiceItems().forEach(ii -> {
            /**
             * use inventory id to get inventory which has product id
             */
            Inventory inventory = inventoryClient.getInventory(ii.getInventoryId());

            products.add(productClient.getProduct(inventory.getProductId()));

        });

        return products;

    }

}
