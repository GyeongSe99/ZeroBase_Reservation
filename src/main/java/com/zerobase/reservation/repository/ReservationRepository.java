package com.zerobase.reservation.repository;

import com.zerobase.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByStoreStoreIdOrderByScheduledTimeAsc(Long storeId);

    Optional<Reservation> findByStoreStoreIdAndMemberMemberId(Long storeId, String memberId);

}
