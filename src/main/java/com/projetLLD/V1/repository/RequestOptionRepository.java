package com.projetLLD.V1.repository;

import com.projetLLD.V1.entity.RequestOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestOptionRepository extends JpaRepository<RequestOption,Long> {
}
