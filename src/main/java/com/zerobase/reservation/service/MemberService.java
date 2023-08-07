package com.zerobase.reservation.service;

import com.zerobase.reservation.common.config.exception.ReservationException;
import com.zerobase.reservation.common.config.security.JwtTokenProvider;
import com.zerobase.reservation.domain.Member;
import com.zerobase.reservation.dto.Member.MemberDto;
import com.zerobase.reservation.dto.Member.PartnershipDto;
import com.zerobase.reservation.dto.Member.SignInResultDto;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.type.CommonResponse;
import com.zerobase.reservation.type.ErrorCode;
import com.zerobase.reservation.type.PartnershipStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Member createMember(MemberDto.Request request) {
        log.info("회원 가입 정보 전달");
        if (memberRepository.findById(request.getMemberId())
                            .isPresent()) {
            throw new ReservationException(ErrorCode.ALREADY_EXIST_MEMBER);
        }

        Member newMember = Member.builder()
                                 .memberId(request.getMemberId())
                                 .memberName(request.getMemberName())
                                 .password(passwordEncoder.encode(request.getPassword()))
                                 .phoneNumber(request.getPhoneNumber())
                                 .partnershipStatus(request.getPartnership())
                                 .build();

        memberRepository.save(newMember);

        if (!newMember.getMemberId()
                      .isEmpty()) {
            log.info("[createMember] 정상 처리 완료");
        } else {
            log.info("[createMember] 실패");
            throw new RuntimeException("회원 가입 실패");
        }

        return newMember;
    }

    public SignInResultDto login(String memberId, String password) {
        log.info("[login] 회원 정보 요청, memberId: {}", memberId);
        Member member = getMember(memberId);

        log.info("[login] 패스워드 일치 확인");
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new ReservationException(ErrorCode.PASSWORD_NOT_CORRECT);
        }

        SignInResultDto signInResultDto =
                SignInResultDto.builder()
                               .token(jwtTokenProvider.createToken(String.valueOf(member.getMemberId())))
                               .build();

        setSuccessResult(signInResultDto);

        return signInResultDto;
    }

    private void setSuccessResult(SignInResultDto signInResultDto) {
        signInResultDto.setSuccess(true);
        signInResultDto.setCode(CommonResponse.SUCCESS.getCode());
        signInResultDto.setMsg(CommonResponse.SUCCESS.getMsg());
    }


    @Transactional
    public Member joinPartnership(PartnershipDto.Request request) {
        Member member = getMember(request.getMemberId());
        member.setPartnershipStatus(PartnershipStatus.PARTNER);
        memberRepository.save(member);
        return member;
    }

    @Transactional
    public Member cancelPartnership(PartnershipDto.Request request) {
        Member member = getMember(request.getMemberId());

        member.setPartnershipStatus(PartnershipStatus.GENERAL);
        memberRepository.save(member);

        return member;
    }

    public Member getMember(String memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new ReservationException(ErrorCode.NOT_FOUND_MEMBER));
        return member;
    }


    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        log.info("[loadUserByUsername] memberId: {}", memberId);
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 권한 설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getPartnershipStatus() == PartnershipStatus.PARTNER) {
            authorities.add(new SimpleGrantedAuthority("partner"));
        } else {
            authorities.add(new SimpleGrantedAuthority("General"));
        }

        return new User(member.getMemberId(), member.getPassword(), authorities);
    }
}

