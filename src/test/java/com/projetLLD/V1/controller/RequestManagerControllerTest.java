package com.projetLLD.V1.controller;


import com.projetLLD.V1.entity.Request;
import com.projetLLD.V1.entity.RequestDocument;
import com.projetLLD.V1.entity.User;
import com.projetLLD.V1.entity.Vehicle;
import com.projetLLD.V1.enums.DocumentType;
import com.projetLLD.V1.enums.RequestStatus;
import com.projetLLD.V1.enums.RequestType;
import com.projetLLD.V1.repository.RequestRepository;
import com.projetLLD.V1.service.RequestDocumentService;
import com.projetLLD.V1.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestManagerController.class)
class RequestManagerControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private RequestRepository requestRepository;

    @MockitoBean
    private RequestDocumentService documentService;

    @MockitoBean
    private RequestService requestService;

    @BeforeEach
    void setup() {

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    // =========================
    // LIST (NO THYMELEAF CHECK)
    // =========================
    @Test
    void shouldReturnListPage() throws Exception {

        when(requestRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/manager/requests"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("requests"));
    }

    // =========================
    // APPROVE
    // =========================
    @Test
    void shouldApproveRequest() throws Exception {

        doNothing().when(requestService).approveRequest(1L);

        mockMvc.perform(post("/manager/requests/1/approve"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/requests/1"));

        verify(requestService).approveRequest(1L);
    }

    // =========================
    // REJECT
    // =========================
    @Test
    void shouldRejectRequest() throws Exception {

        doNothing().when(requestService).rejectRequest(1L);

        mockMvc.perform(post("/manager/requests/1/reject"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/requests/1"));

        verify(requestService).rejectRequest(1L);
    }

    // =========================
    // DELETE
    // =========================
    @Test
    void shouldDeleteRequest() throws Exception {

        doNothing().when(requestService).deleteRequest(1L);

        mockMvc.perform(post("/manager/requests/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/requests"));

        verify(requestService).deleteRequest(1L);
    }

    // =========================
    // VALIDATE DOC
    // =========================
    @Test
    void shouldValidateDocument() throws Exception {

        doNothing().when(documentService).validateDocument(10L);

        mockMvc.perform(post("/manager/requests/1/documents/10/validate"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/requests/1"));

        verify(documentService).validateDocument(10L);
    }

    // =========================
    // REJECT DOC
    // =========================
    @Test
    void shouldRejectDocument() throws Exception {

        doNothing().when(documentService).rejectDocument(10L, "bad doc");

        mockMvc.perform(post("/manager/requests/1/documents/10/reject")
                        .param("reason", "bad doc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/requests/1"));

        verify(documentService).rejectDocument(10L, "bad doc");
    }
}