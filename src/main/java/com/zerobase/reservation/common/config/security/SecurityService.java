package com.zerobase.reservation.common.config.security;

import com.zerobase.reservation.domain.Reservation;
import com.zerobase.reservation.domain.Store;
import com.zerobase.reservation.service.ReservationService;
import com.zerobase.reservation.service.StoreService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    private StoreService storeService;
    private ReservationService reservationService;

    public boolean isStoreOwner(Long storeId, Authentication authentication) {
        String memberId = authentication.getName(); // 현재 로그인된 사용자의 아이디
        Store store = storeService.showStoreDetail(storeId); // 매장 정보 가져오기
        return store != null && store.getMember() != null && memberId.equals(store.getMember()
                                                                                  .getMemberId());
    }

    public boolean isStoreCustomer(Long reservationId, Authentication authentication) {
        String memberId = authentication.getName(); // 현재 로그인된 사용자의 아이디
        Reservation reservation = reservationService.getReservationById(reservationId);
        String reservationMemberId = reservation.getMember()
                                                .getMemberId();
        return memberId != null && reservationMemberId != null && memberId.equals(reservationMemberId);
    }
}
