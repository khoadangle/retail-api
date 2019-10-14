package com.trilogyed.retailapiservice.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

public class Customer implements Serializable {

    private int customerId;
    @Size(min = 1, max = 50, message = "First name must be between {min} and {max} characters.")
    @NotBlank(message = "Please supply a first name.")
    private String firstName;
    @Size(min = 1, max = 50, message = "Last name must be between {min} and {max} characters.")
    @NotBlank(message = "Please supply a last name.")
    private String lastName;
    @Size(min = 1, max = 50, message = "Street must be between {min} and {max} characters.")
    @NotBlank(message = "Please supply a street.")
    private String street;
    @Size(min = 1, max = 50, message = "City must be between {min} and {max} characters.")
    @NotBlank(message = "Please supply a city.")
    private String city;
    @Size(min = 1, max = 10, message = "Zip must be between {min} and {max} characters.")
    @NotBlank(message = "Please supply a zip.")
    private String zip;
    @Size(min = 1, max = 75, message = "Email must be between {min} and {max} characters.")
    @NotBlank(message = "Please supply an email.")
    private String email;
    @Size(min = 1, max = 20, message = "Phone must be between {min} and {max} characters.")
    @NotBlank(message = "Please supply a phone number.")
    private String phone;

    public Customer() {
    }

    public Customer(int customerId, String firstName, String lastName, String street, String city, String zip, String email, String phone) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.email = email;
        this.phone = phone;
    }


    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return getCustomerId() == customer.getCustomerId() &&
                getFirstName().equals(customer.getFirstName()) &&
                getLastName().equals(customer.getLastName()) &&
                getStreet().equals(customer.getStreet()) &&
                getCity().equals(customer.getCity()) &&
                getZip().equals(customer.getZip()) &&
                getEmail().equals(customer.getEmail()) &&
                getPhone().equals(customer.getPhone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomerId(), getFirstName(), getLastName(), getStreet(), getCity(), getZip(), getEmail(), getPhone());
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}
