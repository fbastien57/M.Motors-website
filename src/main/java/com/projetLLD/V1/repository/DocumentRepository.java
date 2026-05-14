package com.projetLLD.V1.repository;

import com.projetLLD.V1.entity.Document;
import com.projetLLD.V1.enums.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {

    List<Document> findByCategoryAndVisibleTrue(DocumentCategory category);
}
