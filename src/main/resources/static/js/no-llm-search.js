// 페이지 로드 시 오늘 날짜로 기본 세팅해주는 편의 기능
document.addEventListener("DOMContentLoaded", function() {
    const dateInput = document.getElementById('date');
    if (dateInput) {
        dateInput.value = new Date().toISOString().substring(0, 10);
    }
});

async function performSearch(event) {
    event.preventDefault(); // 폼 제출 시 페이지 새로고침 방지

    // HTML 요소에서 값 읽어오기
    const departure = document.getElementById('departureAirport').value;
    const arrival = document.getElementById('arrivalAirport').value;
    const date = document.getElementById('date').value;
    const afterTime = document.getElementById('afterTime').value;
    const minPrice = document.getElementById('minPrice').value;
    const maxPrice = document.getElementById('maxPrice').value;

    // 로딩 바 활성화 및 이전 결과 비우기
    const loading = document.getElementById('loading');
    const resultsBody = document.getElementById('resultsBody');
    loading.style.display = 'block';
    resultsBody.innerHTML = '';

    // 동적 쿼리 파라미터 생성 (선택 사항은 값이 있을 때만 추가)
    let url = `/api/no-llm/flights?departureAirport=${encodeURIComponent(departure)}&arrivalAirport=${encodeURIComponent(arrival)}&date=${date}`;
    if (afterTime) url += `&afterTime=${afterTime}`;
    if (minPrice) url += `&minPrice=${minPrice}`;
    if (maxPrice) url += `&maxPrice=${maxPrice}`;

    try {
        // 우리가 만든 순수 백엔드 컨트롤러 API 호출
        const response = await fetch(url);
        if (!response.ok) throw new Error('서버 에러 발생');

        const flights = await response.json(); // List<FlightSearchResponse> 수신
        loading.style.display = 'none';

        if (flights.length === 0) {
            resultsBody.innerHTML = `<tr><td colspan="5" class="no-data">조건에 맞는 항공편이 없습니다.</td></tr>`;
            return;
        }

        // 받아온 DTO 리스트를 순회하며 테이블 행(row) 생성
        flights.forEach(flight => {
            const tr = document.createElement('tr');

            // 시간 포맷 가공 (API 원본 raw 스트링 "202606081430" -> "14:30" 형태로 보기 좋게 표기)
            const depTimeFormatted = formatTime(flight.departureTime);
            const arrTimeFormatted = formatTime(flight.arrivalTime);

            // 가격 포맷 가공 (예: 64000 -> 64,000원)
            const priceFormatted = flight.price
                ? Number(flight.price).toLocaleString() + '원'
                : '가격 미상';

            tr.innerHTML = `
                    <td><strong>${flight.flightId}</strong></td>
                    <td>${flight.airlineName}</td>
                    <td>${depTimeFormatted}</td>
                    <td>${arrTimeFormatted}</td>
                    <td><span style="color: #007bff; font-weight: bold;">${priceFormatted}</span></td>
                `;
            resultsBody.appendChild(tr);
        });

    } catch (error) {
        loading.style.display = 'none';
        resultsBody.innerHTML = `<tr><td colspan="5" class="no-data" style="color: red;">❌ 통신 실패: ${error.message}</td></tr>`;
    }
}

// "202606081430" 형식을 "14:30"으로 잘라주는 간단한 자바스크립트 함수
function formatTime(timeStr) {
    if (timeStr && timeStr.length >= 12) {
        return `${timeStr.substring(8, 10)}:${timeStr.substring(10, 12)}`;
    }
    return timeStr;
}

// === 공항 & 항공사 정보 조회 기능 ===

// 전체 공항 목록
async function fetchAirports() {
    const box = document.getElementById('airportResultBox');
    box.style.display = 'block';
    box.innerHTML = '불러오는 중... ⏳';
    try {
        const res = await fetch('/api/no-llm/info/airports');
        const data = await res.json();
        box.innerHTML = data.map(a => `<div>✔ <strong>${a.airportNm}</strong> : ${a.airportId}</div>`).join('');
    } catch (e) {
        box.innerHTML = '<span style="color:red">불러오기 실패</span>';
    }
}

// 공항 코드 검색
async function searchAirportCode() {
    const name = document.getElementById('airportLookupInput').value;
    if(!name) return alert('공항 이름을 입력해주세요.');

    const box = document.getElementById('airportResultBox');
    box.style.display = 'block';
    box.innerHTML = '조회 중... ⏳';
    try {
        const res = await fetch(`/api/no-llm/info/airports/code?name=${encodeURIComponent(name)}`);
        const code = await res.text();
        box.innerHTML = `<strong>${name}</strong> 공항 코드: <span style="color:#007bff; font-weight:bold;">${code}</span>`;
    } catch (e) {
        box.innerHTML = '<span style="color:red">조회 실패</span>';
    }
}

// 전체 항공사 목록
async function fetchAirlines() {
    const box = document.getElementById('airlineResultBox');
    box.style.display = 'block';
    box.innerHTML = '불러오는 중... ⏳';
    try {
        const res = await fetch('/api/no-llm/info/airlines');
        const data = await res.json();
        box.innerHTML = data.map(a => `<div>✔ <strong>${a.airlineNm}</strong> : ${a.airlineId}</div>`).join('');
    } catch (e) {
        box.innerHTML = '<span style="color:red">불러오기 실패</span>';
    }
}

// 항공사 ID 검색
async function searchAirlineCode() {
    const name = document.getElementById('airlineLookupInput').value;
    if(!name) return alert('항공사 이름을 입력해주세요.');

    const box = document.getElementById('airlineResultBox');
    box.style.display = 'block';
    box.innerHTML = '조회 중... ⏳';
    try {
        const res = await fetch(`/api/no-llm/info/airlines/code?name=${encodeURIComponent(name)}`);
        const code = await res.text();
        box.innerHTML = `<strong>${name}</strong> ID: <span style="color:#007bff; font-weight:bold;">${code}</span>`;
    } catch (e) {
        box.innerHTML = '<span style="color:red">조회 실패</span>';
    }
}