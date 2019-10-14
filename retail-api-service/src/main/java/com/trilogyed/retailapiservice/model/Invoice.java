package com.trilogyed.retailapiservice.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Invoice implements Serializable {

    protected int invoiceId;
    @NotNull(message = "Please supply a customer id.")
    @Positive
    protected Integer customerId;
    protected LocalDate purchaseDate;
    @Valid
    private List<InvoiceItem> invoiceItems;



    public Invoice() {
    }

    public Invoice(int invoiceId, Integer customerId, LocalDate purchaseDate, List<InvoiceItem> invoiceItems) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.purchaseDate = purchaseDate;
        this.invoiceItems = invoiceItems;
    }


    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice that = (Invoice) o;

        boolean check = false;
        if(getInvoiceId() == that.getInvoiceId()) {
            check = true;
        }else{
            return false;
        }
        if((getCustomerId() == null && that.getCustomerId() == null) || getCustomerId().equals(that.getCustomerId())){
            check=true;
        }else{
            return false;
        }
        if((getPurchaseDate() == null && that.getPurchaseDate() == null) || getPurchaseDate().equals(that.getPurchaseDate())){
            check = true;
        }else{
            return false;
        }
        if((getInvoiceItems() == null && that.getInvoiceItems() == null) || getInvoiceItems().equals(that.getInvoiceItems())){
            check = true;
        }else{
            return false;
        }
        return check ;

    }

    @Override
    public int hashCode() {
        return Objects.hash(getInvoiceId(), getCustomerId(), getPurchaseDate(), getInvoiceItems());
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId=" + invoiceId +
                ", customerId=" + customerId +
                ", purchaseDate=" + purchaseDate +
                ", invoiceItems=" + invoiceItems +
                '}';
    }

}
