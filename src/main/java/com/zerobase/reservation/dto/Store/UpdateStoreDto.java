package com.zerobase.reservation.dto.Store;

import com.zerobase.reservation.domain.Store;
import com.zerobase.reservation.dto.Store.AddStoreDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UpdateStoreDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request{

        private String storeName;
        private String address;
        private double latitude;
        private double longitude;
        private String storeDescription;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long storeId;
        private String memberId;
        private String storeName;
        private String address;
        private double latitude;
        private double longitude;
        private String storeDescription;

        public static AddStoreDto.Response storeToResponse(Store store) {
            return AddStoreDto.Response.builder()
                                       .storeId(store.getStoreId())
                                       .memberId(store.getMember().getMemberId())
                                       .storeName(store.getStoreName())
                                       .address(store.getAddress())
                                       .latitude(store.getLatitude())
                                       .longitude(store.getLongitude())
                                       .storeDescription(store.getStoreDescription())
                                       .build();
        }
    }
}
