package com.rookiesvnclient.dto.auth;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username; // SVN 접속 ID
    private String password; // SVN 접속 PW
}
