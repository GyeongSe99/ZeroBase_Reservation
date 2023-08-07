package com.zerobase.reservation.type;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND_MEMBER("사용자가 없습니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다."),
    ALREADY_EXIST_MEMBER("이미 등록된 사용자가 존재합니다."),
    PASSWORD_NOT_CORRECT("비밀번호가 등록된 유저 정보와 일치하지 않습니다."),
    IS_NOT_PARTNER("파트너에 가입된 상태가 아닙니다."),
    ADDRESS_NOT_FOUND("해당 주소를 찾을 수 없습니다."),
    IS_REJECTED_RESERVATION("거부된 예약입니다."),
    ALREADY_ARRIVED_RESERVATION("이미 도착 완료 승인을 받은 예약입니다."),
    NOT_FOUND_STORE("해당 매장Id로 등록된 매장이 없습니다."),
    NOT_FOUND_RESERVATION("해당 예약Id로 등록된 예약을 찾을 수 없습니다."),
    NOT_AVAILABLE_ADD_REVIEW("리뷰 등록 가능한 상태가 아닙니다."),
    NOT_FOUND_REVIEW("해당 리뷰Id로 등록된 후기가 없습니다."),
    NO_RESERVATION_BY_STORE("해당 매장Id로 예약된 정보를 찾을 수 없습니다.");


    private final String description;
}
