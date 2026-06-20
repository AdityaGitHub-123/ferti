package com.fertilizer.ruleengine.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OtpVerifyRequest {
    @NotBlank(message = "Registered email is required")
    @Email(message = "Enter a valid registered email")
    private String email;

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "\\d{4,6}", message = "OTP must be 4 to 6 digits")
    private String otp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
