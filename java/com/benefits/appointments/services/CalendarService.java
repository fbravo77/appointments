package com.benefits.appointments.services;

import com.benefits.appointments.models.dto.input.AppointmentsReqDTO;
import com.benefits.appointments.models.dto.output.CalendarEventsResDto;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {

  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String TOKENS_DIRECTORY_PATH = "src/main/resources/tokens";
  private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";
  private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
  private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
  Mapper mapper = DozerBeanMapperBuilder.buildDefault();
  private static final Logger logger = LogManager.getLogger(CalendarService.class);

  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String credentialsForUser)
      throws IOException {
    // Load client secrets.
    File initialFile = new File(CREDENTIALS_FILE_PATH);
    InputStream in = new FileInputStream(initialFile);
    // InputStream in = AppointmentsApplication.class.getResourceAsStream(credentialsForUser);
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(
            new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH + "/" + credentialsForUser)))
        .setAccessType("offline")
        .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    credential.refreshToken();
    //returns an authorized Credential object.
    return credential;
  }

  public List<CalendarEventsResDto> getAvailableDates(String tokenPath) throws GeneralSecurityException, IOException {
    // Build a new authorized API client service.
    boolean isRemote = true;

    Calendar service = getCalendarApi(tokenPath);

    // List the next 10 events from the primary calendar.
    DateTime now = new DateTime(System.currentTimeMillis());
    DateTime end = new DateTime(
        LocalDateTime.now().plusDays(30L).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    try {
      Events events = service.events().list("primary")
          .setMaxResults(200)
          .setTimeMin(now)
          .setTimeMax(end)
          .setOrderBy("startTime")
          .setSingleEvents(true)
          .execute();
      List<Event> items = events.getItems();

      List<CalendarEventsResDto> eventsListDto = new ArrayList<>();
      if (items.isEmpty()) {
        return eventsListDto;
      } else {
        for (Event event : items) {
          CalendarEventsResDto calendarEventsResDto = mapper.map(event, CalendarEventsResDto.class);

          if (event.getStart().getDateTime() != null) {
            calendarEventsResDto.setStart(event.getStart().getDateTime().toString());
          }
          if (event.getEnd().getDateTime() != null) {
            calendarEventsResDto.setEnd(event.getEnd().getDateTime().toString());
          }
          calendarEventsResDto.setCreator(event.getCreator().getEmail());
          calendarEventsResDto.setOrganizer(event.getOrganizer().getEmail());
          calendarEventsResDto.setGoogleMeet(event.getHangoutLink());
          calendarEventsResDto.setDescription(event.getDescription());
          if (event.getAttendees() != null) {
            calendarEventsResDto.setAttendeesList(event.getAttendees().stream()
                .map(x -> (new CalendarEventsResDto.Attendees(x.getEmail(), x.getResponseStatus()))).collect(
                    Collectors.toList()));
          }
          eventsListDto.add(calendarEventsResDto);
        }
      }
      return eventsListDto;
    } catch (IOException e) {
      logger.error(e);
      return null;
    }
  }

  private static Calendar getCalendarApi(String tokenPath) {
    try {
      final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

      return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY,
          getCredentials(HTTP_TRANSPORT, tokenPath))
          .setApplicationName(APPLICATION_NAME)
          .build();
    } catch (Exception e) {
      logger.error("Error: " + e);
      return null;
    }
  }

  public String createMeeting(String tokenPath, AppointmentsReqDTO appointment, String... attendeeEmail) {
    try {
      String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=1"};
      Calendar service = getCalendarApi(tokenPath);
      DateTime startDateTime = new DateTime(appointment.getStartDate());
      EventDateTime start = new EventDateTime()
          .setDateTime(startDateTime)
          .setTimeZone("America/Los_Angeles");

      DateTime endDateTime = new DateTime(appointment.getEndDate());
      EventDateTime endT = new EventDateTime()
          .setDateTime(endDateTime)
          .setTimeZone("America/Los_Angeles");

      Event event = new Event()
          .setSummary(appointment.getSummary())
          .setLocation(appointment.getRemote() ? "remote" : appointment.getLocation())
          .setDescription(appointment.getDescription())
          .setStart(start)
          .setEnd(endT)
          .setRecurrence(Arrays.asList(recurrence))
          .setAttendees(
              Arrays.stream(attendeeEmail).filter(Objects::nonNull).map(x -> new EventAttendee().setEmail(x)).collect(Collectors.toList()));

      //MEET
      String calendarId = "primary";
      ConferenceSolutionKey conferenceSKey = new ConferenceSolutionKey();
      conferenceSKey.setType("hangoutsMeet");
      CreateConferenceRequest createConferenceReq = new CreateConferenceRequest();
      createConferenceReq.setRequestId(AuthenticationService.generatePassword(15));
      createConferenceReq.setConferenceSolutionKey(conferenceSKey);
      ConferenceData conferenceData = new ConferenceData();
      conferenceData.setCreateRequest(createConferenceReq);
      event.setConferenceData(conferenceData);
      event = service.events().insert(calendarId, event).setConferenceDataVersion(1).execute();
      return event.getHangoutLink();
    } catch (Exception e) {
      logger.error("Error: " + e);
      return null;
    }
  }
}
