package com.nhnacademy.springaiflyschedulepractice.service.util;

import com.nhnacademy.springaiflyschedulepractice.dto.FlightSearchParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ParameterNormalizer {
    public FlightSearchParam normalize(String message, FlightSearchParam params) {
        Map<String, Object> map = new HashMap<>();
        map.put("departure", params.departure());
        map.put("arrival", params.arrival());
        map.put("date", params.date());
        map.put("afterTime", params.afterTime());
        map.put("beforeTime", params.beforeTime());
        map.put("minPrice", params.minPrice());
        map.put("maxPrice", params.maxPrice());

        fillRoute(message, map);
        fillDate(message, map);
        fillTime(message, map);
        fillPrice(message, map);

        Object dateObj = map.get("date");
        if (dateObj instanceof String dateText && !isDateExpression(dateText)) {
            if (!map.containsKey("afterTime") && dateText.contains("이후")) {
                map.put("afterTime", dateText);
            } else if (!map.containsKey("beforeTime") && (dateText.contains("이전") || dateText.contains("전") || dateText.contains("까지"))) {
                map.put("beforeTime", dateText);
            }
            map.put("date", "내일");
        }
        log.info("정규화된 파라미터 (Map 상태): {}", map);

        return new FlightSearchParam(
                (String) map.get("departure"),
                (String) map.get("arrival"),
                (String) map.get("date"),
                (String) map.get("afterTime"),
                (String) map.get("beforeTime"),
                parseInteger(map.get("minPrice")),
                parseInteger(map.get("maxPrice"))
        );
    }

    public boolean hasText(Object value) {
        return value instanceof String text && !text.isBlank() && !"null".equalsIgnoreCase(text);
    }

    private void fillRoute(String message, Map<String, Object> params) {
        if (hasText(params.get("departure")) && hasText(params.get("arrival"))) {
            return;
        }

        Matcher matcher = Pattern.compile("([가-힣]+)\\s*(?:-|에서)\\s*([가-힣]+)").matcher(message);
        if (matcher.find()) {
            params.putIfAbsent("departure", extractAirportName(matcher.group(1)));
            params.putIfAbsent("arrival", extractAirportName(matcher.group(2)));
        }
    }

    private String extractAirportName(String value) {
        for (String airport : List.of("광주", "제주", "김포", "인천", "김해", "부산", "대구", "청주")) {
            if (value.contains(airport)) {
                return airport;
            }
        }
        return value;
    }

    private void fillDate(String message, Map<String, Object> params) {
        if (hasText(params.get("date"))) {
            return;
        }

        if (message.contains("내일")) {
            params.put("date", "내일");
        } else if (message.contains("모레")) {
            params.put("date", "모레");
        }
    }

    private void fillTime(String message, Map<String, Object> params) {
        if (hasText(params.get("afterTime")) || hasText(params.get("beforeTime"))) {
            return;
        }

        Matcher matcher = Pattern.compile("((?:오전|오후)?\\s*\\d{1,2}(?::\\d{2})?\\s*시?)\\s*(이후|이전|전|까지)").matcher(message);
        if (matcher.find()) {
            if ("이후".equals(matcher.group(2))) {
                params.put("afterTime", matcher.group(1));
            } else {
                params.put("beforeTime", matcher.group(1));
            }
        }
    }

    private void fillPrice(String message, Map<String, Object> params) {
        Matcher rangeMatcher = Pattern.compile("(\\d+)\\s*만원\\s*(?:에서|부터|~|-)\\s*(\\d+)\\s*만원").matcher(message);
        if (rangeMatcher.find()) {
            params.putIfAbsent("minPrice", String.valueOf(Integer.parseInt(rangeMatcher.group(1)) * 10000));
            params.putIfAbsent("maxPrice", String.valueOf(Integer.parseInt(rangeMatcher.group(2)) * 10000));
            return;
        }

        Matcher maxMatcher = Pattern.compile("(\\d+)\\s*만원\\s*(?:이하|까지|아래)").matcher(message);
        if (maxMatcher.find()) {
            params.putIfAbsent("maxPrice", String.valueOf(Integer.parseInt(maxMatcher.group(1)) * 10000));
        }
    }

    private boolean isDateExpression(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String normalized = value.trim();
        return normalized.equals("오늘")
                || normalized.equals("내일")
                || normalized.equals("모레")
                || normalized.equals("내일모레")
                || normalized.matches("\\d{8}")
                || normalized.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    public Integer parseInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer i) return i;
        try {
            String normalized = value.toString().replaceAll("[^0-9]", "");
            return normalized.isBlank() ? null : Integer.parseInt(normalized);
        } catch (Exception e) {
            return null;
        }
    }
}
