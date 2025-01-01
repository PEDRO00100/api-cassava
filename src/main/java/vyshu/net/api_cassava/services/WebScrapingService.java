package vyshu.net.api_cassava.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebScrapingService {

    private static final String BASE_URL = "https://www.cassavaroots.com/mx/menu";

    public static class FlavourDetails {
        private String description;
        private String imageURL;

        public FlavourDetails(String description, String imageURL) {
            this.description = description;
            this.imageURL = imageURL;
        }

        public String getDescription() {
            return description;
        }

        public String getImageURL() {
            return imageURL;
        }
    }

    private Document fetchDocument() {
        try {
            return Jsoup.connect(BASE_URL).get();
        } catch (IOException e) {
            return null;
        }
    }

    private List<String> scrapeItems(String selector) {
        Document doc = fetchDocument();
        if (doc == null)
            return new ArrayList<>();

        List<String> items = new ArrayList<>();
        Elements elements = doc.select(selector);
        for (Element element : elements) {
            String item = element.select(".customization_item-title").text();
            if (!item.isEmpty()) {
                items.add(item);
            }
        }
        return items;
    }

    public String getCassavaFlavours() {
        Document doc = fetchDocument();
        if (doc == null)
            return "{}";

        Map<String, Map<String, FlavourDetails>> flavourMap = new HashMap<>();
        Elements tabs = doc.select("div[data-w-tab]");

        for (Element tab : tabs) {
            String tabName = tab.attr("data-w-tab");
            Map<String, FlavourDetails> flavours = new HashMap<>();

            Elements flavourItems = tab.select(".flavour-list_item");
            for (Element item : flavourItems) {
                String flavourName = item.select(".flavour-list_item-title").text();
                String description = item.select(".flavour-list_description").text();
                String imageURL = item.select(".flavour-list_item-image").attr("src");

                flavours.put(flavourName, new FlavourDetails(description, imageURL));
            }

            flavourMap.put(tabName, flavours);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(flavourMap);
        } catch (IOException e) {
            return "{}";
        }
    }

    public List<String> getSizes() {
        return scrapeItems("#w-node-_52b11fcf-8f2c-7475-86b0-d5826de74b35-061a8847 .customization_item");
    }

    public List<String> getTemperature() {
        Document doc = fetchDocument();
        if (doc == null)
            return new ArrayList<>();

        List<String> modalities = new ArrayList<>();
        Element modalitySection = doc.selectFirst(".customization_group:has(h3:containsOwn(Selecciona la modalidad))");

        if (modalitySection != null) {
            Elements modalityElements = modalitySection.select(".customization_item h4.customization_item-title");
            for (Element modalityElement : modalityElements) {
                String modalityName = modalityElement.text();
                if (!modalityName.isEmpty()) {
                    modalities.add(modalityName);
                }
            }
        }

        return modalities;
    }

    public List<String> getMilkTypes() {
        return scrapeItems("#w-node-_00f0097f-f142-a5d7-5072-5a3ab54bd27d-061a8847 .customization_vertical-item");
    }

    public List<String> getToppings() {
        return scrapeItems("#w-node-f0861b3a-bbe7-abe5-3f11-97e6b5909d74-061a8847 .customization_vertical-item");
    }
}