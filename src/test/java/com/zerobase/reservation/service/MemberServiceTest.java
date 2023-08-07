package com.zerobase.reservation.service;

import com.zerobase.reservation.domain.Member;
import com.zerobase.reservation.dto.Member.MemberDto;
import com.zerobase.reservation.dto.Member.PartnershipDto;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.type.PartnershipStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용자 계정 생성 성공")
    void createMemberSuccess() {
        // given
        MemberDto.Request validRequest = MemberDto.Request.builder()
                                                          .memberId("test@example.com")
                                                          .memberName("cjstprud")
                                                          .password("12345678")
                                                          .phoneNumber("01012345678")
                                                          .partnership(PartnershipStatus.GENERAL)
                                                          .build();
        System.out.println(validRequest);

        // when
        when(memberRepository.findById(validRequest.getMemberId())).thenReturn(Optional.empty());
        Member createdMember = memberService.createMember(validRequest);

        //
        verify(memberRepository, times(1)).findById(validRequest.getMemberId());
        verify(memberRepository, times(1)).save(any(Member.class));
        assertNotNull(createdMember);
        assertEquals(validRequest.getMemberId(), createdMember.getMemberId());

    }

    @Test
    @DisplayName("계정 생성 실패_이미 있는 회원 정보")
    void createMember_DuplicateMember() {
        // Given
        // 리포지토리에 이미 존재하는 회원을 준비
        String existingMemberId = "existing@example.com";
        MemberDto.Request duplicateRequest = MemberDto.Request.builder()
                                                              .memberId("existing@example.com")
                                                              .memberName("cjstprud")
                                                              .password("12345678")
                                                              .phoneNumber("01012345678")
                                                              .partnership(PartnershipStatus.GENERAL)
                                                              .build();

        // when
        when(memberRepository.findById(existingMemberId)).thenReturn(Optional.of(new Member()));
        assertThrows(RuntimeException.class, () -> memberService.createMember(duplicateRequest));

        // Then
        verify(memberRepository, times(1)).findById(existingMemberId);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void joinPartnership_Success() {
        // Given
        String userId = "test@example.com";
        PartnershipDto.Request request =
                PartnershipDto.Request.builder()
                                      .memberId(userId)
                                      .password("12345678")
                                      .partnershipStatus(PartnershipStatus.PARTNER)
                                      .build();

        Member fakeMember = new Member();
        when(memberRepository.findById(userId)).thenReturn(Optional.of(fakeMember));

        // When
        Member resultMember = memberService.joinPartnership(request);

        // Then
        verify(memberRepository, times(1)).findById(userId);
        assertEquals(PartnershipStatus.PARTNER, fakeMember.getPartnershipStatus());
        verify(memberRepository, times(1)).save(fakeMember);

        assertNotNull(resultMember);
        assertEquals(PartnershipStatus.PARTNER, resultMember.getPartnershipStatus());
    }

    @Test
    void cancelPartnership_Success() {
        // Given
        String userId = "test@example.com";
        PartnershipDto.Request request =
                PartnershipDto.Request.builder()
                                      .memberId(userId)
                                      .password("12345678")
                                      .partnershipStatus(PartnershipStatus.GENERAL)
                                      .build();

        Member fakeMember = new Member();
        when(memberRepository.findById(userId)).thenReturn(Optional.of(fakeMember));

        // When
        Member resultMember = memberService.cancelPartnership(request);

        // Then
        verify(memberRepository, times(1)).findById(userId);
        assertEquals(PartnershipStatus.GENERAL, fakeMember.getPartnershipStatus());
        verify(memberRepository, times(1)).save(fakeMember);

        assertNotNull(resultMember);
        assertEquals(PartnershipStatus.GENERAL, resultMember.getPartnershipStatus());
    }
}