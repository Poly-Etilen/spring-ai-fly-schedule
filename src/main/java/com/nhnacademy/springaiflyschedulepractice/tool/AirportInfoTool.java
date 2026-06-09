package com.nhnacademy.springaiflyschedulepractice.tool;

import com.nhnacademy.springaiflyschedulepractice.dto.airport.AirportInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AirportInfoTool {
    private final ApiClientService apiClientService;

    @Tool(
            description = "전체 공항 목록을 조회합니다. " +
                    "국내 모든 공항의 코드와 이름을 반환합니다."
    )
    public List<AirportInfoResponse> getAirportList() {
        log.info("Tool 호출: getAirportList()");
        return apiClientService.getAirportList();
    }

    @Tool(
            description = "공항 이름으로 공항 코드를 조회합니다. " +
                    "공항 이름을 입력하면 해당 공항의 IATA 코드를 반환합니다. " +
                    "지원하는 공항: 김포, 인천, 김해, 광주, 제주, 대구, 청주, 양양, 울산, 여수, 사천, 포항, 무안 등"
    )
    public String getAirportInfo(@ToolParam(description = "공항 이름 (예: 광주, 김포, 제주)") String airportName) {
//        log.info("Tool 호출: getAirportCode({})", airportName);
//        if (airportCodeCache == null) {
//            getAirportList();
//        }
//
//        String code = airportCodeCache.get(airportName);
//        if (code == null) {
//            return "알 수 없는 공항입니다.: " + airportName;
//        }
//        return code;
        return apiClientService.getAirportList()
                .stream()
                .filter(a -> a.getAirportName().equals(airportName))
                .map(AirportInfoResponse::getAirportId)
                .findFirst()
                .orElse("알 수 없는 공항: " + airportName);
    }
}
