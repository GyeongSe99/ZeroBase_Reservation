package com.zerobase.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import java.util.List;

public class KakaoMapDto {
    @Data
    public static class Response {
        private List<Document> documents;
    }

    @Data
    public static class Document {
        private String address_name;
        private String zip_code;
        private double latitude;
        private double longitude;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class AddressInfo {
        private String address;
        private String zipcode;
        private double latitude;
        private double longitude;
    }
}
