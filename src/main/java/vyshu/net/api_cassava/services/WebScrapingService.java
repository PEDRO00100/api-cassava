package vyshu.net.api_cassava.services;

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
import java.util.logging.Logger;

@Service
public class WebScrapingService {

    private static final String BASE_URL = "https://www.cassavaroots.com/mx/menu";
    private static final Logger logger = Logger.getLogger(WebScrapingService.class.getName());

    // Clase para los detalles de los sabores
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

    // Método para obtener el documento HTML
    private Document fetchDocument() {
        try {
            return Jsoup.connect(BASE_URL)
                    .timeout(10000) // Timeout de 10 segundos
                    .get();
        } catch (IOException e) {
            logger.severe("Error al obtener el documento: " + e.getMessage());
            return null;
        }
    }

    // Método para raspar las listas simples (como tamaños, temperaturas, etc.)
    private List<String> scrapeItems(String selector) {
        Document doc = fetchDocument();
        if (doc == null) {
            logger.warning("No se pudo obtener el documento para el selector: " + selector);
            return new ArrayList<>();
        }

        List<String> items = new ArrayList<>();
        Elements elements = doc.select(selector);
        for (Element element : elements) {
            String item = element.select(".customization_item-title").text();
            if (!item.isEmpty()) {
                items.add(item);
            }
        }

        if (items.isEmpty()) {
            logger.info("No se encontraron elementos para el selector: " + selector);
        }

        return items;
    }

    // Método para obtener los sabores (Flavours)
    public Map<String, Map<String, FlavourDetails>> getCassavaFlavours() {
        Document doc = fetchDocument();
        if (doc == null) {
            return new HashMap<>();
        }

        Map<String, Map<String, FlavourDetails>> flavourMap = new HashMap<>();
        Elements tabs = doc.select("div[data-w-tab]");

        if (tabs.isEmpty()) {
            logger.warning("No se encontraron pestañas de sabores.");
        }

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

        return flavourMap;
    }

    // Método para obtener los tamaños (Sizes)
    public List<String> getSizes() {
        return scrapeItems("#w-node-_52b11fcf-8f2c-7475-86b0-d5826de74b35-061a8847 .customization_item");
    }

    // Método para obtener las temperaturas (Temperature)
    public List<String> getTemperature() {
        Document doc = fetchDocument();
        List<String> modalities = new ArrayList<>();
        if (doc == null) {
            return modalities;
        }

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

        if (modalities.isEmpty()) {
            logger.info("No se encontraron modalidades de temperatura.");
        }

        return modalities;
    }

    // Método para obtener los tipos de leche (Milk Types)
    public List<String> getMilkTypes() {
        return scrapeItems(
                "#w-node-_00f0097f-f142-a5d7-5072-5a3ab54bd27d-061a8847 .customization_vertical-item");
    }

    // Método para obtener las coberturas (Toppings)
    public List<String> getToppings() {
        return scrapeItems(
                "#w-node-f0861b3a-bbe7-abe5-3f11-97e6b5909d74-061a8847 .customization_vertical-item");
    }
}
