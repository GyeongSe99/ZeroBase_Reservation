package com.zerobase.reservation.service;

import com.zerobase.reservation.common.config.exception.ReservationException;
import com.zerobase.reservation.domain.Reservation;
import com.zerobase.reservation.dto.Reservation.AddReservationDto;
import com.zerobase.reservation.repository.ReservationRepository;
import com.zerobase.reservation.repository.StoreRepository;
import com.zerobase.reservation.type.ArrivedStatus;
import com.zerobase.reservation.type.CompletedStatus;
import com.zerobase.reservation.type.ErrorCode;
import com.zerobase.reservation.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public AddReservationDto.Response addReservation(AddReservationDto.Request request) {
        log.info("[addReservation] 예약 정보 등록 중");
        try {
            Reservation newReservation = Reservation.builder()
                                                    .store(storeRepository.findById(request.getStoreId())
                                                                          .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_STORE)))
                                                    .scheduledTime(request.getScheduledTime())
                                                    .bookingTime(LocalDateTime.now())
                                                    .reservationStatus(ReservationStatus.HOLD)
                                                    .completedStatus(CompletedStatus.HOLD)
                                                    .arrivedStatus(ArrivedStatus.NOT_ARRIVED)
                                                    .build();

            Reservation savedReservation = reservationRepository.save(newReservation);
            if (Objects.equals(savedReservation.getStore()
                                               .getStoreId(), request.getStoreId())) {
                log.info("[addReservation] 예약 정보 등록 성공");
                return AddReservationDto.Response.reservationToResponse(savedReservation);
            } else {
                throw new RuntimeException("[addReservation] 예약 정보 등록 실패: 입력받은 매장아이디와 저장된 객체의 매장아이디 불일치 ");
            }
        } catch (Exception e) {
            log.error("[addReservation] 예약 정보 등록 실패: " + e.getMessage());
            throw new RuntimeException("예약 정보 등록 실패");
        }
    }

    public List<Reservation> showReservationListForOwner(Long storeId) {
        log.info("[showReservationListForOwner]");
        List<Reservation> reservationList = reservationRepository.findAllByStoreStoreIdOrderByScheduledTimeAsc(storeId);

        if (reservationList == null || reservationList.isEmpty()) {
            log.error("[showReservationListForOwner] ");
            return reservationList = new ArrayList<>();
        }

        return reservationList;
    }

    public Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                                    .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_RESERVATION));
    }

    public String getLoginId() {
        log.info("[get LoginId] 현재 로그인되어있는 아이디 정보 가져오기");
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        String memberId = authentication.getName();

        log.info("[get LoginId] 현재 로그인 되어있는 아이디 : {}", memberId);
        if (memberId == null) return "";

        return memberId;
    }

    @Transactional
    public Reservation changeCompletedStatus(Long storeId, Long reservationId, CompletedStatus completedStatus) {
        log.info("[checkCompletedStatus] 예약 확인 후 승인 및 거절");

        Reservation reservation =
                reservationRepository.findById(reservationId)
                                     .orElseThrow(
                                             () -> new ReservationException(ErrorCode.NOT_FOUND_RESERVATION));

        reservation.setCompletedStatus(completedStatus);

        Reservation changedReservation = reservationRepository.save(reservation);
        return reservation;
    }

    public Reservation checkArrivedTime(Long storeId, String memberId) {
        log.info("[checkArrivedTime] 매장 아이디 및 고객 아이디와 일치하는 예약정보 불러오기");
        Reservation reservation = reservationRepository.findByStoreStoreIdAndMemberMemberId(storeId, memberId)
                                                       .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_RESERVATION));

        log.info("[checkArrivedTime] is already arrived reservation check");
        if (reservation.getArrivedStatus() == ArrivedStatus.ARRIVED) {
            log.error("[checkArrivedTime] already arrived reservation");
            throw new ReservationException(ErrorCode.ALREADY_ARRIVED_RESERVATION);
        }
        log.info("[checkArrivedTime] not already arrive. is OK");

        LocalDateTime nowTime = LocalDateTime.now();
        if (!isValidArrivedTime(nowTime, reservation.getScheduledTime())) {
            log.error("[checkArrivedTime] 시간 만료. 10분 전 도착하지 않음.");
            reservation.setReservationStatus(ReservationStatus.REJECTED);
            reservation.setArrivedStatus(ArrivedStatus.ARRIVED);
            reservation.setCompletedStatus(CompletedStatus.CANCEL);
        } else {
            reservation.setArrivedStatus(ArrivedStatus.ARRIVED);
            reservation.setCompletedStatus(CompletedStatus.COMPLETE);
        }

        Reservation changedStatus = reservationRepository.save(reservation);

        if (changedStatus.getReservationStatus() == ReservationStatus.REJECTED) {
            throw new ReservationException(ErrorCode.IS_REJECTED_RESERVATION);
        }

        return changedStatus;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void checkScheduledTime() {
        log.info("[checkScheduledTime] 만료된 예약 정보 확인");
        List<Reservation> allReservations = reservationRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Reservation reservation : allReservations) {
            LocalDateTime reservationTime = reservation.getScheduledTime();
            long minutesDifference = ChronoUnit.MINUTES.between(now, reservationTime);

            if (minutesDifference < 10) {
                // 예약 시간과 현재 시간의 차이가 10분 미만인 경우
                reservation.setReservationStatus(ReservationStatus.REJECTED);
                reservationRepository.save(reservation);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteExpiredMonthlyReservations() {
        log.info("[deleteExpiredReservations - Monthly] 만료된 예약 정보 확인");
        List<Reservation> allReservations = reservationRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Reservation reservation : allReservations) {
            LocalDateTime reservationTime = reservation.getScheduledTime();
            long minutesDifference = ChronoUnit.MINUTES.between(now, reservationTime);

            if (minutesDifference < 10) {
                // 예약 시간과 현재 시간의 차이가 10분 미만인 경우 예약 정보를 삭제
                reservationRepository.delete(reservation);
            }
        }
    }

    private boolean isValidArrivedTime(LocalDateTime now, LocalDateTime reservationTime) {
        long minutesDifference = ChronoUnit.MINUTES.between(now, reservationTime);
        if (minutesDifference < 10) {
            return false;
        } else {
            return true;
        }
    }
}
