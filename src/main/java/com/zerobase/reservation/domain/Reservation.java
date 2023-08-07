package com.zerobase.reservation.domain;

import com.zerobase.reservation.type.ArrivedStatus;
import com.zerobase.reservation.type.CompletedStatus;
import com.zerobase.reservation.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private LocalDateTime bookingTime;  // 예약한 시간
    private LocalDateTime scheduledTime; // 예약된 시간
    private ReservationStatus reservationStatus;    // 예약 승인 여부
    private CompletedStatus completedStatus;    // 예약 서비스 사용 여부
    private ArrivedStatus arrivedStatus;    // 도착 여부

    @ManyToOne
    private Store store;

    @ManyToOne
    private Member member;

}
