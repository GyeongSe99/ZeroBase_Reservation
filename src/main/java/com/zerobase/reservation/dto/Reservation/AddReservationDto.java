package com.zerobase.reservation.dto.Reservation;

import com.zerobase.reservation.domain.Reservation;
import com.zerobase.reservation.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AddReservationDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String memberId;
        private Long storeId;
        private LocalDateTime scheduledTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long storeId;
        private LocalDateTime scheduledTime;
        private LocalDateTime bookingTime;
        private ReservationStatus reservationStatus;

        public static AddReservationDto.Response reservationToResponse(Reservation reservation) {
            return Response.builder()
                           .storeId(reservation.getStore()
                                               .getStoreId())
                           .scheduledTime(reservation.getScheduledTime())
                           .bookingTime(reservation.getBookingTime())
                           .reservationStatus(reservation.getReservationStatus())
                           .build();
        }
    }
}
