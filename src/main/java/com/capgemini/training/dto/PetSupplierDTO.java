package com.capgemini.training.dto;

public class PetSupplierDTO {

    private int supplierId;
    private String name;
    private String city;
    private String state;

    public PetSupplierDTO() {}

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}