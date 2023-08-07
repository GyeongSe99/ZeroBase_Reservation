package com.zerobase.reservation.repository;

import com.zerobase.reservation.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findAllByOrderByStoreNameAsc();
    List<Store> findAllByOrderByStarRatingDesc();

    @Query("SELECT s FROM Store s ORDER BY SQRT((s.latitude - :targetX) * (s.latitude - :targetX) + (s.longitude - :targetY) * (s.longitude - :targetY)) ASC")
    List<Store> findAllByDistance(@Param("targetX") double targetX, @Param("targetY") double targetY);

}
