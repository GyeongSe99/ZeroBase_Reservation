package com.zerobase.reservation.service;

import com.zerobase.reservation.common.config.exception.ReservationException;
import com.zerobase.reservation.domain.Member;
import com.zerobase.reservation.domain.Reservation;
import com.zerobase.reservation.domain.Review;
import com.zerobase.reservation.domain.Store;
import com.zerobase.reservation.dto.Review.AddReviewDto;
import com.zerobase.reservation.dto.Review.DeleteReviewDto;
import com.zerobase.reservation.dto.Review.UpdateReviewDto;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.repository.ReservationRepository;
import com.zerobase.reservation.repository.ReviewRepository;
import com.zerobase.reservation.repository.StoreRepository;
import com.zerobase.reservation.type.ArrivedStatus;
import com.zerobase.reservation.type.CompletedStatus;
import com.zerobase.reservation.type.ErrorCode;
import com.zerobase.reservation.type.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zerobase.reservation.type.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Review addReview(AddReviewDto.Request request) {
        log.info("[addReview] 예약 정보 확인");
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                                                       .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_RESERVATION));

        if (reservation.getReservationStatus() == ReservationStatus.APPROVED &&
                reservation.getArrivedStatus() == ArrivedStatus.ARRIVED &&
                reservation.getCompletedStatus() == CompletedStatus.COMPLETE) {
            log.info("[addReview] 예약 정보 확인 완료. is Ok");
            log.info("[addReview] Star Rating 갱신");
            // 별점 계산해서 매장 정보 갱신

            Store store = storeRepository.findById(request.getStoreId())
                                         .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));
            Store calculatedStore = calculateStarRating(store, request.getStar());


            Member member = memberRepository.findById(request.getMemberId())
                                            .orElseThrow(() -> new ReservationException(NOT_FOUND_MEMBER));

            Review review = Review.builder()
                                  .store(store)
                                  .member(member)
                                  .star(request.getStar())
                                  .context(request.getReviewContext())
                                  .build();

            return reviewRepository.save(review);
        } else {
            throw new ReservationException(NOT_AVAILABLE_ADD_REVIEW);
        }
    }

    @Transactional
    public Review updateReview(UpdateReviewDto.Request request) {
        log.info("[updateReview] 리뷰 수정 진행");
        Long reviewId = request.getReviewId();
        Review review = reviewRepository.findById(reviewId)
                                        .orElseThrow(() -> new ReservationException(NOT_FOUND_REVIEW));
        Store store = storeRepository.findById(request.getStoreId())
                                     .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));

        log.info("[updateReview] 별점 수정 ");
        Long totalStar = store.getTotalStar();
        Long totalReviewCount = store.getReviewCount();
        log.info("별점 변경 전 | totalStar: {}, totalReviewCount: {}", totalStar, totalReviewCount);

        totalStar -= review.getStar();
        totalReviewCount--;

        Store calculatedStore = calculateStarRating(store, request.getStar());
        log.info("변경 후 | totalStar: {}, totalReviewCount: {}", calculatedStore.getTotalStar(), calculatedStore.getReviewCount());

        review.setStar(request.getStar());
        review.setContext(request.getReviewContext());

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(DeleteReviewDto.Request request) {
        log.info("[deleteReview] 리뷰 삭제 진행");
        Long reviewId = request.getReviewId();
        Review review = reviewRepository.findById(reviewId)
                                        .orElseThrow(() -> new ReservationException(NOT_FOUND_REVIEW));
        Store store = storeRepository.findById(request.getStoreId())
                                     .orElseThrow(() -> new ReservationException(NOT_FOUND_STORE));

        Long totalStar = store.getTotalStar();
        Long totalReviewCount = store.getReviewCount();
        log.info("별점 변경 전 | totalStar: {}, totalReviewCount: {}", totalStar, totalReviewCount);

        totalStar -= review.getStar();
        totalReviewCount--;
        double averageRating = (double) totalStar / totalReviewCount;

        store.setTotalStar(totalStar);
        store.setReviewCount(totalReviewCount);
        store.setStarRating(averageRating);

        Store calculatedStore = storeRepository.save(store);

        log.info("변경 후 | totalStar: {}, totalReviewCount: {}", calculatedStore.getTotalStar(), calculatedStore.getReviewCount());

        reviewRepository.delete(review);
    }

    private Store calculateStarRating(Store store, int starRating) {

        Long totalStar = store.getTotalStar();
        Long totalReviewCount = store.getReviewCount();

        totalStar += starRating;
        totalReviewCount++;

        double averageRating = (double) totalStar / totalReviewCount;

        store.setTotalStar(totalStar);
        store.setReviewCount(totalReviewCount);
        store.setStarRating(averageRating);

        return storeRepository.save(store);
    }
}
