package com.capgemini.training.service;

import com.capgemini.training.entity.Employee;
import com.capgemini.training.entity.Pet;
import com.capgemini.training.repository.EmployeeRepository;
import com.capgemini.training.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository repo;

    @Autowired
    private PetRepository petRepo;

    public List<Employee> getAll() {
        return repo.findAll();
    }

    public Employee getById(int id) {
        return repo.findById(id).orElse(null);
    }

    public List<Employee> getByName(String name) {
        return repo.findByFirstNameContainingIgnoreCase(name);
    }

    public List<Employee> getByPosition(String position) {
        return repo.findByPositionIgnoreCase(position);
    }

    public Employee add(Employee emp) {
        return repo.save(emp);
    }

    public Employee update(int id, Employee updated) {
        Employee e = getById(id);
        if (e != null) {
            e.setFirstName(updated.getFirstName());
            e.setLastName(updated.getLastName());
            e.setPosition(updated.getPosition());
            e.setPhoneNumber(updated.getPhoneNumber());
            e.setEmail(updated.getEmail());
            return repo.save(e);
        }
        return null;
    }

    public Employee assignPet(int empId, int petId) {
        Employee emp = getById(empId);
        Pet pet = petRepo.findById(petId).orElse(null);

        if (emp != null && pet != null) {
            emp.getPets().add(pet);
            return repo.save(emp);
        }
        return null;
    }

    public Pet assignEmployeeToPet(int petId, int employeeId) {
        Pet pet = petRepo.findById(petId).orElse(null);
        Employee emp = getById(employeeId);

        if (pet != null && emp != null) {
            pet.getEmployees().add(emp);   
            return petRepo.save(pet);
        }
        return null;
    }

    public List<Employee> getEmployeesByPet(int petId) {
        Pet pet = petRepo.findById(petId).orElse(null);
        if (pet != null) {
            return pet.getEmployees();
        }
        return null;
    }
}