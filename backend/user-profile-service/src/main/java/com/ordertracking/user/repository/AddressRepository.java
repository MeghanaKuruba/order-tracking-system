package com.ordertracking.user.repository;

import com.ordertracking.user.dto.AddressResponse;
import com.ordertracking.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressRepository
        extends JpaRepository<Address, Long> {

    List<Address> findByUserProfileAuthUserIdOrderByCreatedAtAsc(Long authUserId);;

    @Query("""
    SELECT a FROM Address a
    WHERE a.userProfile.authUserId = :authUserId
      AND LOWER(a.doorNoOrBuildingName) = LOWER(:doorNoOrBuildingName)
      AND LOWER(a.street) = LOWER(:street)
      AND LOWER(a.city) = LOWER(:city)
      AND LOWER(a.state) = LOWER(:state)
      AND LOWER(a.country) = LOWER(:country)
      AND a.postalCode = :postalCode
""")
    Optional<Address> findByUserProfileAuthUserIdAndDoorNoOrBuildingNameIgnoreCaseAndStreetIgnoreCaseAndCityIgnoreCaseAndStateIgnoreCaseAndCountryIgnoreCaseAndPostalCode(
            Long authUserId,
            String doorNoOrBuildingName,
            String street,
            String city,
            String state,
            String country,
            String postalCode
    );

    Optional<Address> findByIdAndUserProfileAuthUserId(Long id, Long authUserId);
}