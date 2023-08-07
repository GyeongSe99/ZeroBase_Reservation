package com.zerobase.reservation.controller;

import com.zerobase.reservation.domain.Store;
import com.zerobase.reservation.dto.Store.AddStoreDto;
import com.zerobase.reservation.dto.Store.UpdateStoreDto;
import com.zerobase.reservation.service.KakaoMapService;
import com.zerobase.reservation.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("store")
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class StoreController {
    private StoreService storeService;
    private KakaoMapService kakaoMapService;

    @GetMapping("addStore/searchAddress")
    public ResponseEntity<?> searchAddress(@Valid @RequestParam String search) {
        return kakaoMapService.geocodingParsing(search);
    }

    @PostMapping("addStore")
    public AddStoreDto.Response addStore(@Valid @RequestBody AddStoreDto.Request request) {
        return AddStoreDto.Response.storeToResponse(storeService.addStore(request));
    }

    // 가나다순
    @GetMapping("show/ABC")
    public List<Store> showStoreListByAlphabetical() {
        return storeService.showStoreListByAlphabetical();
    }

    // 별점높은순
    @GetMapping("show/star")
    public List<Store> showStoreListByStarRating() {
        return storeService.showStoreListByStarRating();
    }

    // 거리 가까운 순
    @PostMapping("show/distance")
    public List<Store> showStoreListByDistance(@Valid @RequestParam double x, @Valid @RequestParam double y) {
        return storeService.showStoreListByDistance(x, y);
    }

    @GetMapping("show/detail/{storeId}")
    public Store showStoreDetail(@PathVariable Long storeId) {
        return storeService.showStoreDetail(storeId);
    }

    @GetMapping("storeList")
    public List<Store> showStoreListForPartner() {
        String memberId = getLoginId();

        return storeService.showStoreListForPartner(memberId);
    }

    @PostMapping("updateStore")
    @PreAuthorize("@securityService.isStoreOwner(#storeId, authentication)")
    public void updateStoreDetail(@Valid @RequestParam Long storeId, @Valid @RequestBody UpdateStoreDto.Request request) {
        // 이미 등록되어있는 매장 정보 수정
        String memberId = getLoginId();

        storeService.updateStore(storeId, request);
    }

    private String getLoginId() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();
        String memberId = authentication.getName();

        if (memberId == null) return "";

        return memberId;
    }

}
