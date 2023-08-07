package com.zerobase.reservation.service;

import com.zerobase.reservation.common.config.exception.ReservationException;
import com.zerobase.reservation.dto.KakaoMapDto;
import com.zerobase.reservation.type.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMapService {
    @Value("${KaKaoMap.RESTAPIKey}")
    private String apiKey;

    @Value("${KakaoMap.searchAddressUrl}")
    private String apiUrl;

    public KakaoMapDto.Response getGeocodingInfo(String searchInput) {
        RestTemplate restTemplate = new RestTemplate();

        String url = apiUrl + "?query=" + searchInput;

        // API 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        // API 요청
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<KakaoMapDto.Response> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, KakaoMapDto.Response.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            KakaoMapDto.Response response = responseEntity.getBody();
            if (response != null && response.getDocuments() != null && !response.getDocuments()
                                                                                .isEmpty()) {
                return response;
            }
        }

        return null;
    }

    public ResponseEntity<KakaoMapDto.AddressInfo> geocodingParsing(String search) {
        KakaoMapDto.Response response = getGeocodingInfo(search);
        if (response != null && response.getDocuments() != null && !response.getDocuments()
                                                                            .isEmpty()) {
            KakaoMapDto.Document document = response.getDocuments()
                                                    .get(0);

            String address = document.getAddress_name();
            String zipcode = document.getZip_code();
            double latitude = document.getLatitude();
            double longitude = document.getLongitude();
            return ResponseEntity.ok(KakaoMapDto.AddressInfo.builder()
                                                            .address(address)
                                                            .zipcode(zipcode)
                                                            .latitude(latitude)
                                                            .longitude(longitude)
                                                            .build());
        } else {
            throw new ReservationException(ErrorCode.ADDRESS_NOT_FOUND);
        }
    }
}
