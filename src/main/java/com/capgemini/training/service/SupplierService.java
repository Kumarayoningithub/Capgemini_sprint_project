package com.capgemini.training.service;

import com.capgemini.training.dto.SupplierDTO;
import com.capgemini.training.entity.Address;
import com.capgemini.training.entity.Supplier;
import com.capgemini.training.repository.AddressRepository;
import com.capgemini.training.repository.SupplierRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository repo;

    @Autowired
    private AddressRepository addressRepository;

   
    private SupplierDTO convertToDTO(Supplier s) {
        SupplierDTO dto = new SupplierDTO();

        dto.setSupplierId(s.getSupplierId());
        dto.setName(s.getName());
        dto.setContactPerson(s.getContactPerson());
        dto.setPhoneNumber(s.getPhoneNumber());
        dto.setEmail(s.getEmail());


        if (s.getAddress() != null) {
            dto.setAddressId(s.getAddress().getAddressId());
            dto.setCity(s.getAddress().getCity());
            dto.setState(s.getAddress().getState());
            dto.setStreet(s.getAddress().getStreet());
            dto.setZipcode(s.getAddress().getZipCode());
        }
        return dto;
    }

 
    private Supplier convertToEntity(SupplierDTO dto) {
        Supplier s = new Supplier();

        s.setName(dto.getName());
        s.setContactPerson(dto.getContactPerson());
        s.setPhoneNumber(dto.getPhoneNumber());
        s.setEmail(dto.getEmail());
        s.setAddress(resolveAddress(dto));

        return s;
    }


    public List<SupplierDTO> getAllDTO() {
        return repo.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }


    public SupplierDTO getByIdDTO(int id) {
        Supplier s = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return convertToDTO(s);
    }

  
    public SupplierDTO add(SupplierDTO dto) {
        Supplier saved = repo.save(convertToEntity(dto));
        return convertToDTO(saved);
    }


    public SupplierDTO update(int id, SupplierDTO dto) {
        Supplier s = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        s.setName(dto.getName());
        s.setContactPerson(dto.getContactPerson());
        s.setPhoneNumber(dto.getPhoneNumber());
        s.setEmail(dto.getEmail());
        s.setAddress(resolveAddress(dto));

        return convertToDTO(repo.save(s));
    }


    public List<SupplierDTO> getByNameDTO(String name) {
        return repo.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .toList();
    }

  
    public List<SupplierDTO> getByCityDTO(String city) {
        return repo.findByAddress_CityIgnoreCase(city).stream()
                .map(this::convertToDTO)
                .toList();
    }


    public List<SupplierDTO> getByStateDTO(String state) {
        return repo.findByAddress_StateIgnoreCase(state).stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    public List<SupplierDTO> getByStreetDTO(String street) {
        return repo.findByAddress_StreetIgnoreCase(street).stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    public List<SupplierDTO> getByZipcodeDTO(String zipcode) {
    	return repo.findByAddress_ZipCodeIgnoreCase(zipcode).stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    
    

    private Address resolveAddress(SupplierDTO dto) {
        Integer addressId = dto.getAddressId();
        if (addressId == null && dto.getAddress() != null) {
            addressId = dto.getAddress().getAddressId();
        }
        if (addressId == null) {
            return null;
        }
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }
}