package com.trilogyed.retailapiservice.model;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class Product implements Serializable {

    private int productId;
    @Size(min = 1, max = 50, message = "Product name must be between {min} and {max} characters.")
    @NotBlank(message = "Please supply a product name.")
    private String productName;
    @Size(min = 1, max = 255, message = "Product description must be between {min} and {max} characters.")
    @NotBlank(message = "Please supply a product description.")
    private String productDescription;
    @NotNull(message = "Please supply a list price.")
    @DecimalMin(value = "0.0", inclusive = true, message = "The min value you can enter for list price is {value}.")
    @DecimalMax(value = "99999.99", inclusive = true, message = "The max value you can enter for list price is {value}")
    private BigDecimal listPrice;
    @NotNull(message = "Please supply a unit cost.")
    @DecimalMin(value = "0.0", inclusive = true, message = "The min value you can enter for unit cost is {value}.")
    @DecimalMax(value = "99999.99", inclusive = true, message = "The max value you can enter for unit cost is {value}")
    private BigDecimal unitCost;



    public Product() {
    }

    public Product(int productId, String productName, String productDescription, BigDecimal listPrice, BigDecimal unitCost) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.listPrice = listPrice;
        this.unitCost = unitCost;
    }


    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public BigDecimal getListPrice() {

        if (listPrice == null) {
            return new BigDecimal(0);
        }
        return listPrice;
    }

    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public BigDecimal getUnitCost() {

        if (unitCost == null) {
            return new BigDecimal(0);
        }
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return getProductId() == product.getProductId() &&
                getProductName().equals(product.getProductName()) &&
                getProductDescription().equals(product.getProductDescription()) &&
                getListPrice().equals(product.getListPrice()) &&
                getUnitCost().equals(product.getUnitCost());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), getProductName(), getProductDescription(), getListPrice(), getUnitCost());
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", listPrice=" + listPrice +
                ", unitCost=" + unitCost +
                '}';
    }

}
