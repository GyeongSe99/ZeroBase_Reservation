package com.zerobase.reservation.controller;

import com.zerobase.reservation.domain.Reservation;
import com.zerobase.reservation.dto.Reservation.AddReservationDto;
import com.zerobase.reservation.service.ReservationService;
import com.zerobase.reservation.type.CompletedStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{storeId}")
    @PreAuthorize("isAuthenticated()")
    public AddReservationDto.Response addReservation(@PathVariable Long storeId,
                               @RequestParam(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd E HH") LocalDateTime scheduledTime) {
        log.info("[addReservation] 예약 진행");
        String memberId = reservationService.getLoginId();

        AddReservationDto.Request request = AddReservationDto.Request.builder()
                                                                     .memberId(memberId)
                                                                     .storeId(storeId)
                                                                     .scheduledTime(scheduledTime)
                                                                     .build();

        return reservationService.addReservation(request);
    }

    @GetMapping("/show/{storeId}")
    @PreAuthorize("@securityService.isStoreOwner(#storeId, authentication)")
    public List<Reservation> showReservationListForOwner(@PathVariable Long storeId) {
        log.info("[showReservationList : For Owner]");
        List<Reservation> reservationList = reservationService.showReservationListForOwner(storeId);

        if (reservationList.size() == 0) {
            log.info("[showReservationList : For Owner] 해당 매장에 등록된 예약 정보가 없습니다.");
        }

        return reservationList;
    }
    
    // 예약 정보 확인 후 승인/보류/거절
    @GetMapping("/change/{storeId}/{reservationId}")
    @PreAuthorize("@securityService.isStoreOwner(#storeId, authentication)")
    public Reservation changeCompletedStatus(@PathVariable Long storeId,
                                      @PathVariable Long reservationId,
                                      @RequestParam(required = true, name = "completedStatus") CompletedStatus completedStatus) {
        log.info("예약 승인 및 거절 진행 : For Owner");

        return reservationService.changeCompletedStatus(storeId, reservationId, completedStatus);
    }

    @GetMapping("checkArrive/{storeId}")
    public void checkArrivedTime(@PathVariable Long storeId,
                                 @RequestParam(required = true, name = "memberId") String memberId) {
        log.info("예약자 도착 확인");
        Reservation checkArrivedTime = reservationService.checkArrivedTime(storeId, memberId);
    }




}
