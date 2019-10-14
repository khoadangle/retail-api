package com.trilogyed.retailapiservice.model;

import java.util.Objects;



public class InvoiceViewModelResponse extends InvoiceViewModel {

    int points;

    public InvoiceViewModelResponse() {
    }

    public InvoiceViewModelResponse(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InvoiceViewModelResponse that = (InvoiceViewModelResponse) o;
        return getPoints() == that.getPoints();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPoints());
    }

    @Override
    public String toString() {
        return "InvoiceViewModel{" +
                "Invoice{" +
                "invoiceId=" + invoiceId +
                ", customerId=" + customerId +
                ", purchaseDate=" + purchaseDate +
                '}' +
                "invoiceItems=" + invoiceItems +
                '}' +
                "InvoiceViewModelResponse{" +
                "points=" + points +
                '}';
    }
}
