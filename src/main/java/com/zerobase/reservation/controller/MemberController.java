package com.zerobase.reservation.controller;

import com.zerobase.reservation.dto.Member.MemberDto;
import com.zerobase.reservation.dto.Member.PartnershipDto;
import com.zerobase.reservation.dto.Member.SignInResultDto;
import com.zerobase.reservation.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("register")
    public void registerPage() {
        log.info("회원 가입 페이지");
    }

    @PostMapping("register")
    public MemberDto.Response register(@RequestBody @Valid MemberDto.Request request) {
        log.info("회원 가입");
        return MemberDto.Response.memberToResponse(
                memberService.createMember(request)
        );
    }

    @PostMapping("sign-api/login")
    public SignInResultDto login(@RequestParam String memberId, @RequestParam String password) {
        log.info("로그인");
        SignInResultDto signInResultDto = login(memberId, password);

        if (signInResultDto.getCode() == 0) {
            log.info("[signIn] 정상적으로 로그인 되었습니다.");
        }
        return signInResultDto;
    }


    @RequestMapping("sign-api/exception")
    @ApiIgnore
    public void exception() {
        log.info("접근 권한 없음");
        throw new RuntimeException("접근이 금지 되었습니다.");
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Map<String, String>> exceptionHandler(RuntimeException e) {
        HttpHeaders responseHeaders = new HttpHeaders();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        log.error("ExceptionHandler 호출, {}, {}", e.getCause(), e.getMessage());

        Map<String, String> map = new HashMap<>();
        map.put("errortype", httpStatus.getReasonPhrase());
        map.put("code", "400");
        map.put("message", "에러 발생");

        return new ResponseEntity<>(map, responseHeaders, httpStatus);
    }


    @PostMapping("partnership/join")
    public PartnershipDto.Response joinPartnership(@RequestBody @Valid PartnershipDto.Request request) {
        log.info("파트너쉽 가입");
        return PartnershipDto.Response.memberToResponse(memberService.joinPartnership(request));
    }

    @PostMapping("partnership/cancel")
    public PartnershipDto.Response cancelPartnership(@RequestBody @Valid PartnershipDto.Request request) {
        log.info("파트너쉽 해지");
        return PartnershipDto.Response.memberToResponse(memberService.cancelPartnership(request));
    }

}
