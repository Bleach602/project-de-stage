package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.cloudinary.CloudinaryResponse;
import IFFPO_Web_Platform.entity.Document;
import IFFPO_Web_Platform.entity.Paiement;
import IFFPO_Web_Platform.entity.Recu;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import IFFPO_Web_Platform.repository.DocumentRepository;
import IFFPO_Web_Platform.repository.PaiementRepository;
import IFFPO_Web_Platform.repository.RecuRepository;
import IFFPO_Web_Platform.repository.UtilisateurRepository;
import IFFPO_Web_Platform.service.Cloudinary.CloudinaryService;
import IFFPO_Web_Platform.service.RecuGeneratorService;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewRecuGeneratorServiceImpl
        implements RecuGeneratorService {


    // ============================================================
    // DEPENDANCES
    // ============================================================

    private final DocumentRepository documentRepository;

    private final RecuRepository recuRepository;

    private final UtilisateurRepository utilisateurRepository;

    private final PaiementRepository paiementRepository;

    private final CloudinaryService cloudinaryService;


    // ============================================================
    // PALETTE
    // ============================================================

    private static final Color COLOR_PRIMARY =
            new Color(15, 34, 64);

    private static final Color COLOR_ACCENT =
            new Color(227, 100, 20);

    private static final Color COLOR_BG_LIGHT =
            new Color(245, 247, 250);

    private static final Color COLOR_TEXT_DARK =
            new Color(40, 40, 40);

    private static final Color COLOR_TEXT_MUTED =
            new Color(100, 110, 120);

    private static final Color COLOR_SUCCESS =
            new Color(16, 124, 65);


    // ============================================================
    // GENERATION DU RECU
    // ============================================================

    @Override
    @Transactional
    public Recu genererRecu(Paiement paiement) {

        if (paiement == null) {

            throw new IllegalArgumentException(
                    "Le paiement est obligatoire."
            );
        }


        String numeroRecu =
                genererNumeroRecu();


        String nomFichier =
                numeroRecu + ".pdf";


        log.info(
                "Génération du reçu : {}",
                numeroRecu
        );


        /*
         * ========================================================
         * PDF EN MÉMOIRE
         * ========================================================
         */
        try (
                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {


            // ====================================================
            // DOCUMENT PDF
            // ====================================================

            com.lowagie.text.Document pdf =
                    new com.lowagie.text.Document(
                            PageSize.A4,
                            20,
                            20,
                            20,
                            20
                    );


            PdfWriter writer =
                    PdfWriter.getInstance(
                            pdf,
                            outputStream
                    );


            pdf.open();


            float pageWidth =
                    pdf.getPageSize().getWidth();


            float pageHeight =
                    pdf.getPageSize().getHeight();


            boolean isLandscape =
                    pageWidth > pageHeight;


            float scaleRatio =
                    isLandscape
                            ? 0.85f
                            : 1.0f;


            int titleFontSize =
                    (int) (16 * scaleRatio);


            int bodyFontSize =
                    (int) (9 * scaleRatio);


            int tablePadding =
                    isLandscape
                            ? 4
                            : 6;


            // ====================================================
            // POLICES
            // ====================================================

            Font fontTitre =
                    FontFactory.getFont(
                            FontFactory.TIMES_ROMAN,
                            titleFontSize,
                            COLOR_PRIMARY
                    );


            Font fontSubTitre =
                    FontFactory.getFont(
                            FontFactory.TIMES_ROMAN,
                            bodyFontSize + 1,
                            COLOR_PRIMARY
                    );


            Font fontNormal =
                    FontFactory.getFont(
                            FontFactory.TIMES_ROMAN,
                            bodyFontSize,
                            COLOR_TEXT_DARK
                    );


            Font fontMuted =
                    FontFactory.getFont(
                            FontFactory.TIMES_ROMAN,
                            bodyFontSize - 1,
                            COLOR_TEXT_MUTED
                    );


            Font fontBold =
                    FontFactory.getFont(
                            FontFactory.TIMES_ROMAN,
                            bodyFontSize,
                            COLOR_TEXT_DARK
                    );


            // ====================================================
            // BORDURE
            // ====================================================

            PdfContentByte cb =
                    writer.getDirectContent();


            cb.setColorStroke(
                    COLOR_PRIMARY
            );


            cb.setLineWidth(
                    1.5f
            );


            cb.rectangle(
                    12,
                    12,
                    pageWidth - 24,
                    pageHeight - 24
            );


            cb.stroke();


            // Barre orange supérieure

            cb.setColorFill(
                    COLOR_ACCENT
            );


            cb.rectangle(
                    12,
                    pageHeight - 16,
                    pageWidth - 24,
                    4
            );


            cb.fill();


            // ====================================================
            // FILIGRANE
            // ====================================================

            PdfContentByte canvas =
                    writer.getDirectContentUnder();


            BaseFont baseFont =
                    BaseFont.createFont(
                            BaseFont.HELVETICA_BOLD,
                            BaseFont.WINANSI,
                            BaseFont.EMBEDDED
                    );


            canvas.saveState();


            PdfGState gState =
                    new PdfGState();


            gState.setFillOpacity(
                    0.04f
            );


            canvas.setGState(
                    gState
            );


            canvas.beginText();


            canvas.setColorFill(
                    COLOR_PRIMARY
            );


            canvas.setFontAndSize(
                    baseFont,
                    isLandscape ? 50 : 60
            );


            canvas.showTextAligned(
                    Element.ALIGN_CENTER,
                    "IFP PERLE D'OR - OFFICIAL RECEIPT",
                    pageWidth / 2,
                    pageHeight / 2,
                    35
            );


            canvas.endText();


            canvas.restoreState();


            // ====================================================
            // EN-TÊTE
            // ====================================================

            PdfPTable headerTable =
                    new PdfPTable(3);


            headerTable.setWidthPercentage(
                    100
            );


            headerTable.setWidths(
                    new float[]{30, 40, 30}
            );


            headerTable.setSpacingAfter(
                    10 * scaleRatio
            );


            // ----------------------------------------------------
            // GAUCHE
            // ----------------------------------------------------

            PdfPCell cGauche =
                    createCleanCell();


            cGauche.addElement(
                    new Paragraph(
                            "RÉPUBLIQUE DU CAMEROUN\n"
                                    + "Paix - Travail - Patrie",
                            fontMuted
                    )
            );


            cGauche.addElement(
                    new Paragraph(
                            "MINEFOP",
                            fontBold
                    )
            );


            // ----------------------------------------------------
            // CENTRE
            // ----------------------------------------------------

            PdfPCell cCentre =
                    createCleanCell();


            cCentre.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );


            try {

                Image logo =
                        Image.getInstance(
                                "src/main/resources/static/img/logo.jpg"
                        );


                logo.scaleToFit(
                        50 * scaleRatio,
                        50 * scaleRatio
                );


                logo.setAlignment(
                        Element.ALIGN_CENTER
                );


                cCentre.addElement(
                        logo
                );


            } catch (Exception e) {

                log.warn(
                        "Logo introuvable lors de la génération du reçu."
                );
            }


            Paragraph pInst =
                    new Paragraph(
                            "IFP PERLE D'OR",
                            fontTitre
                    );


            pInst.setAlignment(
                    Element.ALIGN_CENTER
            );


            cCentre.addElement(
                    pInst
            );


            // ----------------------------------------------------
            // DROITE
            // ----------------------------------------------------

            PdfPCell cDroite =
                    createCleanCell();


            cDroite.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );


            Paragraph pRight =
                    new Paragraph(
                            "REPUBLIC OF CAMEROON\n"
                                    + "Peace - Work - Fatherland",
                            fontMuted
                    );


            pRight.setAlignment(
                    Element.ALIGN_RIGHT
            );


            cDroite.addElement(
                    pRight
            );


            headerTable.addCell(
                    cGauche
            );


            headerTable.addCell(
                    cCentre
            );


            headerTable.addCell(
                    cDroite
            );


            pdf.add(
                    headerTable
            );


            // ====================================================
            // TITRE
            // ====================================================

            PdfPTable titleBanner =
                    new PdfPTable(1);


            titleBanner.setWidthPercentage(
                    100
            );


            PdfPCell cellBanner =
                    new PdfPCell(
                            new Phrase(
                                    "REÇU OFFICIEL DE PAIEMENT",
                                    FontFactory.getFont(
                                            FontFactory.HELVETICA_BOLD,
                                            bodyFontSize + 4,
                                            Color.WHITE
                                    )
                            )
                    );


            cellBanner.setBackgroundColor(
                    COLOR_PRIMARY
            );


            cellBanner.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );


            cellBanner.setPadding(
                    6 * scaleRatio
            );


            cellBanner.setBorder(
                    Rectangle.NO_BORDER
            );


            titleBanner.addCell(
                    cellBanner
            );


            titleBanner.setSpacingAfter(
                    12 * scaleRatio
            );


            pdf.add(
                    titleBanner
            );


            // ====================================================
            // FORMATION
            // ====================================================

            String formation =
                    Optional.ofNullable(
                                    paiement.getCandidature()
                            )
                            .map(
                                    candidature ->
                                            candidature.getSpecialite()
                            )
                            .map(
                                    specialite ->
                                            specialite.getNom()
                            )
                            .orElse(
                                    "Non renseignée"
                            );


            String filiere =
                    Optional.ofNullable(
                                    paiement.getCandidature()
                            )
                            .map(
                                    candidature ->
                                            candidature.getSpecialite()
                            )
                            .map(
                                    specialite ->
                                            specialite.getFiliere()
                            )
                            .map(
                                    filiereEntity ->
                                            filiereEntity.getNom()
                            )
                            .orElse(
                                    "Non renseignée"
                            );


            // ====================================================
            // TABLEAU DES INFORMATIONS
            // ====================================================

            PdfPTable detailsTable =
                    new PdfPTable(4);


            detailsTable.setWidthPercentage(
                    100
            );


            detailsTable.setWidths(
                    new float[]{
                            22,
                            28,
                            22,
                            28
                    }
            );


            detailsTable.setSpacingAfter(
                    15 * scaleRatio
            );


            addGridRow(
                    detailsTable,
                    "N° de Reçu :",
                    numeroRecu,
                    "Date de création :",
                    LocalDate.now().format(
                            DateTimeFormatter.ofPattern(
                                    "dd/MM/yyyy"
                            )
                    ),
                    fontSubTitre,
                    fontBold,
                    fontMuted,
                    tablePadding
            );


            addGridRow(
                    detailsTable,
                    "Référence :",
                    safeString(
                            paiement.getReference()
                    ),
                    "Statut :",
                    "PAIEMENT VALIDÉ",
                    fontSubTitre,
                    fontBold,
                    fontMuted,
                    tablePadding
            );


            addGridRow(
                    detailsTable,
                    "FORMATION",
                    formation,
                    "Filière :",
                    filiere,
                    fontSubTitre,
                    fontBold,
                    fontMuted,
                    tablePadding
            );


            addGridRow(
                    detailsTable,
                    "Candidat :",
                    safeString(
                            paiement.getNomCandidat()
                    ),
                    "Téléphone :",
                    safeString(
                            paiement.getPhoneNumber()
                    ),
                    fontSubTitre,
                    fontNormal,
                    fontMuted,
                    tablePadding
            );


            addGridRow(
                    detailsTable,
                    "Mode Paiement :",
                    safeString(
                            paiement.getModePaiement() != null
                                    ?
                                    paiement
                                    .getModePaiement()
                                    .name()
                                    :
                                    null
                    ),
                    "Montant Réglé :",
                    paiement.getMontant()
                            + " FCFA",
                    fontSubTitre,
                    fontSubTitre,
                    fontMuted,
                    tablePadding
            );


            pdf.add(
                    detailsTable
            );


            // ====================================================
            // STATUT
            // ====================================================

            PdfPTable statusTable =
                    new PdfPTable(2);


            statusTable.setWidthPercentage(
                    100
            );


            statusTable.setWidths(
                    new float[]{60, 40}
            );


            PdfPCell cellNote =
                    createCleanCell();


            cellNote.addElement(
                    new Paragraph(
                            "Note Importante :",
                            fontBold
                    )
            );


            cellNote.addElement(
                    new Paragraph(
                            "Ce document officiel atteste du règlement "
                                    + "complet des frais d'inscription auprès "
                                    + "de l'établissement IFP PERLE D'OR. "
                                    + "À conserver impérativement.",
                            fontMuted
                    )
            );


            PdfPCell cellStamp =
                    createCleanCell();


            cellStamp.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );


            PdfPTable stampPill =
                    new PdfPTable(1);


            PdfPCell pillCell =
                    new PdfPCell(
                            new Phrase(
                                    "✔ PAIEMENT CONFIRMÉ",
                                    FontFactory.getFont(
                                            FontFactory.HELVETICA_BOLD,
                                            bodyFontSize + 1,
                                            COLOR_SUCCESS
                                    )
                            )
                    );


            pillCell.setBackgroundColor(
                    new Color(230, 245, 235)
            );


            pillCell.setBorderColor(
                    COLOR_SUCCESS
            );


            pillCell.setBorderWidth(
                    1f
            );


            pillCell.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );


            pillCell.setPadding(
                    8
            );


            stampPill.addCell(
                    pillCell
            );


            cellStamp.addElement(
                    stampPill
            );


            statusTable.addCell(
                    cellNote
            );


            statusTable.addCell(
                    cellStamp
            );


            statusTable.setSpacingAfter(
                    15 * scaleRatio
            );


            pdf.add(
                    statusTable
            );


            // ====================================================
            // SIGNATURE
            // ====================================================

            PdfPTable signTable =
                    new PdfPTable(2);


            signTable.setWidthPercentage(
                    100
            );


            PdfPCell emptySign =
                    createCleanCell();


            PdfPCell signCell =
                    createCleanCell();


            signCell.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );


            Paragraph pSign =
                    new Paragraph(
                            "Fait à Foumban, le "
                                    + LocalDate.now().format(
                                    DateTimeFormatter.ofPattern(
                                            "dd/MM/yyyy"
                                    )
                            )
                                    + "\n"
                                    + "Le Responsable des Inscriptions"
                                    + "\n\n\n"
                                    + "__________________________",
                            fontBold
                    );


            pSign.setAlignment(
                    Element.ALIGN_RIGHT
            );


            signCell.addElement(
                    pSign
            );


            signTable.addCell(
                    emptySign
            );


            signTable.addCell(
                    signCell
            );


            signTable.setSpacingAfter(
                    10 * scaleRatio
            );


            pdf.add(
                    signTable
            );


            // ====================================================
            // FOOTER
            // ====================================================

            PdfPTable footerTable =
                    new PdfPTable(1);


            footerTable.setWidthPercentage(
                    100
            );


            PdfPCell footerCell =
                    new PdfPCell(
                            new Phrase(
                                    "Institut de Formation Professionnelle "
                                            + "Les PERLES D'OR (IFF-PERLE D'OR) — "
                                            + "Foumban, Cameroun\n"
                                            + "Contact : contact@ifp-po.com | "
                                            + "+237 658 626 681 | "
                                            + "Web : www.ifp-po.com",
                                    fontMuted
                            )
                    );


            footerCell.setBackgroundColor(
                    COLOR_BG_LIGHT
            );


            footerCell.setBorder(
                    Rectangle.TOP
            );


            footerCell.setBorderColor(
                    new Color(
                            210,
                            215,
                            220
                    )
            );


            footerCell.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );


            footerCell.setPadding(
                    6
            );


            footerTable.addCell(
                    footerCell
            );


            pdf.add(
                    footerTable
            );


            // ====================================================
            // FERMETURE PDF
            // ====================================================

            pdf.close();


            byte[] pdfBytes =
                    outputStream.toByteArray();


            if (
                    pdfBytes.length == 0
            ) {

                throw new RuntimeException(
                        "Le PDF généré est vide."
                );
            }


            log.info(
                    "PDF reçu généré en mémoire : {} octets",
                    pdfBytes.length
            );


            // ====================================================
            // CLOUDINARY
            // ====================================================

            CloudinaryResponse cloudinaryResponse =
                    cloudinaryService.uploadFile(
                            pdfBytes,
                            nomFichier,
                            TypeDocument.RECU_PAIEMENT
                    );


            if (
                    cloudinaryResponse == null
            ) {

                throw new RuntimeException(
                        "Cloudinary n'a retourné aucune réponse."
                );
            }


            if (
                    cloudinaryResponse.getPublicId() == null
                            ||
                            cloudinaryResponse
                                    .getPublicId()
                                    .isBlank()
            ) {

                throw new RuntimeException(
                        "Cloudinary n'a pas retourné de publicId."
                );
            }


            log.info(
                    "Reçu envoyé sur Cloudinary : {}",
                    cloudinaryResponse.getPublicId()
            );


            // ====================================================
            // DOCUMENT
            // ====================================================

            IFFPO_Web_Platform.entity.Document document =
                    new IFFPO_Web_Platform.entity.Document();


            document.setNomFichier(
                    nomFichier
            );


            document.setTypeDocument(
                    TypeDocument.RECU_PAIEMENT
            );


            document.setUrl(
                    cloudinaryResponse.getSecureUrl()
            );


            document.setPublicId(
                    cloudinaryResponse.getPublicId()
            );


            document.setResourceType(
                    cloudinaryResponse.getResourceType()
            );


            document.setCandidature(
                    paiement.getCandidature()
            );


            document =
                    documentRepository.save(
                            document
                    );


            // ====================================================
            // RECU
            // ====================================================

            Recu recu =
                    new Recu();


            recu.setNumeroRecu(
                    numeroRecu
            );


            recu.setPaiement(
                    paiement
            );


            recu.setDocument(
                    document
            );


            recu.setNbreTelechargements(
                    0
            );


            Recu savedRecu =
                    recuRepository.save(
                            recu
                    );


            log.info(
                    "✅ Reçu enregistré : id={}, publicId={}",
                    savedRecu.getId(),
                    document.getPublicId()
            );


            return savedRecu;


        } catch (Exception e) {

            log.error(
                    "Erreur lors de la génération du reçu.",
                    e
            );


            throw new RuntimeException(
                    "Erreur lors de la génération du reçu.",
                    e
            );
        }
    }


    // ============================================================
    // CELLULE SANS BORDURE
    // ============================================================

    private PdfPCell createCleanCell() {

        PdfPCell cell =
                new PdfPCell();


        cell.setBorder(
                Rectangle.NO_BORDER
        );


        cell.setPadding(
                2
        );


        return cell;
    }


    // ============================================================
    // GRID
    // ============================================================

    private void addGridRow(
            PdfPTable table,
            String label1,
            String val1,
            String label2,
            String val2,
            Font fontVal1,
            Font fontVal2,
            Font fontLabel,
            int padding
    ) {

        PdfPCell cLabel1 =
                new PdfPCell(
                        new Phrase(
                                label1,
                                fontLabel
                        )
                );


        cLabel1.setBackgroundColor(
                COLOR_BG_LIGHT
        );


        cLabel1.setPadding(
                padding
        );


        cLabel1.setBorderColor(
                Color.WHITE
        );


        PdfPCell cVal1 =
                new PdfPCell(
                        new Phrase(
                                safeString(val1),
                                fontVal1
                        )
                );


        cVal1.setBackgroundColor(
                COLOR_BG_LIGHT
        );


        cVal1.setPadding(
                padding
        );


        cVal1.setBorderColor(
                Color.WHITE
        );


        PdfPCell cLabel2 =
                new PdfPCell(
                        new Phrase(
                                label2,
                                fontLabel
                        )
                );


        cLabel2.setBackgroundColor(
                COLOR_BG_LIGHT
        );


        cLabel2.setPadding(
                padding
        );


        cLabel2.setBorderColor(
                Color.WHITE
        );


        PdfPCell cVal2 =
                new PdfPCell(
                        new Phrase(
                                safeString(val2),
                                fontVal2
                        )
                );


        cVal2.setBackgroundColor(
                COLOR_BG_LIGHT
        );


        cVal2.setPadding(
                padding
        );


        cVal2.setBorderColor(
                Color.WHITE
        );


        table.addCell(
                cLabel1
        );


        table.addCell(
                cVal1
        );


        table.addCell(
                cLabel2
        );


        table.addCell(
                cVal2
        );
    }


    // ============================================================
    // SAFE STRING
    // ============================================================

    private String safeString(
            String value
    ) {

        return value != null
                ? value
                : "N/A";
    }


    // ============================================================
    // NUMERO RECU
    // ============================================================

    private String genererNumeroRecu() {

        return "REC-"
                + LocalDate.now().getYear()
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(
                        0,
                        8
                )
                .toUpperCase();
    }


    // ============================================================
    // SAVE
    // ============================================================

    @Override
    public void save(
            Recu recu
    ) {

        recuRepository.save(
                recu
        );
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    public Recu GetById(
            Long id
    ) {

        return recuRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Reçu introuvable."
                                )
                );
    }


    // ============================================================
    // TELECHARGEMENT PAR EMAIL
    // ============================================================

    @Override
    @Transactional
    public Resource telechargerRecu(
            String email
    ) {

        Utilisateur utilisateur =
                utilisateurRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Utilisateur introuvable."
                                        )
                        );


        Paiement paiement =
                paiementRepository
                        .findFirstByCandidatureUtilisateurOrderByCreatedAtDesc(
                                utilisateur
                        )
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Aucun paiement trouvé."
                                        )
                        );


        Recu recu =
                paiement.getRecu();


        if (
                recu == null
        ) {

            throw new RuntimeException(
                    "Votre reçu n'a pas encore été généré."
            );
        }


        Document document =
                recu.getDocument();


        if (
                document == null
                        ||
                        document.getPublicId() == null
                        ||
                        document.getPublicId().isBlank()
        ) {

            throw new RuntimeException(
                    "Le reçu ne possède pas de publicId Cloudinary."
            );
        }


        try {

            byte[] contenu =
                    cloudinaryService.downloadFile(
                            document.getPublicId(),
                            document.getResourceType()
                    );


            recu.setNbreTelechargements(
                    recu.getNbreTelechargements() + 1
            );


            recuRepository.save(
                    recu
            );


            return new ByteArrayResource(
                    contenu
            );


        } catch (Exception e) {

            log.error(
                    "Erreur lors du téléchargement du reçu.",
                    e
            );


            throw new RuntimeException(
                    "Impossible de télécharger le reçu.",
                    e
            );
        }
    }


    // ============================================================
    // TELECHARGEMENT PAR ID
    // ============================================================


    @Transactional
    public byte[] telechargerRecuParId(
            Long id
    ) {

        Recu recu =
                recuRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Reçu introuvable."
                                        )
                        );


        Document document =
                recu.getDocument();


        if (
                document == null
        ) {

            throw new RuntimeException(
                    "Aucun document associé au reçu."
            );
        }


        if (
                document.getPublicId() == null
                        ||
                        document.getPublicId().isBlank()
        ) {

            throw new RuntimeException(
                    "Le document ne possède pas de publicId Cloudinary."
            );
        }


        try {

            log.info(
                    "Téléchargement du reçu depuis Cloudinary : {}",
                    document.getPublicId()
            );


            byte[] contenu =
                    cloudinaryService.downloadFile(
                            document.getPublicId(),
                            document.getResourceType()
                    );


            recu.setNbreTelechargements(
                    recu.getNbreTelechargements() + 1
            );


            recuRepository.save(
                    recu
            );


            return contenu;


        } catch (Exception e) {

            log.error(
                    "Erreur téléchargement Cloudinary du reçu {}",
                    id,
                    e
            );


            throw new RuntimeException(
                    "Impossible de télécharger le reçu.",
                    e
            );
        }
    }
}