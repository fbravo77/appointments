package com.benefits.appointments.models.dto.output;

import lombok.Data;

@Data
public class StandardResponse {
  private String response;
  private String data;

  public StandardResponse(String response, String data) {
    this.response = response;
    this.data = data;
  }
}
