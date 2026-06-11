package com.nhnacademy.springaiflyschedulepractice.tool;

import com.nhnacademy.springaiflyschedulepractice.dto.airline.AirlineInfoResponse;
import com.nhnacademy.springaiflyschedulepractice.service.ApiClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AirlineInfoTool implements AiTool{
    private final ApiClientService apiClientService;

    @Tool(
            description = "전체 항공사 목록을 조회합니다. " +
                    "국내 모든 항공사의 코드와 이름을 반환합니다."
    )
    public List<AirlineInfoResponse> getAirlineList() {
        return apiClientService.getAirlineList();
    }

    @Tool(
            description = "항공사 이름으로 항공사 ID를 조회합니다. " +
                    "항공사 이름을 입력하면 해당 항공사의 IATA 코드를 반환합니다. " +
                    "지원하는 항공사: 대한항공, 아시아나항공, 제주항공, 에어부산, 에어서울, 진에어, 티웨이항공 등"
    )
    public String getAirlineId(@ToolParam(description = "항공사 이름 (예: 아시아나항공, 대한항공, 제주항공") String airlineName) {
        return apiClientService.getAirlineList()
                .stream()
                .filter(airline -> airline.getAirlineNm().equals(airlineName))
                .map(AirlineInfoResponse::getAirlineId)
                .findFirst()
                .orElse("알 수 없는 항공사: " + airlineName);
    }
}
