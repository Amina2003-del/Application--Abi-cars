package location_voiture.service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import location_voiture.persistence.model.Facture;
import location_voiture.persistence.model.Locataire;
import location_voiture.persistence.model.Réservation;
import ma.abisoft.persistence.model.User;

@Service
public class PdfFactureGenerator {

    public byte[] genererFacturePDF(Facture facture) throws DocumentException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 40, 30);
        String statut = (facture.getStatut() != null && facture.getStatut().equalsIgnoreCase("payée")) ? "PAYÉ" : "NON PAYÉ";
        BaseColor statutColor = statut.equals("PAYÉ") ? new BaseColor(40, 167, 69) : new BaseColor(220, 53, 69);

        // Création de la police avec style
        Font statusFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        statusFont.setColor(statutColor);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();
           

            // Polices
            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.GRAY);
            Font descriptionFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK); // Police plus petite pour description

            // En-tête
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{50, 50});
            headerTable.setSpacingAfter(20);

            PdfPCell emitterCell = new PdfPCell();
            emitterCell.setBorder(Rectangle.NO_BORDER);
            // Ajout du logo
            try {
                Image logo = Image.getInstance("src/main/resources/static/assets/img/logoApps (2).png");
                logo.scaleToFit(100, 50);
                emitterCell.addElement(logo);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement du logo : " + e.getMessage());
                emitterCell.addElement(new Paragraph("Rent Car", companyFont));
            }
            emitterCell.addElement(new Paragraph("ABIcars", companyFont));
            emitterCell.addElement(new Paragraph("125 Avenue des Bureaux de Printemps", smallFont));
            emitterCell.addElement(new Paragraph("30000 FES, Maroc", smallFont));
            emitterCell.addElement(new Paragraph("Numéro TVA : FR123456789", smallFont));
            emitterCell.addElement(new Paragraph("Téléphone : 07 13 61 53 81", smallFont));
            emitterCell.addElement(new Paragraph("Email : contact@alaedintours.com", smallFont));
            headerTable.addCell(emitterCell);

            PdfPCell invoiceCell = new PdfPCell();
            invoiceCell.setBorder(Rectangle.NO_BORDER);
            invoiceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("fr", "FR"));
            invoiceCell.addElement(new Paragraph("FACTURE", headerFont));
            invoiceCell.addElement(new Paragraph("Numéro : " + (facture.getId() != null ? facture.getId().toString() : "N/A"), valueFont));
            invoiceCell.addElement(new Paragraph("Émise le : " + (facture.getDateEmission() != null ? sdf.format(facture.getDateEmission()) : "N/A"), valueFont));
            Réservation reservation = facture.getReservation();
            double prixTotal = reservation != null && reservation.getPrixTotal() != null ? reservation.getPrixTotal() : 0.0;
            invoiceCell.addElement(new Paragraph("Montant : " + String.format("%.2f €", prixTotal), valueFont));
            invoiceCell.addElement(new Paragraph("Échéance : " + (facture.getDateLimite() != null ? sdf.format(facture.getDateLimite()) : "N/A"), valueFont));
