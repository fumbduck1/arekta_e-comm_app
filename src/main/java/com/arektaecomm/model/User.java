package com.arektaecomm.model;

public class User {
    private String id;
    private String email;
    private String role;
    private String profileImageUrl; // Add this field
    private String name;
    private String phone;
    private String billingAddress;
    private String mailingAddress;

    // Update constructors, getters, and setters
    public User(String id, String email, String role, String name, String phone, String billingAddress,
            String mailingAddress) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
        this.phone = phone;
        this.billingAddress = billingAddress;
        this.mailingAddress = mailingAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String url) {
        this.profileImageUrl = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }
}