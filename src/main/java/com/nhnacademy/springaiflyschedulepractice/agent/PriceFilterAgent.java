package com.nhnacademy.springaiflyschedulepractice.agent;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightInfoResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PriceFilterAgent {

    public List<FlightInfoResponse> filterByPriceRange(
            List<FlightInfoResponse> flights,
            Integer minPrice,
            Integer maxPrice){

        if(flights == null || flights.isEmpty()){
            return List.of();
        }

        return flights.stream()
                .filter( flight -> {
                    Optional<Integer> parsedPrice = parsePrice(flight.getEconomyCharge());
                    if(parsedPrice.isEmpty())
                        return false;

                    Integer price = parsedPrice.get();

                    if(minPrice != null && price < minPrice)
                        return false;

                    return maxPrice == null || price <= maxPrice;

                }).collect(Collectors.toList());
    }

    private Optional<Integer> parsePrice(String price) {
        if (price == null || price.isBlank()) {
            return Optional.empty();
        }

        try {
            String normalized = price.replaceAll("[^0-9]", "");
            if (normalized.isBlank()) {
                return Optional.empty();
            }

            int parsed = Integer.parseInt(normalized);
            return parsed > 0 ? Optional.of(parsed) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