// Vert pour PAYÉ, rouge pour NON PAYÉ
            invoiceCell.addElement(new Paragraph(statut, statusFont));          
            invoiceCell.addElement(new Paragraph("Mode de paiement : " + (facture.getModePaiement() != null ? facture.getModePaiement() : "N/A"), valueFont));
            invoiceCell.addElement(new Paragraph("Référence paiement : " + (facture.getReferencePaiement() != null ? facture.getReferencePaiement() : "N/A"), valueFont));
            headerTable.addCell(invoiceCell);

            document.add(headerTable);
         // Récupération du User depuis la facture
            User user = facture.getClient();

            // Client
            PdfPTable clientTable = new PdfPTable(1);
            clientTable.setWidthPercentage(50);
            clientTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            clientTable.setSpacingAfter(20);

            PdfPCell clientCell = new PdfPCell();
            clientCell.setBorder(Rectangle.NO_BORDER);
            clientCell.addElement(new Paragraph("FACTURÉ À", headerFont));
            clientCell.addElement(new Paragraph(facture.getClient() != null && facture.getClient().getNomComplet() != null ? facture.getClient().getNomComplet() : "N/A", valueFont));
            Locataire locataire = (user != null) ? user.getLocataire() : null;
            clientCell.addElement(new Paragraph(
                    locataire != null && locataire.getAdresse() != null ? locataire.getAdresse() : "",
                    smallFont
            ));
            clientCell.addElement(new Paragraph(
                    locataire != null && locataire.getAdresse() != null ? locataire.getAdresse() : "",
                    smallFont
            ));
            clientCell.addElement(new Paragraph(facture.getClient() != null && facture.getClient().getEmail() != null ? facture.getClient().getEmail() : "", smallFont));
            clientCell.addElement(new Paragraph(facture.getClient() != null && facture.getClient().getTelephone() != null ? facture.getClient().getTelephone() : "", smallFont));
            clientTable.addCell(clientCell);

            document.add(clientTable);

            // Tableau des services
            PdfPTable serviceTable = new PdfPTable(5);
            serviceTable.setWidthPercentage(100);
            serviceTable.setWidths(new float[]{45, 13, 13, 14, 15});
            serviceTable.setSpacingBefore(10);
            serviceTable.setSpacingAfter(10);

            serviceTable.addCell(createTableHeaderCell("DESCRIPTION", headerFont));
            serviceTable.addCell(createTableHeaderCell("PRIX", headerFont));
            serviceTable.addCell(createTableHeaderCell("RÉDUCTION", headerFont));
            serviceTable.addCell(createTableHeaderCell("TOTAL HORS TVA", headerFont));
            serviceTable.addCell(createTableHeaderCell("MONTANT (EUR)", headerFont));

            String description = "N/A";
            SimpleDateFormat sdfService = new SimpleDateFormat("dd MMMM yyyy", new Locale("fr", "FR"));
            if (reservation != null && reservation.getVoiture() != null) {
                String marque = reservation.getVoiture().getMarque() != null ? reservation.getVoiture().getMarque() : "N/A";
                String modele = reservation.getVoiture().getModele() != null ? reservation.getVoiture().getModele() : "N/A";

                String dateDebut = "N/A";
                String dateFin = "N/A";

                if (reservation.getDateDebut() != null) {
                    Object d1 = reservation.getDateDebut();
                    if (d1 instanceof Date) {
                        dateDebut = sdfService.format((Date) d1);
                    } else if (d1 instanceof LocalDate) {
                        dateDebut = sdfService.format(Date.from(((LocalDate) d1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }
                }

                if (reservation.getDateFin() != null) {
                    Object d2 = reservation.getDateFin();
                    if (d2 instanceof Date) {
                        dateFin = sdfService.format((Date) d2);
                    } else if (d2 instanceof LocalDate) {
                        dateFin = sdfService.format(Date.from(((LocalDate) d2).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    }
                }

                description = String.format("Location de %s %s, du %s au %s", marque, modele, dateDebut, dateFin);
            }

            double discount = 0.0;
            double totalExclVat = prixTotal - discount;

            serviceTable.addCell(createTableCell(description, descriptionFont)); // Police plus petite
            serviceTable.addCell(createTableCell(String.format("%.2f €", prixTotal), valueFont));
            serviceTable.addCell(createTableCell(String.format("%.2f €", discount), valueFont));
            serviceTable.addCell(createTableCell(String.format("%.2f €", totalExclVat), valueFont));
            serviceTable.addCell(createTableCell(String.format("%.2f €", prixTotal), valueFont));

            // Ajout des adresses sous le tableau
            if (reservation != null) {
                PdfPTable addressTable = new PdfPTable(1);
                addressTable.setWidthPercentage(100);
                addressTable.setSpacingBefore(5);
                addressTable.setSpacingAfter(10);

                PdfPCell addressCell = new PdfPCell();
                addressCell.setBorder(Rectangle.NO_BORDER);
                addressCell.addElement(new Paragraph("Prise en charge : " + (reservation.getAdressePriseEnCharge() != null ? reservation.getAdressePriseEnCharge() : "N/A"), smallFont));
                addressCell.addElement(new Paragraph("Restitution : " + (reservation.getAdresseRestitution() != null ? reservation.getAdresseRestitution() : "N/A"), smallFont));
                addressTable.addCell(addressCell);

                document.add(addressTable);
            }

            document.add(serviceTable);

            // Totaux
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(50);
            totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.setSpacingBefore(10);

            totalTable.addCell(createTotalLabelCell("Total hors TVA", labelFont));
            totalTable.addCell(createTotalValueCell(String.format("%.2f €", totalExclVat), valueFont));
            totalTable.addCell(createTotalLabelCell("TVA", labelFont));
            totalTable.addCell(createTotalValueCell("0.00 €", valueFont));
            totalTable.addCell(createTotalLabelCell("Total", labelFont));
            totalTable.addCell(createTotalValueCell(String.format("%.2f €", prixTotal), valueFont));
            totalTable.addCell(createTotalLabelCell("Paiements", labelFont));
            totalTable.addCell(createTotalValueCell(String.format("%.2f €", prixTotal), valueFont));
            totalTable.addCell(createTotalLabelCell("Montant dû", labelFont));
            totalTable.addCell(createTotalValueCell("0.00 €", valueFont));

            document.add(totalTable);

            // Pied de page
            PdfPTable footerTable = new PdfPTable(1);
            footerTable.setWidthPercentage(100);
            footerTable.setSpacingBefore(30);

            PdfPCell footerCell = new PdfPCell(new Phrase("Merci pour votre confiance !", smallFont));
            footerCell.setBorder(Rectangle.NO_BORDER);
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell);

            document.add(footerTable);

            document.close();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new DocumentException("Erreur lors de la génération du PDF : " + e.getMessage());
        }

        return out.toByteArray();
    }

    private PdfPCell createTableHeaderCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(240, 240, 240));
        cell.setBorderColor(new BaseColor(224, 224, 224)); // Bordure plus claire
        cell.setBorderWidth(0.5f); // Bordure fine
        cell.setPadding(10); // Plus d'espacement
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPCell createTableCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderColor(new BaseColor(224, 224, 224));
        cell.setBorderWidth(0.5f);
        cell.setBorder(Rectangle.TOP | Rectangle.BOTTOM); // Bordures haut/bas uniquement
        cell.setPadding(10);
        cell.setLeading(0, 1.2f); // Espacement vertical du texte
        return cell;
    }

    private PdfPCell createTotalLabelCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }

    private PdfPCell createTotalValueCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }

	
}