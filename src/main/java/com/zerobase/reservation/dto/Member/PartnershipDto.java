package com.zerobase.reservation.dto.Member;

import com.zerobase.reservation.domain.Member;
import com.zerobase.reservation.type.PartnershipStatus;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class PartnershipDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull
        @Email
        private String memberId;

        @NotNull
        private String password;

        @NotNull
        private PartnershipStatus partnershipStatus;

    }

    @Getter
    @Setter
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
        private PartnershipStatus partnershipStatus;

        public static PartnershipDto.Response memberToResponse(Member member) {
            return PartnershipDto.Response.builder()
                                          .memberId(member.getMemberId())
                                          .memberName(member.getMemberName())
                                          .password(member.getPassword())
                                          .partnershipStatus(member.getPartnershipStatus())
                                          .build();
        }
    }
}
