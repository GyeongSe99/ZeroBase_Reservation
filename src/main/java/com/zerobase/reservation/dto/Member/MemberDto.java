package com.zerobase.reservation.dto.Member;

import com.zerobase.reservation.domain.Member;
import com.zerobase.reservation.type.PartnershipStatus;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class MemberDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull
        @Email
        private String memberId;

        @NotNull
        private String memberName;

        @NotNull
        private String password;

        @NotNull
        private String phoneNumber;

        @NotNull
        private PartnershipStatus partnership;

        public static Member requestToMember(MemberDto.Request request) {
            return Member.builder()
                         .memberId(request.getMemberId())
                         .memberName(request.getMemberName())
                         .password(request.getPassword())
                         .partnershipStatus(request.getPartnership())
                         .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        @NotNull
        @Email
        private String memberId;

        @NotNull
        private String memberName;

        @NotNull
        private String password;

        @NotNull
        private String phoneNumber;

        @NotNull
        private PartnershipStatus partnership;

        public static Response memberToResponse(Member member) {
            return Response.builder()
                           .memberId(member.getMemberId())
                           .memberName(member.getMemberName())
                           .password(member.getPassword())
                           .partnership(member.getPartnershipStatus())
                           .build();
        }
    }
}
