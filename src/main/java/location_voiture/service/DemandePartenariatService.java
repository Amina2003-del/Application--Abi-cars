package location_voiture.service;

import location_voiture.persistence.model.DemandePartenariat;
import location_voiture.persistence.model.Document;
import location_voiture.persistence.model.StatutDemande;
import location_voiture.repository.DemandePartenariatRepository;
import location_voiture.repository.DocumentRepository;
import ma.abisoft.persistence.dao.UserRepository;
import ma.abisoft.persistence.model.User;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.io.InputStream;
import com.itextpdf.text.Image;

import java.util.UUID;

import javax.transaction.Transactional;

@Service
public class DemandePartenariatService {

    @Autowired
    private DocumentRepository documentRepo;

    @Autowired
    private EmailService emailService;
    @Autowired
    private DemandePartenariatRepository demandeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DemandePartenariatRepository demandeRepo;

    private final Path uploadDir = Paths.get("uploads"); // ✔️ Chemin relatif au projet

    public DemandePartenariat submitDemande(String nom, String prenom, String email, String telephone, String adresse, MultipartFile[] documents) throws IOException {
        // Vérifier si l'utilisateur existe
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setFirstName(prenom);
            user.setLastName(nom);
            user.setEmail(email);
            user.setTel(telephone);
            user.setEnabled(true);
            userRepository.save(user);
        }

        // Créer la demande
        DemandePartenariat demande = new DemandePartenariat();
        demande.setUtilisateur(user);
        demande.setDateSoumission(LocalDateTime.now());
        demande.setStatut(StatutDemande.EN_ATTENTE);
        demande.setAdresse(adresse);

        // Gérer les documents
        if (documents != null && documents.length > 0) {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                System.out.println("✅ Dossier 'uploads' créé à : " + uploadDir.toAbsolutePath());
            }

