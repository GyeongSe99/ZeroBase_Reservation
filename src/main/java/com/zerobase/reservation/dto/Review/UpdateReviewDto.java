package com.zerobase.reservation.dto.Review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UpdateReviewDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull
        private String memberId;

        @NotNull
        private Long storeId;

        @NotNull
        private Long reservationId;

        private Long ReviewId;

        @Min(0)
        @Max(5)
        private int star;

        private String reviewContext;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long storeId;
        private String memberId;
        private int star;
        private String reviewContext;
    }
}
