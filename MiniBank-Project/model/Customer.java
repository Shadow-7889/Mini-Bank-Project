package model;

import java.io.Serializable;

import model.annotation.Id;

public class Customer implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private static long customerCounter = 100;

    /** Practical 3: static nested class (needs no Customer object to exist). */
    public static class Address implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String line;
        private final String city;
        private final String pincode;

        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine() {
            return line;
        }

        public String getCity() {
            return city;
        }

        public String getPincode() {
            return pincode;
        }

        @Override
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }
    }

    @Id
    private final String customerId;
    private final String name;
    private final String email;
    private final String mobile;
    private Address address;

    public Customer(String name, String email, String mobile, Address address) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
    }

    public Customer(String name, String email, String mobile) {
        this(name, email, mobile, new Address("-", "-", "-"));
    }

    private static synchronized String generateCustomerId() {
        customerCounter++;
        return "CUST" + customerCounter;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public Address getAddress() {
        return address;
    }

    /** Practical 3: returns a copy; the Address is copied too (deep copy). */
    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();
            if (address != null) {
                copy.address = new Address(address.getLine(), address.getCity(), address.getPincode());
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e); // cannot happen: we implement Cloneable
        }
    }

    @Override
    public String toString() {
        return customerId + " | " + name + " | " + mobile + " | " + email;
    }
}
