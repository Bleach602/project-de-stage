package IFFPO_Web_Platform.service.implementation;

import IFFPO_Web_Platform.dto.cloudinary.CloudinaryResponse;
import IFFPO_Web_Platform.entity.Document;
import IFFPO_Web_Platform.entity.FicheInscription;
import IFFPO_Web_Platform.entity.Utilisateur;
import IFFPO_Web_Platform.entity.enums.TypeDocument;
import IFFPO_Web_Platform.repository.FicheInscriptionRepository;
import IFFPO_Web_Platform.service.Cloudinary.CloudinaryService;
import IFFPO_Web_Platform.service.PdfGeneratorService;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private final CloudinaryService cloudinaryService;
    private final FicheInscriptionRepository ficheRepository;

    // ============================================================
    // PALETTE GRAPHIQUE
    // ============================================================
    private static final Color BLEU_INSTITUTIONNEL = new Color(11, 30, 61);
    private static final Color OR_INSTITUTIONNEL = new Color(163, 128, 31);
    private static final Color ROUGE_CAMEROUN = new Color(206, 17, 38);
    private static final Color VERT_CAMEROUN = new Color(0, 150, 60);
    private static final Color GRIS_TEXTE = new Color(50, 50, 50);
    private static final Color GRIS_LIGNE = new Color(210, 210, 210);
    private static final Color GRIS_DISCRET = new Color(150, 150, 150);
    private static final Color CREME_FOND = new Color(250, 247, 240);

    private static final String FONT_TIMES = "Times-Roman";
    private static final String FONT_TIMES_BOLD = "Times-Bold";
    private static final String FONT_TIMES_ITALIC = "Times-Italic";

    // Largeur uniforme pour toutes les sections
    private static final float[] LARGEURS_SECTIONS = new float[]{25f, 75f};
    private static final float LARGEUR_TABLEAU = 70f; // 70% de la largeur

    // ============================================================
    // GÉNÉRATION DU PDF
    // ============================================================
    @Override
    public void genererPdf(FicheInscription fiche) {
        if (fiche == null) {
            throw new IllegalArgumentException("La fiche d'inscription est obligatoire.");
        }

        log.info("==== DEBUT GENERATION FICHE PDF (id={}) ====", fiche.getId());

        String nomFichier = "FICHE_INSCRIPTION.pdf";

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            com.lowagie.text.Document pdfDocument = new com.lowagie.text.Document(
                    PageSize.A4, 36, 36, 28, 32
            );

            PdfWriter writer = PdfWriter.getInstance(pdfDocument, outputStream);
            writer.setPageEvent(new TamponSecurise());
            writer.setInitialLeading(11);

            pdfDocument.open();

            String nom = Optional.ofNullable(fiche.getUtilisateur())
                    .map(Utilisateur::getNom).orElse("N/A");
            String prenom = Optional.ofNullable(fiche.getUtilisateur())
                    .map(Utilisateur::getPrenom).orElse("N/A");
            String telephone = Optional.ofNullable(fiche.getUtilisateur())
                    .map(Utilisateur::getTelephone).orElse("N/A");

            // 1. EN-TÊTE INSTITUTIONNEL
            ajouterEnTeteInstitutionnel(pdfDocument);

            // 2. TITRE
            ajouterBandeauTitre(pdfDocument);

            // RUBRIQUE 1 : IDENTITÉ DU CANDIDAT (photo à droite)
            ajouterSectionIdentite(pdfDocument, fiche, nom, prenom, telephone);

            // RUBRIQUE 2 : PARCOURS ACADÉMIQUE
            ajouterSectionParcours(pdfDocument, fiche);

            // RUBRIQUE 3 : FORMATION CHOISIE
            ajouterSectionFormation(pdfDocument, fiche);

            // RUBRIQUE 4 : RESPONSABLE LÉGAL
            ajouterSectionResponsable(pdfDocument, fiche);

            // RUBRIQUE 5 : SIGNATURES
            ajouterSignatures(pdfDocument);

            pdfDocument.close();

            byte[] pdfBytes = outputStream.toByteArray();
            if (pdfBytes.length == 0) {
                throw new IOException("Le PDF généré est vide.");
            }

            log.info("PDF généré : {} octets", pdfBytes.length);

            CloudinaryResponse response = cloudinaryService.uploadFile(
                    pdfBytes, nomFichier, TypeDocument.FICHE_INSCRIPTION);

            IFFPO_Web_Platform.entity.Document documentEntity = new IFFPO_Web_Platform.entity.Document();
            documentEntity.setTypeDocument(TypeDocument.FICHE_INSCRIPTION);
            documentEntity.setNomFichier(nomFichier);
            documentEntity.setUrl(response.getSecureUrl());
            documentEntity.setPublicId(response.getPublicId());
            documentEntity.setResourceType(response.getResourceType());

            fiche.setFichePdf(documentEntity);
            ficheRepository.save(fiche);

            log.info("==== PDF ENREGISTRE AVEC SUCCES — fiche={} ====", fiche.getId());

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF.", e);
            throw new RuntimeException("Erreur lors de la génération du PDF.", e);
        }
    }

    // ============================================================
    // EN-TÊTE INSTITUTIONNEL
    // ============================================================
    private void ajouterEnTeteInstitutionnel(com.lowagie.text.Document pdfDocument)
            throws DocumentException {

        LineSeparator topLine = new LineSeparator();
        topLine.setLineColor(OR_INSTITUTIONNEL);
        topLine.setLineWidth(1.5f);
        pdfDocument.add(topLine);

        PdfPTable entete = new PdfPTable(3);
        entete.setWidthPercentage(100);
        entete.setWidths(new float[]{14f, 72f, 14f});
        entete.setSpacingAfter(4);

        // Logo Cameroun
        PdfPCell cellLogoCam = new PdfPCell();
        cellLogoCam.setBorder(Rectangle.NO_BORDER);
        cellLogoCam.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellLogoCam.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Image logoCam = chargerLogo("cameroun.jpeg");
        if (logoCam != null) {
            logoCam.scaleToFit(38, 42);
            cellLogoCam.addElement(logoCam);
        }
        entete.addCell(cellLogoCam);

        // Texte central
        PdfPCell cellTexte = new PdfPCell();
        cellTexte.setBorder(Rectangle.NO_BORDER);
        cellTexte.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellTexte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellTexte.setPadding(0);

        Paragraph republique = new Paragraph("REPUBLIQUE DU CAMEROUN",
                FontFactory.getFont(FONT_TIMES_BOLD, 10, BLEU_INSTITUTIONNEL));
        republique.setAlignment(Element.ALIGN_CENTER);
        republique.setSpacingAfter(1);
        cellTexte.addElement(republique);

        Paragraph devise = new Paragraph("Paix — Travail — Patrie",
                FontFactory.getFont(FONT_TIMES_ITALIC, 8, ROUGE_CAMEROUN));
        devise.setAlignment(Element.ALIGN_CENTER);
        devise.setSpacingAfter(1);
        cellTexte.addElement(devise);

        Paragraph sep = new Paragraph("****************",
                FontFactory.getFont(FONT_TIMES, 7, GRIS_DISCRET));
        sep.setAlignment(Element.ALIGN_CENTER);
        sep.setSpacingAfter(1);
        cellTexte.addElement(sep);

        Paragraph ministere = new Paragraph("MINISTERE DE L'EMPLOI ET DE LA FORMATION PROFESSIONNELLE",
                FontFactory.getFont(FONT_TIMES_BOLD, 7.5f, BLEU_INSTITUTIONNEL));
        ministere.setAlignment(Element.ALIGN_CENTER);
        ministere.setSpacingAfter(1);
        cellTexte.addElement(ministere);

        Paragraph sep2 = new Paragraph("****************",
                FontFactory.getFont(FONT_TIMES, 7, GRIS_DISCRET));
        sep2.setAlignment(Element.ALIGN_CENTER);
        sep2.setSpacingAfter(1);
        cellTexte.addElement(sep2);

        Paragraph institut = new Paragraph("INSTITUT DE FORMATION PROFESSIONNELLE LES PERLES D'OR",
                FontFactory.getFont(FONT_TIMES_BOLD, 10, OR_INSTITUTIONNEL));
        institut.setAlignment(Element.ALIGN_CENTER);
        institut.setSpacingAfter(1);
        cellTexte.addElement(institut);

        Paragraph agrement = new Paragraph("Agrément MINEFOP N° 00000444/MINEFOP/SG/DFOP/SDGSF/CSACD/CBAC du 19 Nov. 2021",
                FontFactory.getFont(FONT_TIMES, 6, GRIS_TEXTE));
        agrement.setAlignment(Element.ALIGN_CENTER);
        cellTexte.addElement(agrement);

        entete.addCell(cellTexte);

        // Logo IFP
        PdfPCell cellLogoIfp = new PdfPCell();
        cellLogoIfp.setBorder(Rectangle.NO_BORDER);
        cellLogoIfp.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellLogoIfp.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Image logoIfp = chargerLogo("logo.jpg");
        if (logoIfp != null) {
            logoIfp.scaleToFit(38, 42);
            cellLogoIfp.addElement(logoIfp);
        }
        entete.addCell(cellLogoIfp);

        pdfDocument.add(entete);

        LineSeparator sepBas = new LineSeparator();
        sepBas.setLineColor(OR_INSTITUTIONNEL);
        sepBas.setLineWidth(1f);
        pdfDocument.add(sepBas);

        pdfDocument.add(new Paragraph(" "));
    }

    // ============================================================
    // CHARGEMENT DES LOGOS
    // ============================================================
    private Image chargerLogo(String path) {
        try {
            ClassPathResource resource = new ClassPathResource("static/img/" + path);
            if (resource.exists()) {
                byte[] bytes = resource.getInputStream().readAllBytes();
                return Image.getInstance(bytes);
            }
            log.warn("Logo non trouvé : {}", path);
            return null;
        } catch (Exception e) {
            log.warn("Impossible de charger le logo {} : {}", path, e.getMessage());
            return null;
        }
    }

    // ============================================================
    // BANDEAU TITRE
    // ============================================================
    private void ajouterBandeauTitre(com.lowagie.text.Document pdfDocument) throws DocumentException {
        Paragraph titre = new Paragraph("FICHE D'INSCRIPTION",
                FontFactory.getFont(FONT_TIMES_BOLD, 15, BLEU_INSTITUTIONNEL));
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingBefore(2);
        titre.setSpacingAfter(1);
        pdfDocument.add(titre);

        Paragraph sousTitre = new Paragraph("Année académique " + LocalDate.now().getYear() + "-" + (LocalDate.now().getYear() + 1),
                FontFactory.getFont(FONT_TIMES_ITALIC, 8, GRIS_TEXTE));
        sousTitre.setAlignment(Element.ALIGN_CENTER);
        sousTitre.setSpacingAfter(8);
        pdfDocument.add(sousTitre);
    }

    // ============================================================
    // RUBRIQUE 1 : IDENTITÉ DU CANDIDAT (PHOTO À DROITE)
    // ============================================================
    private void ajouterSectionIdentite(com.lowagie.text.Document pdfDocument,
                                        FicheInscription fiche,
                                        String nom, String prenom, String telephone) throws DocumentException {

        ajouterTitreSection(pdfDocument, "1.", "IDENTITÉ DU CANDIDAT");

        String dateNaiss = fiche.getDateNaissance() != null
                ? fiche.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "N/A";

        String lieu = Optional.ofNullable(fiche.getLieuNaissance()).orElse("N/A");
        String sexe = fiche.getSexe() != null ? fiche.getSexe().name() : "N/A";
        String adresse = Optional.ofNullable(fiche.getAdresse()).orElse("N/A");

        PdfPTable ligne = new PdfPTable(2);
        ligne.setWidthPercentage(100);
        ligne.setWidths(new float[]{70f, 30f}); // Inversé : informations à gauche, photo à droite
        ligne.setSpacingAfter(8);

        // ============================================================
        // CELLULE GAUCHE : INFORMATIONS PERSONNELLES
        // ============================================================
        PdfPCell colInfos = new PdfPCell();
        colInfos.setBorder(Rectangle.NO_BORDER);
        colInfos.setPaddingRight(10);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(LARGEURS_SECTIONS);

        boolean alterne = true;
        alterne = ajouterLigneDonnee(table, "Nom", nom.toUpperCase(), alterne);
        alterne = ajouterLigneDonnee(table, "Prénom", prenom, alterne);
        alterne = ajouterLigneDonnee(table, "Date de naissance", dateNaiss, alterne);
        alterne = ajouterLigneDonnee(table, "Lieu de naissance", lieu, alterne);
        alterne = ajouterLigneDonnee(table, "Sexe", sexe, alterne);
        alterne = ajouterLigneDonnee(table, "Téléphone", telephone, alterne);
        ajouterLigneDonnee(table, "Adresse", adresse, alterne);

        colInfos.addElement(table);

        // ============================================================
        // CELLULE DROITE : PHOTO DU CANDIDAT
        // ============================================================
        PdfPCell colPhoto = new PdfPCell();
        colPhoto.setBorder(Rectangle.NO_BORDER);
        colPhoto.setHorizontalAlignment(Element.ALIGN_RIGHT);
        colPhoto.setVerticalAlignment(Element.ALIGN_TOP);

        PdfPTable cadrePhoto = new PdfPTable(1);
        cadrePhoto.setWidthPercentage(80);
        cadrePhoto.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell cellulePhoto = new PdfPCell();
        cellulePhoto.setBorder(Rectangle.BOX);
        cellulePhoto.setBorderColor(GRIS_LIGNE);
        cellulePhoto.setBorderWidth(0.6f);
        cellulePhoto.setBackgroundColor(CREME_FOND);
        cellulePhoto.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellulePhoto.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cellulePhoto.setPadding(5);
        cellulePhoto.setMinimumHeight(110);

        boolean photoAjoutee = ajouterPhotoCloudinary(fiche, cellulePhoto);
        if (!photoAjoutee) {
            Paragraph mention = new Paragraph("PHOTO\nD'IDENTITÉ",
                    FontFactory.getFont(FONT_TIMES, 8, new Color(180, 180, 180)));
            mention.setAlignment(Element.ALIGN_CENTER);
            cellulePhoto.addElement(mention);
        }

        cadrePhoto.addCell(cellulePhoto);
        colPhoto.addElement(cadrePhoto);

        ligne.addCell(colInfos);
        ligne.addCell(colPhoto);
        pdfDocument.add(ligne);
    }

    // ============================================================
    // RUBRIQUE 2 : PARCOURS ACADÉMIQUE
    // ============================================================
    private void ajouterSectionParcours(com.lowagie.text.Document pdfDocument, FicheInscription fiche)
            throws DocumentException {

        ajouterTitreSection(pdfDocument, "2.", "PARCOURS ACADÉMIQUE");

        String diplome = Optional.ofNullable(fiche.getDiplomePlusEleve()).orElse("N/A");
        String annee = fiche.getAnneeObtention() != null ? String.valueOf(fiche.getAnneeObtention()) : "N/A";

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(LARGEUR_TABLEAU);
        table.setWidths(LARGEURS_SECTIONS);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setSpacingAfter(8);

        boolean alterne = true;
        alterne = ajouterLigneDonnee(table, "Diplôme le plus élevé", diplome, alterne);
        ajouterLigneDonnee(table, "Année d'obtention", annee, alterne);

        pdfDocument.add(table);
    }

    // ============================================================
    // RUBRIQUE 3 : FORMATION CHOISIE
    // ============================================================
    private void ajouterSectionFormation(com.lowagie.text.Document pdfDocument, FicheInscription fiche)
            throws DocumentException {

        ajouterTitreSection(pdfDocument, "3.", "FORMATION CHOISIE");

        String filiere = fiche.getSpecialite() != null && fiche.getSpecialite().getFiliere() != null
                ? fiche.getSpecialite().getFiliere().getNom()
                : "N/A";
        String specialite = fiche.getSpecialite() != null
                ? fiche.getSpecialite().getNom()
                : "N/A";

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(LARGEUR_TABLEAU);
        table.setWidths(LARGEURS_SECTIONS);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setSpacingAfter(8);

        boolean alterne = true;
        alterne = ajouterLigneDonnee(table, "Filière", filiere, alterne);
        ajouterLigneDonnee(table, "Spécialité", specialite, alterne);

        pdfDocument.add(table);
    }

    // ============================================================
    // RUBRIQUE 4 : RESPONSABLE LÉGAL
    // ============================================================
    private void ajouterSectionResponsable(com.lowagie.text.Document pdfDocument, FicheInscription fiche)
            throws DocumentException {

        ajouterTitreSection(pdfDocument, "4.", "RESPONSABLE LÉGAL");

        String nomResp = Optional.ofNullable(fiche.getNomResponsable()).orElse("N/A");
        String prenomResp = Optional.ofNullable(fiche.getPrenomResponsable()).orElse("N/A");
        String telResp = Optional.ofNullable(fiche.getTelephoneResponsable()).orElse("N/A");
        String adrResp = Optional.ofNullable(fiche.getAdresseResponsable()).orElse("N/A");
        String statutResp = fiche.getStatutResponsable() != null ? fiche.getStatutResponsable().name() : "N/A";

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(LARGEUR_TABLEAU);
        table.setWidths(LARGEURS_SECTIONS);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setSpacingAfter(10);

        boolean alterne = true;
        alterne = ajouterLigneDonnee(table, "Nom", nomResp, alterne);
        alterne = ajouterLigneDonnee(table, "Prénom", prenomResp, alterne);
        alterne = ajouterLigneDonnee(table, "Téléphone", telResp, alterne);
        alterne = ajouterLigneDonnee(table, "Adresse", adrResp, alterne);
        ajouterLigneDonnee(table, "Statut", statutResp, alterne);

        pdfDocument.add(table);
    }

    // ============================================================
    // RUBRIQUE 5 : SIGNATURES
    // ============================================================
    private void ajouterSignatures(com.lowagie.text.Document pdfDocument) throws DocumentException {

        ajouterTitreSection(pdfDocument, "5.", "SIGNATURES");

        String dateJour = "Fait à Foumban, le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidths(new float[]{50f, 50f});
        table.setSpacingBefore(6);

        // Signature Candidat
        PdfPCell sigCandidat = new PdfPCell();
        sigCandidat.setBorder(Rectangle.NO_BORDER);
        sigCandidat.setHorizontalAlignment(Element.ALIGN_CENTER);
        sigCandidat.setPaddingTop(2);

        Paragraph enteteCandidat = new Paragraph(dateJour,
                FontFactory.getFont(FONT_TIMES, 7, GRIS_DISCRET));
        enteteCandidat.setAlignment(Element.ALIGN_CENTER);
        sigCandidat.addElement(enteteCandidat);

        sigCandidat.addElement(new Paragraph(" "));
        sigCandidat.addElement(new Paragraph(" "));

        LineSeparator ligneCandidat = new LineSeparator();
        ligneCandidat.setLineColor(GRIS_LIGNE);
        ligneCandidat.setLineWidth(0.4f);
        sigCandidat.addElement(ligneCandidat);

        Paragraph legendeCandidat = new Paragraph("Signature du candidat",
                FontFactory.getFont(FONT_TIMES_BOLD, 7.5f, GRIS_TEXTE));
        legendeCandidat.setAlignment(Element.ALIGN_CENTER);
        legendeCandidat.setSpacingBefore(1);
        sigCandidat.addElement(legendeCandidat);

        // Signature Administration
        PdfPCell sigAdmin = new PdfPCell();
        sigAdmin.setBorder(Rectangle.NO_BORDER);
        sigAdmin.setHorizontalAlignment(Element.ALIGN_CENTER);
        sigAdmin.setPaddingTop(2);

        Paragraph enteteAdmin = new Paragraph("Pour l'administration",
                FontFactory.getFont(FONT_TIMES, 7, GRIS_DISCRET));
        enteteAdmin.setAlignment(Element.ALIGN_CENTER);
        sigAdmin.addElement(enteteAdmin);

        sigAdmin.addElement(new Paragraph(" "));
        sigAdmin.addElement(new Paragraph(" "));

        LineSeparator ligneAdmin = new LineSeparator();
        ligneAdmin.setLineColor(GRIS_LIGNE);
        ligneAdmin.setLineWidth(0.4f);
        sigAdmin.addElement(ligneAdmin);

        Paragraph legendeAdmin = new Paragraph("Visa et cachet de l'institut",
                FontFactory.getFont(FONT_TIMES_BOLD, 7.5f, GRIS_TEXTE));
        legendeAdmin.setAlignment(Element.ALIGN_CENTER);
        legendeAdmin.setSpacingBefore(1);
        sigAdmin.addElement(legendeAdmin);

        table.addCell(sigCandidat);
        table.addCell(sigAdmin);
        pdfDocument.add(table);
    }

    // ============================================================
    // TITRE DE SECTION
    // ============================================================
    private void ajouterTitreSection(com.lowagie.text.Document pdfDocument, String numero, String titre)
            throws DocumentException {

        PdfPTable entete = new PdfPTable(1);
        entete.setWidthPercentage(100);
        entete.setSpacingBefore(6);
        entete.setSpacingAfter(3);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(OR_INSTITUTIONNEL);
        cell.setBorderWidthBottom(0.6f);
        cell.setPaddingBottom(2);

        Paragraph p = new Paragraph();
        p.add(new Chunk(numero + "  ",
                FontFactory.getFont(FONT_TIMES_BOLD, 10, OR_INSTITUTIONNEL)));
        p.add(new Chunk(titre,
                FontFactory.getFont(FONT_TIMES_BOLD, 10, BLEU_INSTITUTIONNEL)));

        cell.addElement(p);
        entete.addCell(cell);
        pdfDocument.add(entete);
    }

    // ============================================================
    // LIGNE DE DONNÉE
    // ============================================================
    private boolean ajouterLigneDonnee(PdfPTable table, String label, String valeur, boolean fondAlterne) {
        Color fond = fondAlterne ? CREME_FOND : Color.WHITE;

        PdfPCell cellLabel = new PdfPCell(new Phrase(label,
                FontFactory.getFont(FONT_TIMES_BOLD, 8, BLEU_INSTITUTIONNEL)));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setBackgroundColor(fond);
        cellLabel.setPadding(3);

        PdfPCell cellValeur = new PdfPCell(new Phrase(valeur,
                FontFactory.getFont(FONT_TIMES, 9, Color.BLACK)));
        cellValeur.setBorder(Rectangle.NO_BORDER);
        cellValeur.setBackgroundColor(fond);
        cellValeur.setPadding(3);

        table.addCell(cellLabel);
        table.addCell(cellValeur);

        return !fondAlterne;
    }

    // ============================================================
    // PHOTO CLOUDINARY
    // ============================================================
    private boolean ajouterPhotoCloudinary(FicheInscription fiche, PdfPCell frameCell) {
        if (fiche.getPhotoIdentite() == null || fiche.getPhotoIdentite().getUrl() == null) {
            log.warn("Aucune photo associée à la fiche.");
            return false;
        }

        String photoUrl = fiche.getPhotoIdentite().getUrl();

        try {
            URL url = new URL(photoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setInstanceFollowRedirects(true);

            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                log.error("Impossible de récupérer la photo. HTTP {}", status);
                connection.disconnect();
                return false;
            }

            byte[] photoBytes;
            try (InputStream inputStream = connection.getInputStream()) {
                photoBytes = inputStream.readAllBytes();
            }
            connection.disconnect();

            if (photoBytes.length == 0) {
                log.error("Photo vide retournée.");
                return false;
            }

            Image photo = Image.getInstance(photoBytes);
            photo.scaleToFit(90, 105);
            photo.setAlignment(Image.ALIGN_CENTER);
            frameCell.addElement(photo);

            log.info("Photo ajoutée avec succès.");
            return true;

        } catch (Exception e) {
            log.error("Impossible d'intégrer la photo.", e);
            return false;
        }
    }

    // ============================================================
    // TÉLÉCHARGEMENT
    // ============================================================
    @Override
    public void telechargerPdf(Utilisateur utilisateur, HttpServletResponse response) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("L'utilisateur est obligatoire.");
        }

        FicheInscription fiche = ficheRepository.findByUtilisateur(utilisateur)
                .orElseThrow(() -> new RuntimeException("Aucune fiche trouvée."));

        IFFPO_Web_Platform.entity.Document pdfEntity = fiche.getFichePdf();
        if (pdfEntity == null || pdfEntity.getPublicId() == null) {
            throw new RuntimeException("Le PDF n'a pas encore été généré.");
        }

        try {
            byte[] contenu = cloudinaryService.downloadFile(pdfEntity.getPublicId(), pdfEntity.getResourceType());

            if (contenu == null || contenu.length == 0) {
                throw new IOException("Le PDF est vide.");
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + pdfEntity.getNomFichier() + "\"");
            response.setContentLength(contenu.length);
            response.getOutputStream().write(contenu);
            response.getOutputStream().flush();

            log.info("PDF téléchargé : {}", pdfEntity.getNomFichier());

        } catch (Exception e) {
            log.error("Erreur lors du téléchargement.", e);
            throw new RuntimeException("Impossible de télécharger le PDF.", e);
        }
    }

    // ============================================================
    // ÉVÉNEMENT DE PAGE
    // ============================================================
    private class TamponSecurise extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, com.lowagie.text.Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle page = document.getPageSize();

            dessinerCadrePage(canvas, page);
            dessinerPiedDePage(writer, canvas, page);
        }
    }

    private void dessinerCadrePage(PdfContentByte canvas, Rectangle page) {
        float marge = 14f;

        canvas.saveState();

        canvas.setColorStroke(OR_INSTITUTIONNEL);
        canvas.setLineWidth(0.5f);
        canvas.rectangle(marge, marge, page.getWidth() - 2 * marge, page.getHeight() - 2 * marge);
        canvas.stroke();

        canvas.setColorStroke(GRIS_LIGNE);
        canvas.setLineWidth(0.2f);
        canvas.rectangle(marge + 3, marge + 3,
                page.getWidth() - 2 * (marge + 3), page.getHeight() - 2 * (marge + 3));
        canvas.stroke();

        canvas.restoreState();
    }

    private void dessinerPiedDePage(PdfWriter writer, PdfContentByte canvas, Rectangle page) {
        try {
            Font policePied = FontFactory.getFont(FONT_TIMES, 6, GRIS_DISCRET);
            BaseFont baseFont = policePied.getBaseFont();

            canvas.saveState();

            canvas.setColorStroke(GRIS_LIGNE);
            canvas.setLineWidth(0.2f);
            canvas.moveTo(40, 28);
            canvas.lineTo(page.getWidth() - 40, 28);
            canvas.stroke();

            canvas.setColorFill(GRIS_DISCRET);
            canvas.setFontAndSize(baseFont, 6);

            canvas.showTextAligned(Element.ALIGN_CENTER,
                    "INSTITUT DE FORMATION PROFESSIONNELLE LES PERLES D'OR - Foumban, Cameroun",
                    page.getWidth() / 2, 18, 0);

            canvas.showTextAligned(Element.ALIGN_LEFT,
                    "Document officiel - toute falsification est punissable par la loi.",
                    40, 10, 0);

            canvas.showTextAligned(Element.ALIGN_RIGHT,
                    "Page " + writer.getPageNumber(),
                    page.getWidth() - 40, 10, 0);

            canvas.restoreState();
        } catch (Exception e) {
            log.warn("Pied de page non appliqué : {}", e.getMessage());
        }
    }
}