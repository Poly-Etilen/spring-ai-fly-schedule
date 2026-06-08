package com.nhnacademy.springaiflyschedulepractice.tool;

import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirlineInfoTool {
    private final ApiClientService apiClientService;
    private Map<String, String> airlineIdCache;

    @Tool(
            description = "전체 항공사 목록을 조회합니다. " +
                    "국내 모든 항공사의 코드와 이름을 반환합니다."
    )
    public List<AirlineInfoResponse> getAirlineList() {
        log.info("Tool 호출: getAirlineList()");
        List<AirlineInfoResponse> airlines = apiClientService.getAirlineList();

        airlineIdCache = airlines.stream()
                .collect(Collectors.toMap(AirlineInfoResponse::getAirlineNm, AirlineInfoResponse::getAirlineId));
        return airlines;
    }

    @Tool(
            description = "항공사 이름으로 항공사 ID를 조회합니다. " +
                    "항공사 이름을 입력하면 해당 항공사의 IATA 코드를 반환합니다. " +
                    "지원하는 항공사: 대한한공, 아시아나항공, 제주항공, 에어부산, 에어서울, 진에어, 티웨이항공 등"
    )
    public String getAirlineId(@ToolParam(description = "항공사 이름 (예: 아시아나항공, 대한항공, 제주항공") String airlineName) {
        log.info("Tool 호출: getAirlineId({})", airlineName);

        if (airlineIdCache == null) {
            getAirlineList();
        }
        String id = airlineIdCache.get(airlineName);
        if (id == null) {
            return "알 수 없는 항공사입니다: " + airlineName;
        }
        return id;
    }
}
