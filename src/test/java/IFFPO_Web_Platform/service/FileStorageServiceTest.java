//package IFFPO_Web_Platform.service;
//
//import IFFPO_Web_Platform.dto.cloudinary.CloudinaryResponse;
//import IFFPO_Web_Platform.entity.Document;
//import IFFPO_Web_Platform.entity.enums.TypeDocument;
//import IFFPO_Web_Platform.repository.DocumentRepository;
//import IFFPO_Web_Platform.service.Cloudinary.CloudinaryService;
//import IFFPO_Web_Platform.service.implementation.FileStorageServiceImpl;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.multipart.MultipartFile;
//
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class FileStorageServiceTest {
//
//
//    @Mock
//    DocumentRepository documentRepository;
//
//    @Mock
//    CloudinaryService cloudinaryService;
//
//    @InjectMocks
//    FileStorageServiceImpl fileStorageService;
//
//    @ExtendWith(MockitoExtension.class)
//
//
//        @Test
//        void doitEnregistrerUnePhotoAvecSucces() {
//
//            // ARRANGE
//
//            MultipartFile photo = mock(MultipartFile.class);
//
//            CloudinaryResponse response = new CloudinaryResponse();
//
//            Document document = new Document();
//
//            response.setSecureUrl("https://cloudinary.com/photo.jpg");
//            response.setPublicId("iffpo/photos/photo123");
//            response.setResourceType("image");
//
//            when(photo.isEmpty())
//                    .thenReturn(false);
//
//            when(photo.getOriginalFilename())
//                    .thenReturn("photo.jpg");
//
//            when(cloudinaryService.uploadFile(
//                    photo,
//                    TypeDocument.PHOTO_IDENTITE
//            )).thenReturn(response);
//
//            when(documentRepository.save(any(Document.class)))
//                    .thenReturn(document);
//
//
//            // ACT
//
//            Document resultat =
//                    fileStorageService.savePhoto(photo);
//
//
//            // ASSERT
//
//            assertNotNull(resultat);
//
//
//            // VERIFY
//
//            verify(cloudinaryService, times(1))
//                    .uploadFile(
//                            photo,
//                            TypeDocument.PHOTO_IDENTITE
//                    );
//
//            verify(documentRepository, times(1))
//                    .save(any(Document.class));
//        }
//    }
//
//
