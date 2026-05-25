package com.projetLLD.V1.controller;

import com.projetLLD.V1.entity.Document;
import com.projetLLD.V1.enums.DocumentCategory;
import com.projetLLD.V1.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentManagerController.class)
class DocumentManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnDocumentList() throws Exception {

        Document doc = Document.builder()
                .id(1L)
                .name("Test doc")
                .description("desc")
                .category(DocumentCategory.LOA_FORM) // IMPORTANT
                .filePath("/tmp/test.pdf")
                .visible(true)
                .build();

        when(documentService.findAll()).thenReturn(List.of(doc));

        mockMvc.perform(get("/manager/documents"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/document/list"))
                .andExpect(model().attributeExists("documents"))
                .andExpect(model().attributeExists("dto"));
    }

    @Test
    void shouldRejectUnauthenticatedUser() throws Exception {

        mockMvc.perform(get("/manager/documents"))
                .andExpect(status().isUnauthorized());
    }
}