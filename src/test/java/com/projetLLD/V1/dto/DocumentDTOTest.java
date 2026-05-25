package com.projetLLD.V1.dto;

import com.projetLLD.V1.enums.DocumentCategory;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentDTOTest {

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "dummy content".getBytes()
        );

        DocumentDTO dto = new DocumentDTO();

        dto.setId(1L);
        dto.setName("Carte grise");
        dto.setDescription("Document véhicule");
        dto.setCategory(DocumentCategory.LOA_FORM);
        dto.setFile(file);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Carte grise");
        assertThat(dto.getDescription()).isEqualTo("Document véhicule");
        assertThat(dto.getCategory()).isEqualTo(DocumentCategory.LOA_FORM);
        assertThat(dto.getFile()).isEqualTo(file);
    }

    @Test
    void shouldGenerateToString() {

        DocumentDTO dto = new DocumentDTO();
        dto.setId(1L);
        dto.setName("Test");

        String result = dto.toString();

        assertThat(result)
                .contains("id=1")
                .contains("name=Test");
    }

    @Test
    void shouldTestEqualsAndHashCode() {

        DocumentDTO dto1 = new DocumentDTO();
        dto1.setId(1L);
        dto1.setName("Doc");
        dto1.setDescription("Description");
        dto1.setCategory(DocumentCategory.LOA_FORM);

        DocumentDTO dto2 = new DocumentDTO();
        dto2.setId(1L);
        dto2.setName("Doc");
        dto2.setDescription("Description");
        dto2.setCategory(DocumentCategory.LOA_FORM);

        // equals
        assertThat(dto1).isEqualTo(dto2);

        // hashCode
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldTestToString() {

        DocumentDTO dto = new DocumentDTO();
        dto.setId(1L);
        dto.setName("Test");

        String result = dto.toString();

        assertThat(result)
                .contains("id=1")
                .contains("name=Test");
    }

    @Test
    void shouldNotBeEqualWhenDifferent() {

        DocumentDTO dto1 = new DocumentDTO();
        dto1.setId(1L);

        DocumentDTO dto2 = new DocumentDTO();
        dto2.setId(2L);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualToNull() {

        DocumentDTO dto = new DocumentDTO();

        assertThat(dto).isNotEqualTo(null);
    }

    @Test
    void shouldBeEqualToItself() {

        DocumentDTO dto = new DocumentDTO();

        assertThat(dto).isEqualTo(dto);
    }
}