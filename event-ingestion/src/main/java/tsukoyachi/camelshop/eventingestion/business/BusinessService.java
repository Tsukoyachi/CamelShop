package tsukoyachi.camelshop.eventingestion.business;

import org.springframework.stereotype.Service;

@Service
public class BusinessService {
    public void processMessage(String message) {
        System.out.println("Processing message: " + message);
        // TODO: Add business logic and persistance layer
    }
}

