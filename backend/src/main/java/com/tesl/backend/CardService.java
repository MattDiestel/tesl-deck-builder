package com.tesl.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CardService {

    private static final String TESL_PATH = "/home/matt/TESL/TESL-web";
    private static final String CLOUDINARY_BASE = "https://res.cloudinary.com/dmwxzhylv/image/upload/TESL";

    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<>();

        // Handle single color folders
        String[] singleColors = {"Blue", "Green", "Red", "Yellow", "Purple", "Neutral"};
        for (String color : singleColors) {
            File frontsFolder = new File(TESL_PATH + "/" + color + "/fronts");
            if (frontsFolder.exists()) {
                File[] images = frontsFolder.listFiles((dir, name) -> name.endsWith(".png"));
                if (images != null) {
                    for (File image : images) {
                        String filename = image.getName().replace(".png", "");
                        String id = color + "_" + filename;
                        String imageUrl = CLOUDINARY_BASE + "/" + color + "/fronts/" + filename;
                        cards.add(new Card(id, imageUrl, List.of(color)));
                    }
                }
            }
        }

        // Handle multi color folders
        File multiColorFolder = new File(TESL_PATH + "/Multi Color");
        if (multiColorFolder.exists()) {
            File[] colorCombos = multiColorFolder.listFiles(File::isDirectory);
            if (colorCombos != null) {
                for (File combo : colorCombos) {
                    String comboName = combo.getName();
                    List<String> colors = Arrays.asList(comboName.split("_"));
                    File[] images = combo.listFiles((dir, name) -> name.endsWith(".png"));
                    if (images != null) {
                        for (File image : images) {
                            String filename = image.getName().replace(".png", "");
                            String id = comboName + "_" + filename;
                            String imageUrl = CLOUDINARY_BASE + "/Multi Color/" + comboName + "/" + filename;
                            cards.add(new Card(id, imageUrl, colors));
                        }
                    }
                }
            }
        }

        return cards;
    }
}