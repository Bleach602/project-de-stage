package IFFPO_Web_Platform.controller;

import IFFPO_Web_Platform.entity.Document;
import IFFPO_Web_Platform.repository.DocumentRepository;
import IFFPO_Web_Platform.service.DocumentService;
import IFFPO_Web_Platform.service.FileStorageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/dashboard/documents")
@PreAuthorize("hasAuthority('PERM_GESTION_CANDIDATURE')")
public class DocumentAdminController {

    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    private final DocumentService documentService;


    public DocumentAdminController(DocumentRepository documentRepository, FileStorageService fileStorageService, DocumentService documentService) {
        this.documentRepository = documentRepository;
        this.fileStorageService = fileStorageService;
        this.documentService = documentService;
    }


    @GetMapping("/{id}/voir")
    public ResponseEntity<Resource> voirDocument(
            @PathVariable Long id) {

        return documentService.voirDocument(id);

    }
}
