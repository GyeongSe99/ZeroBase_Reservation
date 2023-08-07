package com.zerobase.reservation.service;

import com.zerobase.reservation.common.config.exception.ReservationException;
import com.zerobase.reservation.domain.Member;
import com.zerobase.reservation.domain.Store;
import com.zerobase.reservation.dto.Store.AddStoreDto;
import com.zerobase.reservation.dto.Store.UpdateStoreDto;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.repository.StoreRepository;
import com.zerobase.reservation.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private StoreRepository storeRepository;
    private MemberRepository memberRepository;

    @Transactional
    public Store addStore(AddStoreDto.Request request) {
        Member member = getMember(request.getMemberId());

        Store newStore = Store.builder()
                              .member(member)
                              .storeName(request.getStoreName())
                              .latitude(request.getLatitude())
                              .longitude(request.getLongitude())
                              .storeDescription(request.getStoreDescription())
                              .build();

        return storeRepository.save(newStore);
    }

    public List<Store> showStoreListByAlphabetical() {
        List<Store> storeList = storeRepository.findAllByOrderByStoreNameAsc();

        if (storeList.size() == 0) {
            throw new ReservationException(ErrorCode.NOT_FOUND_STORE);
        }

        return storeList;
    }

    public List<Store> showStoreListByStarRating() {
        List<Store> storeList = storeRepository.findAllByOrderByStarRatingDesc();

        if (storeList.size() == 0) {
            throw new ReservationException(ErrorCode.NOT_FOUND_STORE);
        }

        return storeList;
    }

    public List<Store> showStoreListByDistance(double x, double y) {
        List<Store> storeList = storeRepository.findAllByDistance(x, y);

        if (storeList.size() == 0) {
            throw new ReservationException(ErrorCode.NOT_FOUND_STORE);
        }

        return storeList;
    }

    public Store showStoreDetail(Long storeId) {
        Store store = storeRepository.findById(storeId)
                                     .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_STORE));

        return store;
    }

    public List<Store> showStoreListForPartner(String memberId) {
        Member member = getMember(memberId);
        List<Store> stores = member.getStores();

        if (stores.size() == 0) {
            throw new ReservationException(ErrorCode.NOT_FOUND_STORE);
        }

        return stores;
    }

    @Transactional
    public Store updateStore(Long storeId, UpdateStoreDto.Request request) {
        Store store = showStoreDetail(storeId);

        store.setStoreName(request.getStoreName());
        store.setAddress(request.getAddress());
        store.setLatitude(request.getLatitude());
        store.setLongitude(request.getLongitude());
        store.setStoreDescription(request.getStoreDescription());

        return storeRepository.save(store);
    }


    private Member getMember(String memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_MEMBER));
        return member;
    }

}
