package com.projetLLD.V1.repository;

import com.projetLLD.V1.entity.RequestDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestDocumentRepository extends JpaRepository<RequestDocument, Long> {

    List<RequestDocument> findByRequestId(Long requestId);
}
