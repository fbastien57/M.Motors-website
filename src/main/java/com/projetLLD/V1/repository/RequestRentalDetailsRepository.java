package com.projetLLD.V1.repository;

import com.projetLLD.V1.entity.RequestRentalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestRentalDetailsRepository extends JpaRepository<RequestRentalDetails,Long> {

    Optional<RequestRentalDetails> findByRequestId(Long requestId);
}
