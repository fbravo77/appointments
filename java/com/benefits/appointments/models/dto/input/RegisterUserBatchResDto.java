package com.benefits.appointments.models.dto.input;

import lombok.Data;
@Data
public class RegisterUserBatchResDto {
    private String workday;
    private String status;
    private String fullName;

    @Override
    public String toString() {
        return "{" +
            "workday='" + workday + '\'' +
            ", status='" + status + '\'' +
            ", fullName='" + fullName + '\'' +
            '}';
    }
}
