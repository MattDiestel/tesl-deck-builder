package com.tesl.backend;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.net.URL;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "https://frontend-gilt-kappa-25.vercel.app"})
public class PdfController {

    private final CardService cardService;

    public PdfController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody List<String> cardIds) {
        try {
            java.util.Map<String, Card> cardMap = new java.util.HashMap<>();
            for (Card c : cardService.getAllCards()) {
                cardMap.put(c.getId(), c);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(10, 10, 10, 10);

            float[] columnWidths = {1, 1, 1};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            int count = 0;
            for (String cardId : cardIds) {
                Card card = cardMap.get(cardId);
                if (card == null) continue;

                try {
                    byte[] imageBytes = new URL(card.getImagePath()).openStream().readAllBytes();
                    Image img = new Image(ImageDataFactory.create(imageBytes));
                    img.scaleToFit(180, 250);
                    img.setAutoScale(false);

                    Cell cell = new Cell().add(img);
                    cell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
                    cell.setPadding(3);
                    table.addCell(cell);
                    count++;
                } catch (Exception e) {
                    System.out.println("Skipping card: " + cardId + " — " + e.getMessage());
                }
            }

            // Fill remaining cells
            int remainder = count % 3;
            if (remainder != 0) {
                for (int i = 0; i < 3 - remainder; i++) {
                    Cell empty = new Cell();
                    empty.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
                    table.addCell(empty);
                }
            }

            document.add(table);
            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                ContentDisposition.attachment().filename("deck.pdf").build()
            );

            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}