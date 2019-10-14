package com.trilogyed.retailapiservice.service;

import com.trilogyed.retailapiservice.exception.InsufficientQuantityException;
import com.trilogyed.retailapiservice.exception.NotFoundException;
import com.trilogyed.retailapiservice.model.*;
import com.trilogyed.retailapiservice.util.feign.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class RetailApiServiceTest {


    @InjectMocks
    private RetailApiService service;

    @Mock
    private ProductClient productClient;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private InvoiceClient invoiceClient;

    @Mock
    private LevelUpClient levelUpClient;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    static final String EXCHANGE = "levelup-exchange";
    static final String ROUTING_KEY = "levelup.create.retail.service";

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        setUpProductClientMock();
        setUpInventoryClientMock();
        setUpInvoiceClientMock();
        setUpLevelUpClientMock();
        setUpCustomerClientMock();

    }

    /**
     * TESTS FOR PRODUCTS
     */
    @Test
    public void getProduct() {

        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Mario Kart");
        product.setProductDescription("Time to play.");
        product.setListPrice(new BigDecimal(49.99).setScale(2, RoundingMode.HALF_UP));
        product.setUnitCost(new BigDecimal(20.00).setScale(2, RoundingMode.HALF_UP));

        Product product1 = service.getProduct(product.getProductId());

        assertEquals(product, product1);
    }

    @Test
    public void getProductsInInventory() {

        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Mario Kart");
        product.setProductDescription("Time to play.");
        product.setListPrice(new BigDecimal(49.99).setScale(2, RoundingMode.HALF_UP));
        product.setUnitCost(new BigDecimal(20.00).setScale(2, RoundingMode.HALF_UP));

        Product product2 = new Product();
        product2.setProductId(2);
        product2.setProductName("Zelda");
        product2.setProductDescription("The Wind Fish.");
        product2.setListPrice(new BigDecimal(59.99).setScale(2, RoundingMode.HALF_UP));
        product2.setUnitCost(new BigDecimal(39.00).setScale(2, RoundingMode.HALF_UP));

        List<Product> productList = new ArrayList<>();
        productList.add(product);
        productList.add(product2);

        List<Product> productsFromService = service.getProductsInInventory();

        assertEquals(productList, productsFromService);

    }

    @Test
    public void getProductsByInvoiceId() {

        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Mario Kart");
        product.setProductDescription("Time to play.");
        product.setListPrice(new BigDecimal(49.99).setScale(2, RoundingMode.HALF_UP));
        product.setUnitCost(new BigDecimal(20.00).setScale(2, RoundingMode.HALF_UP));

        Product product2 = new Product();
        product2.setProductId(2);
        product2.setProductName("Zelda");
        product2.setProductDescription("The Wind Fish.");
        product2.setListPrice(new BigDecimal(59.99).setScale(2, RoundingMode.HALF_UP));
        product2.setUnitCost(new BigDecimal(39.00).setScale(2, RoundingMode.HALF_UP));

        List<Product> productList = new ArrayList<>();
        productList.add(product);
        productList.add(product2);

        List<Product> productsFromService = service.getProductsByInvoiceId(114);

        assertEquals(productList, productsFromService);

    }

    /**
     * TESTS FOR INVOICE
     */
    @Test
    public void getInvoice() {

        InvoiceViewModel invoiceVM = new InvoiceViewModel();
        invoiceVM.setInvoiceId(2);
        invoiceVM.setCustomerId(2);
        invoiceVM.setPurchaseDate(LocalDate.of(2019, 5, 10));
        invoiceVM.setInvoiceItems(new ArrayList<>());

        InvoiceViewModel invoiceVM1 = service.getInvoice(invoiceVM.getInvoiceId());

        assertEquals(invoiceVM, invoiceVM1);

    }


    @Test
    public void getAllInvoices() {

        // invoice items
        List<InvoiceItem> invoiceItems = new ArrayList<>();

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInvoiceItemId(1);
        invoiceItem.setInvoiceId(114);
        invoiceItem.setInventoryId(1);
        invoiceItem.setQuantity(1);
        invoiceItem.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem);

        InvoiceItem invoiceItem2 = new InvoiceItem();
        invoiceItem2.setInvoiceItemId(2);
        invoiceItem2.setInvoiceId(114);
        invoiceItem2.setInventoryId(2);
        invoiceItem2.setQuantity(5);
        invoiceItem2.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem2);

        InvoiceViewModel invoiceVM = new InvoiceViewModel();
        invoiceVM.setInvoiceId(114);
        invoiceVM.setCustomerId(1);
        invoiceVM.setPurchaseDate(LocalDate.of(2019, 7, 15));
        invoiceVM.setInvoiceItems(invoiceItems);


        InvoiceViewModel invoiceVM2 = new InvoiceViewModel();
        invoiceVM2.setInvoiceId(2);
        invoiceVM2.setCustomerId(2);
        invoiceVM2.setPurchaseDate(LocalDate.of(2019, 5, 10));
        invoiceVM2.setInvoiceItems(new ArrayList<>());

        List<InvoiceViewModel> invoiceList = new ArrayList<>();
        invoiceList.add(invoiceVM);
        invoiceList.add(invoiceVM2);

        List<InvoiceViewModel> fromService = service.getAllInvoices();

        assertEquals(2, fromService.size());
        assertEquals(invoiceList, fromService);

    }


    @Test
    public void getInvoicesByCustomerId() {

        // invoice items
        List<InvoiceItem> invoiceItems = new ArrayList<>();

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInvoiceItemId(1);
        invoiceItem.setInvoiceId(114);
        invoiceItem.setInventoryId(1);
        invoiceItem.setQuantity(1);
        invoiceItem.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem);

        InvoiceItem invoiceItem2 = new InvoiceItem();
        invoiceItem2.setInvoiceItemId(2);
        invoiceItem2.setInvoiceId(114);
        invoiceItem2.setInventoryId(2);
        invoiceItem2.setQuantity(5);
        invoiceItem2.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem2);

        InvoiceViewModel invoiceVM = new InvoiceViewModel();
        invoiceVM.setInvoiceId(114);
        invoiceVM.setCustomerId(1);
        invoiceVM.setPurchaseDate(LocalDate.of(2019, 7, 15));
        invoiceVM.setInvoiceItems(invoiceItems);

        InvoiceViewModel invoiceVM2 = new InvoiceViewModel();
        invoiceVM2.setInvoiceId(2);
        invoiceVM2.setCustomerId(2);
        invoiceVM2.setPurchaseDate(LocalDate.of(2019, 5, 10));
        invoiceVM2.setInvoiceItems(new ArrayList<>());

        List<InvoiceViewModel> invoicesCust1 = new ArrayList<>();
        invoicesCust1.add(invoiceVM);

        List<InvoiceViewModel> invoicesCust2 = new ArrayList<>();
        invoicesCust2.add(invoiceVM2);

        List<InvoiceViewModel> invoicesForCust1FromService = service.getInvoicesByCustomerId(1);

        assertEquals(invoicesCust1, invoicesForCust1FromService);

        List<InvoiceViewModel> invoicesForCust2FromService = service.getInvoicesByCustomerId(2);

        assertEquals(invoicesCust2, invoicesForCust2FromService);

    }

    /**
     * TESTS FOR LEVELUP
     */
    @Test
    public void getPoints() {

        LevelUp levelUpCust1 = new LevelUp();
        levelUpCust1.setLevelUpId(1);
        levelUpCust1.setCustomerId(1);
        levelUpCust1.setPoints(10);
        levelUpCust1.setMemberDate(LocalDate.of(2019, 7, 15));

        int levelUpFromService = service.getPoints(1);

        assertEquals(levelUpCust1.getPoints(), levelUpFromService);

    }

    @Test
    public void addInvoice() {

        List<InvoiceItem> invoiceItemsBefore = new ArrayList<>();

        InvoiceItem invoiceItemBefore = new InvoiceItem();
        invoiceItemBefore.setInventoryId(1);
        invoiceItemBefore.setQuantity(1);
        invoiceItemBefore.setUnitPrice(new BigDecimal("29.99"));

        invoiceItemsBefore.add(invoiceItemBefore);

        InvoiceItem invoiceItem2Before = new InvoiceItem();
        invoiceItem2Before.setInventoryId(2);
        invoiceItem2Before.setQuantity(5);
        invoiceItem2Before.setUnitPrice(new BigDecimal("29.99"));

        invoiceItemsBefore.add(invoiceItem2Before);

        InvoiceViewModel invoiceVM = new InvoiceViewModel();
        invoiceVM.setCustomerId(1);
        invoiceVM.setInvoiceItems(invoiceItemsBefore);

        List<InvoiceItem> invoiceItems = new ArrayList<>();

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInvoiceItemId(1);
        invoiceItem.setInvoiceId(114);
        invoiceItem.setInventoryId(1);
        invoiceItem.setQuantity(1);
        invoiceItem.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem);

        InvoiceItem invoiceItem2 = new InvoiceItem();
        invoiceItem2.setInvoiceItemId(2);
        invoiceItem2.setInvoiceId(114);
        invoiceItem2.setInventoryId(2);
        invoiceItem2.setQuantity(5);
        invoiceItem2.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem2);

        InvoiceViewModelResponse ivmRes = new InvoiceViewModelResponse();
        ivmRes.setInvoiceId(114);
        ivmRes.setCustomerId(1);
        ivmRes.setPurchaseDate(LocalDate.of(2019, 7, 15));
        ivmRes.setInvoiceItems(invoiceItems);

        LevelUp levelUp = new LevelUp();
        levelUp.setLevelUpId(1);
        levelUp.setCustomerId(1);
        levelUp.setPoints(13);
        levelUp.setMemberDate(LocalDate.of(2019, 8, 25));

        ivmRes.setPoints(13);

        InvoiceViewModelResponse ivmResFromService = service.addInvoice(invoiceVM);
        /**
         * check if method was called with that arguments
         */
        verify(rabbitTemplate).convertAndSend(EXCHANGE, ROUTING_KEY, levelUp);
        assertEquals(ivmRes, ivmResFromService);

    }


    @Test
    public void calculatePoints() {

        InvoiceItem invoiceItemBefore = new InvoiceItem();
        invoiceItemBefore.setInventoryId(1);
        invoiceItemBefore.setQuantity(2);
        invoiceItemBefore.setUnitPrice(new BigDecimal("29.99"));

        List<InvoiceItem> invoiceItems3Before = new ArrayList<>();
        invoiceItems3Before.add(invoiceItemBefore);

        InvoiceViewModel ivm = new InvoiceViewModel();
        ivm.setInvoiceId(5);
        ivm.setCustomerId(5);
        ivm.setPurchaseDate(LocalDate.of(2019, 5, 10));
        ivm.setInvoiceItems(invoiceItems3Before);

        int points = service.calculatePoints(ivm);

        LevelUp levelUp = new LevelUp(ivm.getCustomerId(), 1, LocalDate.now());

        /**
         * check if method was called with that arguments
         */
        verify(rabbitTemplate).convertAndSend(EXCHANGE, ROUTING_KEY, levelUp);

        assertEquals(1, points);

    }

    @Test(expected  = IllegalArgumentException.class)
    public void checkIfCustomer() {

        InvoiceViewModel invoiceVM = new InvoiceViewModel();
        invoiceVM.setInvoiceId(114);
        invoiceVM.setCustomerId(200);
        invoiceVM.setPurchaseDate(LocalDate.of(2019, 7, 15));
        invoiceVM.setInvoiceItems(new ArrayList<>());

        service.checkIfCustomer(invoiceVM);

    }

    /**
     * testing fallback method to see if it'll throw an exception
     */
    @Test(expected = NotFoundException.class)
    public void getPointsFallback(){
        service.getPointsFallback(100);
    }

    /**
     * testing if insufficient quantity in stock throws an exception
     */
    @Test(expected = InsufficientQuantityException.class)
    public void checkIfExceptionWhenInsufficientQuantity(){

        List<InvoiceItem> invoiceItems = new ArrayList<>();

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInventoryId(100);
        invoiceItem.setQuantity(5);
        invoiceItem.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem);

        InvoiceViewModel ivm = new InvoiceViewModelResponse();
        ivm.setCustomerId(1);
        ivm.setPurchaseDate(LocalDate.of(2019, 7, 15));
        ivm.setInvoiceItems(invoiceItems);

        service.validateInvoiceItems(ivm);

    }

    /**
     * testing if invalid product throws an exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void checkIfExceptionWhenInvalidProduct(){

        List<InvoiceItem> invoiceItems = new ArrayList<>();

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInventoryId(100);
        invoiceItem.setQuantity(1);
        invoiceItem.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem);

        InvoiceViewModel ivm = new InvoiceViewModelResponse();
        ivm.setCustomerId(1);
        ivm.setPurchaseDate(LocalDate.of(2019, 7, 15));
        ivm.setInvoiceItems(invoiceItems);

        service.validateInvoiceItems(ivm);

    }

    /**
     * testing if invalid inventory throws an exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void checkIfExceptionWhenInvalidInventory(){

        List<InvoiceItem> invoiceItems = new ArrayList<>();

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInventoryId(200);
        invoiceItem.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem);

        InvoiceViewModel ivm = new InvoiceViewModelResponse();
        ivm.setCustomerId(1);
        ivm.setPurchaseDate(LocalDate.of(2019, 7, 15));
        ivm.setInvoiceItems(invoiceItems);

        service.validateInvoiceItems(ivm);

    }

    /**
     *           MOCKS
     */

    public void setUpProductClientMock() {

        Product product = new Product();
        product.setProductName("Mario Kart");
        product.setProductDescription("Time to play.");
        product.setListPrice(new BigDecimal(49.99).setScale(2, RoundingMode.HALF_UP));
        product.setUnitCost(new BigDecimal(20.00).setScale(2, RoundingMode.HALF_UP));

        Product product2 = new Product();
        product2.setProductId(1);
        product2.setProductName("Mario Kart");
        product2.setProductDescription("Time to play.");
        product2.setListPrice(new BigDecimal(49.99).setScale(2, RoundingMode.HALF_UP));
        product2.setUnitCost(new BigDecimal(20.00).setScale(2, RoundingMode.HALF_UP));

        Product product3 = new Product();
        product3.setProductName("Zelda");
        product3.setProductDescription("The Wind Fish.");
        product3.setListPrice(new BigDecimal(59.99).setScale(2, RoundingMode.HALF_UP));
        product3.setUnitCost(new BigDecimal(39.00).setScale(2, RoundingMode.HALF_UP));

        Product product4 = new Product();
        product4.setProductId(2);
        product4.setProductName("Zelda");
        product4.setProductDescription("The Wind Fish.");
        product4.setListPrice(new BigDecimal(59.99).setScale(2, RoundingMode.HALF_UP));
        product4.setUnitCost(new BigDecimal(39.00).setScale(2, RoundingMode.HALF_UP));

        doReturn(product2).when(productClient).getProduct(1);
        doReturn(product4).when(productClient).getProduct(2);
        doReturn(null).when(productClient).getProduct(100);

    }

    public void setUpInventoryClientMock() {

        Inventory inventory = new Inventory();
        inventory.setInventoryId(1);
        inventory.setProductId(1);
        inventory.setQuantity(10);

        Inventory inventory2 = new Inventory();
        inventory2.setInventoryId(2);
        inventory2.setProductId(2);
        inventory2.setQuantity(30);

        Inventory inventory3 = new Inventory();
        inventory3.setInventoryId(100);
        inventory3.setProductId(100);
        inventory3.setQuantity(1);

        List<Inventory> inventoryList = new ArrayList<>();
        inventoryList.add(inventory);
        inventoryList.add(inventory2);

        doReturn(inventoryList).when(inventoryClient).getAllInventory();
        doReturn(inventory).when(inventoryClient).getInventory(inventory.getInventoryId());
        doReturn(inventory2).when(inventoryClient).getInventory(inventory2.getInventoryId());
        doReturn(inventory3).when(inventoryClient).getInventory(inventory3.getInventoryId());
        doReturn(null).when(inventoryClient).getInventory(200);

    }

    public void setUpInvoiceClientMock() {

        List<InvoiceItem> invoiceItemsBefore = new ArrayList<>();

        InvoiceItem invoiceItemBefore = new InvoiceItem();
        invoiceItemBefore.setInventoryId(1);
        invoiceItemBefore.setQuantity(1);
        invoiceItemBefore.setUnitPrice(new BigDecimal("29.99"));

        invoiceItemsBefore.add(invoiceItemBefore);

        InvoiceItem invoiceItem2Before = new InvoiceItem();
        invoiceItem2Before.setInventoryId(2);
        invoiceItem2Before.setQuantity(5);
        invoiceItem2Before.setUnitPrice(new BigDecimal("29.99"));

        invoiceItemsBefore.add(invoiceItem2Before);

        InvoiceViewModel invoiceVMBefore = new InvoiceViewModel();
        invoiceVMBefore.setCustomerId(1);
        invoiceVMBefore.setInvoiceItems(invoiceItemsBefore);

        List<InvoiceItem> invoiceItems = new ArrayList<>();

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setInvoiceItemId(1);
        invoiceItem.setInvoiceId(114);
        invoiceItem.setInventoryId(1);
        invoiceItem.setQuantity(1);
        invoiceItem.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem);

        InvoiceItem invoiceItem2 = new InvoiceItem();
        invoiceItem2.setInvoiceItemId(2);
        invoiceItem2.setInvoiceId(114);
        invoiceItem2.setInventoryId(2);
        invoiceItem2.setQuantity(5);
        invoiceItem2.setUnitPrice(new BigDecimal("29.99"));

        invoiceItems.add(invoiceItem2);

        InvoiceViewModel invoiceVM = new InvoiceViewModel();
        invoiceVM.setInvoiceId(114);
        invoiceVM.setCustomerId(1);
        invoiceVM.setPurchaseDate(LocalDate.of(2019, 7, 15));
        invoiceVM.setInvoiceItems(invoiceItems);

        InvoiceViewModel invoiceVM2 = new InvoiceViewModel();
        invoiceVM2.setInvoiceId(2);
        invoiceVM2.setCustomerId(2);
        invoiceVM2.setPurchaseDate(LocalDate.of(2019, 5, 10));
        invoiceVM2.setInvoiceItems(new ArrayList<>());

        List<InvoiceViewModel> invoiceList = new ArrayList<>();
        invoiceList.add(invoiceVM);
        invoiceList.add(invoiceVM2);

        doReturn(invoiceVM).when(invoiceClient).getInvoice(invoiceVM.getInvoiceId());
        doReturn(invoiceVM2).when(invoiceClient).getInvoice(2);
        doReturn(invoiceList).when(invoiceClient).getAllInvoices();

        doReturn(invoiceVM).when(invoiceClient).addInvoice(invoiceVMBefore);

        List<InvoiceViewModel> invoiceListCust1 = new ArrayList<>();
        invoiceItem.setInvoiceItemId(1);
        invoiceListCust1.add(invoiceVM);

        List<InvoiceViewModel> invoiceListCust2 = new ArrayList<>();
        invoiceListCust2.add(invoiceVM2);

        doReturn(invoiceListCust1).when(invoiceClient).getInvoicesByCustomerId(1);
        doReturn(invoiceListCust2).when(invoiceClient).getInvoicesByCustomerId(2);

    }

    public void setUpLevelUpClientMock() {

        LevelUp levelUpCust1 = new LevelUp();
        levelUpCust1.setLevelUpId(1);
        levelUpCust1.setCustomerId(1);
        levelUpCust1.setPoints(10);
        levelUpCust1.setMemberDate(LocalDate.of(2019, 7, 15));

        doReturn(levelUpCust1).when(levelUpClient).getLevelUpByCustomerId(1);
        doReturn(null).when(levelUpClient).getLevelUpByCustomerId(5);

    }

    public void setUpCustomerClientMock() {

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setFirstName("John");
        customer.setLastName("Adams");
        customer.setStreet("20 Broadway");
        customer.setCity("New York");
        customer.setZip("10001");
        customer.setEmail("ja@gmail.com");
        customer.setPhone("646-123-4567");

        doReturn(customer).when(customerClient).getCustomer(1);
        doReturn(null).when(customerClient).getCustomer(200);

    }

}