package com.house.pigeon.member.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EmailVerificationRequest(
        @NotBlank(message = "이메일: 필수 정보입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "이메일: 유효하지 않은 이메일입니다.")
        @Size(min = 6, max = 32, message = "이메일: 유효하지 않은 이메일입니다.")
        String email
) {

}

