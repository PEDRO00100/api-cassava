package vyshu.net.api_cassava.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vyshu.net.api_cassava.services.WebScrapingService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MainController {

    private final WebScrapingService webScrapingService;

    public MainController(WebScrapingService webScrapingService) {
        this.webScrapingService = webScrapingService;
    }

    @GetMapping("/flavours")
    public ResponseEntity<Map<String, Map<String, WebScrapingService.FlavourDetails>>> getCassavaFlavours() {
        try {
            String jsonResponse = webScrapingService.getCassavaFlavours();
            ObjectMapper objectMapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Map<String, WebScrapingService.FlavourDetails>> flavourData = objectMapper
                    .readValue(jsonResponse, Map.class);
            return new ResponseEntity<>(flavourData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sizes")
    public ResponseEntity<List<String>> getSizes() {
        return handleServiceCall(() -> webScrapingService.getSizes());
    }

    @GetMapping("/temperature")
    public ResponseEntity<List<String>> getTemperature() {
        return handleServiceCall(() -> webScrapingService.getTemperature());
    }

    @GetMapping("/milk-types")
    public ResponseEntity<List<String>> getMilkTypes() {
        return handleServiceCall(() -> webScrapingService.getMilkTypes());
    }

    @GetMapping("/toppings")
    public ResponseEntity<List<String>> getToppings() {
        return handleServiceCall(() -> webScrapingService.getToppings());
    }

    private <T> ResponseEntity<T> handleServiceCall(ServiceCall<T> serviceCall) {
        try {
            return new ResponseEntity<>(serviceCall.execute(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @FunctionalInterface
    private interface ServiceCall<T> {
        T execute() throws Exception;
    }
}