            for (MultipartFile file : documents) {
                if (!file.isEmpty()) {
                    if (!file.getContentType().equals("application/pdf")) {
                        throw new IllegalArgumentException("Seuls les fichiers PDF sont acceptés.");
                    }
                    if (file.getSize() > 10 * 1024 * 1024) {
                        throw new IllegalArgumentException("La taille du fichier ne doit pas dépasser 10 Mo.");
                    }

                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path filePath = uploadDir.resolve(fileName);
                    Files.write(filePath, file.getBytes());
                    System.out.println("📥 Fichier enregistré dans : " + filePath.toAbsolutePath());

                    Document document = new Document();
                    document.setFileName(file.getOriginalFilename());
                    document.setFilePath(filePath.toString());
                    document.setContentType(file.getContentType());
                    document.setDemande(demande);
                    demande.addDocument(document);
                }
            }
        }

        return demandeRepository.save(demande);
    }


    public List<DemandePartenariat> getToutesLesDemandes() {
        return demandeRepo.findAll();
    }

    @Transactional
    public String accepter(Long id) {
        DemandePartenariat dp = demandeRepo.findById(id).orElseThrow();
        dp.setStatut(StatutDemande.ACCEPTE);

        String filename = "contrat_" + dp.getUtilisateur().getId() + "_" + System.currentTimeMillis() + ".pdf";
        String folderPath = "uploads/contrats/";
        String fullPath = folderPath + filename;

        try {
            new File(folderPath).mkdirs();

            com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(PageSize.A4, 50, 50, 70, 50);
            PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(fullPath));
            pdfDoc.open();

            // === Fonts ===
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, com.itextpdf.text.BaseColor.BLACK);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, com.itextpdf.text.BaseColor.DARK_GRAY);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12, com.itextpdf.text.BaseColor.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 10, com.itextpdf.text.BaseColor.GRAY);

            InputStream logoStream = getClass().getResourceAsStream("/static/assets/img/logoApps.png");
            if (logoStream != null) {
                byte[] logoBytes = toByteArray(logoStream);
                Image logo = Image.getInstance(logoBytes);
                logo.scaleToFit(100, 60);
                logo.setAlignment(Image.ALIGN_CENTER);
                pdfDoc.add(logo);
            }


            Paragraph header = new Paragraph("RENT CAR AGENCE", titleFont);
            header.setAlignment(Paragraph.ALIGN_CENTER);
            pdfDoc.add(header);

            Paragraph subHeader = new Paragraph("Contrat de Partenariat Professionnel", subtitleFont);
            subHeader.setAlignment(Paragraph.ALIGN_CENTER);
            subHeader.setSpacingAfter(20f);
            pdfDoc.add(subHeader);

            // === Bloc Infos Partenaire ===
            pdfDoc.add(new Paragraph("Informations du Partenaire", subtitleFont));
            pdfDoc.add(new Paragraph("Nom : " + dp.getUtilisateur().getFirstName() + " " + dp.getUtilisateur().getLastName(), textFont));
            pdfDoc.add(new Paragraph("Email : " + dp.getUtilisateur().getEmail(), textFont));
            pdfDoc.add(new Paragraph("Adresse : " + dp.getAdresse(), textFont));
            pdfDoc.add(new Paragraph("Date de Soumission : " + dp.getDateSoumission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), textFont));
            pdfDoc.add(new Paragraph("Date d'Acceptation : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), textFont));
            pdfDoc.add(new Paragraph("Statut : ACCEPTÉ", textFont));

            pdfDoc.add(new Paragraph(" "));

            // === Bloc Société ===
            pdfDoc.add(new Paragraph("Informations de la Société", subtitleFont));
            pdfDoc.add(new Paragraph("Société : RENT CAR ", textFont));
            pdfDoc.add(new Paragraph("Adresse : 125 Avenue des Bureaux de Printemp, 3000 FES, Maroc", textFont));
            pdfDoc.add(new Paragraph("Téléphone : 07 13 61 53 81", textFont));
            pdfDoc.add(new Paragraph("Email : contact@alaedintours.com", textFont));
            pdfDoc.add(new Paragraph("Horaires : 9h - 18h (Lundi à Vendredi)", textFont));

            pdfDoc.add(new Paragraph(" "));

            // === Signature / Clôture ===
            pdfDoc.add(new Paragraph("Ce contrat est conclu dans le respect des engagements mutuels. Pour toute réclamation ou information complémentaire, contactez-nous.", textFont));
            pdfDoc.add(new Paragraph(" "));
            pdfDoc.add(new Paragraph("Fait à Fès, le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), textFont));

            pdfDoc.add(new Paragraph(" "));
            pdfDoc.add(new Paragraph("Signature (RENT CAR )", textFont));

            // === Pied de page ===
            com.itextpdf.text.Rectangle rect = pdfDoc.getPageSize();
            ColumnText.showTextAligned(writer.getDirectContent(),
                    Element.ALIGN_CENTER,
                    new Phrase("ALAEDIN TOURS - Contrat généré automatiquement", smallFont),
                    (rect.getLeft() + rect.getRight()) / 2,
                    rect.getBottom() + 30, 0);

            pdfDoc.close();

            // === Enregistrement dans la base de données ===
            Document contratDoc = new Document();
            contratDoc.setFileName(filename);
            contratDoc.setContentType("application/pdf");
            contratDoc.setFilePath(fullPath);
            contratDoc.setDemande(dp);
            dp.getDocuments().add(contratDoc);

            documentRepo.save(contratDoc);
            demandeRepo.save(dp);

        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(fullPath);
        emailService.envoyerContrat(
            dp.getUtilisateur().getEmail(),
            dp.getUtilisateur().getNom(),
            file
        );
        return filename;
       
    }

    public byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }


	public void rejeter(Long id) {
        DemandePartenariat dp = demandeRepo.findById(id).orElseThrow();
        dp.setStatut(StatutDemande.REJETE);
        demandeRepo.save(dp);
    }
}