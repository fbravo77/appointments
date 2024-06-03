package com.benefits.appointments.services;

import com.benefits.appointments.models.dto.input.CreateAppointmentInputDTO;
import com.benefits.appointments.models.dto.output.CalendarEventsOutputDTO;
import com.benefits.appointments.security.service.AuthenticationService;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
  private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
  private static final Logger logger = LogManager.getLogger(CalendarService.class);
  private final NetHttpTransport httpTransport;
  private final String tokensDirectoryPath;
  private final String credentialsFilePath;
  private final Mapper mapper = DozerBeanMapperBuilder.buildDefault();

  public CalendarService(@Value("${google.api.tokens.directory}") String tokensDirectoryPath,
                         @Value("${google.api.credentials.file}") String credentialsFilePath) throws GeneralSecurityException, IOException {
    this.tokensDirectoryPath = tokensDirectoryPath;
    this.credentialsFilePath = credentialsFilePath;
    this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
  }

  private Credential getCredentials(String credentialsForUser) throws IOException {
    File initialFile = new File(credentialsFilePath);
    InputStream in = new FileInputStream(initialFile);
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokensDirectoryPath + "/" + credentialsForUser)))
        .setAccessType("offline")
        .build();

    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    credential.refreshToken();
    return credential;
  }

  private Calendar getCalendarApi(String tokenPath) throws IOException {
    return new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials(tokenPath))
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  public List<CalendarEventsOutputDTO> getAvailableDates(String tokenPath) throws GeneralSecurityException, IOException {
    Calendar service = getCalendarApi(tokenPath);
    DateTime now = new DateTime(System.currentTimeMillis());
    DateTime end = new DateTime(LocalDateTime.now().plusDays(30L).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

    try {
      Events events = service.events().list("primary")
          .setMaxResults(200)
          .setTimeMin(now)
          .setTimeMax(end)
          .setOrderBy("startTime")
          .setSingleEvents(true)
          .execute();

      return events.getItems().stream().map(event -> {
        CalendarEventsOutputDTO calendarEventsOutputDTO = mapper.map(event, CalendarEventsOutputDTO.class);
        if (event.getStart().getDateTime() != null) {
          calendarEventsOutputDTO.setStart(event.getStart().getDateTime().toString());
        }
        if (event.getEnd().getDateTime() != null) {
          calendarEventsOutputDTO.setEnd(event.getEnd().getDateTime().toString());
        }
        calendarEventsOutputDTO.setCreator(event.getCreator().getEmail());
        calendarEventsOutputDTO.setOrganizer(event.getOrganizer().getEmail());
        calendarEventsOutputDTO.setGoogleMeet(event.getHangoutLink());
        calendarEventsOutputDTO.setDescription(event.getDescription());
        if (event.getAttendees() != null) {
          calendarEventsOutputDTO.setAttendeesList(event.getAttendees().stream()
              .map(x -> new CalendarEventsOutputDTO.Attendees(x.getEmail(), x.getResponseStatus()))
              .collect(Collectors.toList()));
        }
        return calendarEventsOutputDTO;
      }).collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Error fetching available dates: {}", e.getMessage());
      throw e;
    }
  }

  public String createMeeting(String tokenPath, CreateAppointmentInputDTO appointment, String... attendeeEmail) {
    try {
      Calendar service = getCalendarApi(tokenPath);

      Event event = new Event()
          .setSummary(appointment.getSummary())
          .setLocation(appointment.getRemote() ? "remote" : appointment.getLocation())
          .setDescription(appointment.getDescription())
          .setStart(new EventDateTime().setDateTime(new DateTime(appointment.getStartDate())).setTimeZone("America/Los_Angeles"))
          .setEnd(new EventDateTime().setDateTime(new DateTime(appointment.getEndDate())).setTimeZone("America/Los_Angeles"))
          .setRecurrence(Collections.singletonList("RRULE:FREQ=DAILY;COUNT=1"))
          .setAttendees(Arrays.stream(attendeeEmail).filter(Objects::nonNull).map(email -> new EventAttendee().setEmail(email)).collect(Collectors.toList()));

      ConferenceData conferenceData = new ConferenceData();
      conferenceData.setCreateRequest(new CreateConferenceRequest()
          .setRequestId(AuthenticationService.generatePassword(15))
          .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet")));
      event.setConferenceData(conferenceData);

      event = service.events().insert("primary", event).setConferenceDataVersion(1).execute();
      return event.getHangoutLink();
    } catch (Exception e) {
      logger.error("Error creating meeting: {}", e.getMessage());
      return null;
    }
  }
}
