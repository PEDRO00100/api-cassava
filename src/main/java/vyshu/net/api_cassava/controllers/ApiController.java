package vyshu.net.api_cassava.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vyshu.net.api_cassava.services.WebScrapingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final WebScrapingService webScrapingService;

    public ApiController(WebScrapingService webScrapingService) {
        this.webScrapingService = webScrapingService;
    }

    @GetMapping("/flavour")
    public ResponseEntity<Map<String, Map<String, Map<String, WebScrapingService.FlavourDetails>>>> getCassavaFlavours() {
        try {
            Map<String, Map<String, WebScrapingService.FlavourDetails>> flavours = webScrapingService
                    .getCassavaFlavours();
            Map<String, Map<String, Map<String, WebScrapingService.FlavourDetails>>> response = new HashMap<>();
            response.put("flavours", flavours);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/size")
    public ResponseEntity<Map<String, List<String>>> getSizes() {
        return handleServiceCall(() -> {
            List<String> sizes = webScrapingService.getSizes();
            return Map.of("sizes", sizes);
        });
    }

    @GetMapping("/temperature")
    public ResponseEntity<Map<String, List<String>>> getTemperature() {
        return handleServiceCall(() -> {
            List<String> temperature = webScrapingService.getTemperature();
            return Map.of("temperatures", temperature);
        });
    }

    @GetMapping("/milk")
    public ResponseEntity<Map<String, List<String>>> getMilkTypes() {
        return handleServiceCall(() -> {
            List<String> milkTypes = webScrapingService.getMilkTypes();
            return Map.of("milks", milkTypes);
        });
    }

    @GetMapping("/topping")
    public ResponseEntity<Map<String, List<String>>> getToppings() {
        return handleServiceCall(() -> {
            List<String> toppings = webScrapingService.getToppings();
            return Map.of("toppings", toppings);
        });
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