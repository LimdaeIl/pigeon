package com.house.pigeon.member.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record CreateMemberRequest(
        @NotBlank(message = "이메일: 필수 정보입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "이메일: 유효하지 않은 이메일입니다.")
        @Size(min = 6, max = 32, message = "이메일: 유효하지 않은 이메일입니다.")
        String email,

        @NotBlank(message = "비밀번호: 필수 정보입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}"
                , message = "비밀번호: 8~20자 영문 대소문자, 숫자, 특수문자를 조합하여 작성해야 합니다.")
        @Size(min = 8, max = 20, message = "비밀번호: 8~20자 영문 대소문자, 숫자, 특수문자를 조합하여 작성해야 합니다.")
        String password,

        @NotBlank(message = "휴대전화번호: 필수 정보입니다.")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "휴대전화번호: 10~11자 숫자만 입력해주세요.")
        @Size(min = 10, max = 11, message = "휴대전화번호: 10~11자 숫자만 입력해주세요.")
        String phone
) {

    @Builder
    public CreateMemberRequest {
    }
}
