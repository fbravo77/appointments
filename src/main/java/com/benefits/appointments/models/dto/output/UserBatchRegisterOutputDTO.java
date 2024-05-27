package com.benefits.appointments.models.dto.output;

import lombok.Data;
@Data
public class UserBatchRegisterOutputDTO {
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
