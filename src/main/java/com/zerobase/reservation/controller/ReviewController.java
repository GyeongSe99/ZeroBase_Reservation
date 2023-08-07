package com.zerobase.reservation.controller;

import com.zerobase.reservation.domain.Review;
import com.zerobase.reservation.dto.Review.AddReviewDto;
import com.zerobase.reservation.dto.Review.DeleteReviewDto;
import com.zerobase.reservation.dto.Review.UpdateReviewDto;
import com.zerobase.reservation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("review")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("add")
    @PreAuthorize("@securityService.isStoreCustomer(#request.getReservationId(), authentication)")
    public AddReviewDto.Response addReview(@RequestBody AddReviewDto.Request request) {
        log.info("[addReview] 리뷰 추가");

        Review review = reviewService.addReview(request);

        return AddReviewDto.Response.builder()
                                    .storeId(review.getStore()
                                                   .getStoreId())
                                    .memberId(review.getMember()
                                                    .getMemberId())
                                    .star(review.getStar())
                                    .reviewContext(request.getReviewContext())
                                    .build();
    }

    @PostMapping("update")
    @PreAuthorize("@securityService.isStoreCustomer(#request.getReservationId(), authentication)")
    public UpdateReviewDto.Response updateReview(@RequestBody UpdateReviewDto.Request request) {
        log.info("[updateReview] 리뷰 수정");

        Review review = reviewService.updateReview(request);

        return UpdateReviewDto.Response.builder()
                                       .storeId(review.getStore()
                                                      .getStoreId())
                                       .memberId(review.getMember()
                                                       .getMemberId())
                                       .star(review.getStar())
                                       .reviewContext(request.getReviewContext())
                                       .build();
    }

    @PostMapping("delete")
    @PreAuthorize("@securityService.isStoreCustomer(#request.getReservationId(), authentication)")
    public void deleteReview(@RequestBody DeleteReviewDto.Request request) {
        log.info("[deleteReview]");

        reviewService.deleteReview(request);
        log.info("[deleteReview] 삭제 완료");
    }
}
