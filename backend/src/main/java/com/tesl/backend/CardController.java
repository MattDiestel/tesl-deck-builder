package com.tesl.backend;

import java.io.File;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "https://frontend-gilt-kappa-25.vercel.app")
public class CardController {

    private static final String TESL_PATH = "/home/matt/TESL/TESL";
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // Returns all cards as JSON
    @GetMapping("/cards")
    public List<Card> getCards(@RequestParam(required = false) String color) {
        List<Card> cards = cardService.getAllCards();
        if (color != null && !color.isEmpty()) {
            return cards.stream()
                .filter(c -> c.getColors().contains(color))
                .toList();
        }
        return cards;
    }

    // Serves the actual image files
    @GetMapping(value = "/images/{color}/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getSingleColorImage(
            @PathVariable String color,
            @PathVariable String filename) {
        File file = new File(TESL_PATH + "/" + color + "/fronts/" + filename + ".png");
        if (!file.exists()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new FileSystemResource(file));
    }

    @GetMapping(value = "/images/Multi Color/{combo}/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getMultiColorImage(
            @PathVariable String combo,
            @PathVariable String filename) {
        File file = new File(TESL_PATH + "/Multi Color/" + combo + "/" + filename + ".png");
        if (!file.exists()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new FileSystemResource(file));
    }
}