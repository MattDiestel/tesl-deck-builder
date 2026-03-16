package com.tesl.backend;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PdfController {

    private final CardService cardService;

    public PdfController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(@RequestBody List<String> cardIds) {
        try {
            // Build a lookup map of all cards
            java.util.Map<String, Card> cardMap = new java.util.HashMap<>();
            for (Card c : cardService.getAllCards()) {
                cardMap.put(c.getId(), c);
            }

            // A4 page, 3 cards per row
            Document document = new Document(PageSize.A4, 10, 10, 10, 10);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);

            int count = 0;
            for (String cardId : cardIds) {
                Card card = cardMap.get(cardId);
                if (card == null) continue;

                try {
                    // Fetch image from Cloudinary URL
                    String imageUrl = card.getImagePath();
                    Image img = Image.getInstance(new URL(imageUrl));
                    img.scaleToFit(180, 250);

                    PdfPCell cell = new PdfPCell(img, true);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(3);
                    table.addCell(cell);
                    count++;
                } catch (Exception e) {
                    // Skip cards that fail to load
                    System.out.println("Skipping card: " + cardId + " — " + e.getMessage());
                }
            }

            // Fill remaining cells in last row
            int remainder = count % 3;
            if (remainder != 0) {
                for (int i = 0; i < 3 - remainder; i++) {
                    PdfPCell empty = new PdfPCell();
                    empty.setBorder(Rectangle.NO_BORDER);
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
            return ResponseEntity.internalServerError().build();
        }
    }
}