package com.benefits.appointments.models.dto.output;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CalendarEventsOutputDTO {

  private String created;
  private String creator;
  private String end;
  private String start;
  private String status;
  private String summary;
  private String organizer;
  private String googleMeet;
  private String description;
  private List<Attendees> attendeesList;

  @Data
  @AllArgsConstructor
  public static class Attendees {

    private String email;
    private String responseStatus;
  }

  public CalendarEventsOutputDTO(String workEmail, String startDate, String endDate, String googleMeet, String summary,
                                 String createdAt, String personalEmail) {
    this.creator = workEmail;
    this.start = startDate;
    this.end = endDate;
    this.googleMeet = googleMeet;
    this.summary = summary;
    this.created = createdAt;
    this.organizer = personalEmail;
    this.attendeesList = List.of(new Attendees(workEmail, "accepted"),
        new Attendees(personalEmail, "pending"));
  }
}
