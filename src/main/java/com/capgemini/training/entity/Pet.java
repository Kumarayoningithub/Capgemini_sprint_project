package com.capgemini.training.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int petId;

    private String name;
    private String breed;
    private int age;
    private BigDecimal price;
    private String description;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PetCategory category;

    @ManyToMany
    @JoinTable(
        name = "pet_grooming_relationship",
        joinColumns = @JoinColumn(name = "pet_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<GroomingService> groomingServices = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "pet_vaccination_relationship",
        joinColumns = @JoinColumn(name = "pet_id"),
        inverseJoinColumns = @JoinColumn(name = "vaccination_id")
    )
    private List<Vaccination> vaccinations = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "pet_food_relationship",
        joinColumns = @JoinColumn(name = "pet_id"),
        inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    private List<PetFood> foods = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "pet_supplier_relationship",
        joinColumns = @JoinColumn(name = "pet_id"),
        inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private List<Supplier> suppliers = new ArrayList<>();

    @ManyToMany(mappedBy = "pets")
    private List<Employee> employees = new ArrayList<>();

    public Pet() {}

	public int getPetId() {
		return petId;
	}

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public PetCategory getCategory() {
		return category;
	}

	public void setCategory(PetCategory category) {
		this.category = category;
	}

	public List<GroomingService> getGroomingServices() {
		return groomingServices;
	}

	public void setGroomingServices(List<GroomingService> groomingServices) {
		this.groomingServices = groomingServices;
	}

	public List<Vaccination> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(List<Vaccination> vaccinations) {
		this.vaccinations = vaccinations;
	}

	public List<PetFood> getFoods() {
		return foods;
	}

	public void setFoods(List<PetFood> foods) {
		this.foods = foods;
	}

	public List<Supplier> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

  
    
}