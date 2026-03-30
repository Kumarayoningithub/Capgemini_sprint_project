package com.capgemini.training.dto;

public class SupplierDTO {

    private int supplierId;
    private String name;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private Integer addressId;
    private AddressRef address;
    private String city;
    private String state;
    
    private String zipcode;
    private String street;
    
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContactPerson() {
		return contactPerson;
	}
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getAddressId() {
		return addressId;
	}
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	public AddressRef getAddress() {
		return address;
	}
	public void setAddress(AddressRef address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public static class AddressRef {
		private Integer addressId;

		public Integer getAddressId() {
			return addressId;
		}

		public void setAddressId(Integer addressId) {
			this.addressId = addressId;
		}
	}

}