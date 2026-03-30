
package com.capgemini.training.repository;

import com.capgemini.training.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findByNameContainingIgnoreCase(String name);

    List<Supplier> findByAddress_CityIgnoreCase(String city);

    List<Supplier> findByAddress_StateIgnoreCase(String state);
    
    List<Supplier> findByAddress_StreetIgnoreCase(String street);
    
    List<Supplier> findByAddress_ZipCodeIgnoreCase(String zipCode);
    
    
    
    

}
