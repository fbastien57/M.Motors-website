package com.projetLLD.V1.repository;

import com.projetLLD.V1.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository  extends JpaRepository<Request, Long> {

    List<Request> findByUserId(Long userId);
}
