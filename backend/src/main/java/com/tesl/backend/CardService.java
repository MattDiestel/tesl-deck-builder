package com.tesl.backend;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CardService {

    private static final String CLOUDINARY_BASE = "https://res.cloudinary.com/dmwxzhylv/image/upload/TESL";
    private static final String[] SINGLE_COLORS = {"Blue", "Green", "Red", "Yellow", "Purple", "Neutral"};
    private static final String[] MULTI_COLORS = {
        "Blue_Green", "Blue_Purple", "Blue_Yellow", "Blue_Yellow_Green",
        "Blue_Yellow_Purple", "Blue_Green_Purple", "Red_Blue", "Red_Blue_Green",
        "Red_Green", "Red_Green_Purple", "Red_Yellow", "Red_Yellow_Green",
        "Red_Yellow_Purple", "Yellow_Green", "Yellow_Green_Purple", "Yellow_Purple"
    };

    private final Cloudinary cloudinary;

    public CardService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
            "api_key", System.getenv("CLOUDINARY_API_KEY"),
            "api_secret", System.getenv("CLOUDINARY_API_SECRET")
        ));
    }

    public List<Card> getAllCards() {
        List<Card> cards = new ArrayList<>();

        // Single colors
        for (String color : SINGLE_COLORS) {
            try {
                Map result = cloudinary.api().resources(ObjectUtils.asMap(
                    "type", "upload",
                    "prefix", "TESL/" + color + "/fronts/",
                    "max_results", 500
                ));
                List<Map> resources = (List<Map>) result.get("resources");
                if (resources != null) {
                    for (Map resource : resources) {
                        String publicId = (String) resource.get("public_id");
                        String filename = publicId.substring(publicId.lastIndexOf("/") + 1);
                        String id = color + "_" + filename;
                        String imageUrl = CLOUDINARY_BASE + "/" + color + "/fronts/" + filename;
                        cards.add(new Card(id, imageUrl, List.of(color)));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error fetching " + color + ": " + e.getMessage());
            }
        }

        // Multi colors
        for (String combo : MULTI_COLORS) {
            try {
                Map result = cloudinary.api().resources(ObjectUtils.asMap(
                    "type", "upload",
                    "prefix", "TESL/Multi Color/" + combo + "/",
                    "max_results", 500
                ));
                List<Map> resources = (List<Map>) result.get("resources");
                if (resources != null) {
                    List<String> colors = Arrays.asList(combo.split("_"));
                    for (Map resource : resources) {
                        String publicId = (String) resource.get("public_id");
                        String filename = publicId.substring(publicId.lastIndexOf("/") + 1);
                        String id = combo + "_" + filename;
                        String imageUrl = CLOUDINARY_BASE + "/Multi Color/" + combo + "/" + filename;
                        cards.add(new Card(id, imageUrl, colors));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error fetching " + combo + ": " + e.getMessage());
            }
        }

        return cards;
    }
}