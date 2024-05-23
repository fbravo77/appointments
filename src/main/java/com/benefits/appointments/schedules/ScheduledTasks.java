package com.benefits.appointments.schedules;

import com.benefits.appointments.models.entities.Appointment;
import com.benefits.appointments.repositories.AppointmentRepository;
import com.benefits.appointments.security.repository.UserRepository;
import com.benefits.appointments.services.EmailService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

  @Autowired
  AppointmentRepository appointmentRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  EmailService emailService;

  @Value("${front-end.url}")
  private String frontEndUrl;

  private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

  //@Scheduled(cron = "0 0 8 * * *")
  // @Scheduled(fixedRate = 30000)
  public void reviewPendingAppointmentsAndSendReminder() {
    LocalDateTime now = LocalDateTime.now();
    int REMINDERS_DAY_INTERVAL = 3;
    List<Appointment> appointmentList = appointmentRepository.findByStartDateBetweenAndReminderSent(now,
        now.plusDays(5), false);
    getAppointmentsCheck(appointmentList,"REMINDER",now, REMINDERS_DAY_INTERVAL);
    logger.info("reviewPendingAppointmentsAndSendReminder Task performed at {} emails sent: {}", now , appointmentList.size());
  }

  //@Scheduled(cron = "0 0 8 * * *")
 // @Scheduled(fixedRate = 30000)
  public void reviewPendingAppointmentsAndSendConfirmation() {
    LocalDateTime now = LocalDateTime.now();
    int CONFIRMATION_DAY_INTERVAL = 3;
    List<Appointment> appointmentList = appointmentRepository.findByStartDateBetweenAndConfirmationSent(now,now.plusDays(
        5), false);
    getAppointmentsCheck(appointmentList,"CONFIRMATION",now, CONFIRMATION_DAY_INTERVAL);
    logger.info("reviewPendingAppointmentsAndSendConfirmation Task performed at {} emails sent: {}", now , appointmentList.size());
  }

  private void getAppointmentsCheck(List<Appointment> appointmentList, String taskType,LocalDateTime now, int interval){

    String personalEmail, workEmail, name, date;
    for (Appointment appointment : appointmentList) {
      LocalDateTime appointmentDate = appointment.getStartDate();
      if (now.isBefore(appointmentDate) && Duration.between(now, appointmentDate).toDays() == interval) {
        personalEmail = appointment.getPatient().getPersonalEmail();
        workEmail = appointment.getPatient().getWorkEmail();
        name = appointment.getPatient().getPatient().getPreferredName();
        date = appointment.getStartDate().toString();
        if(taskType.equals("CONFIRMATION")) {
          sendConfirmationEmail(name, date, appointment.getId(), personalEmail, workEmail);
          appointment.setConfirmationSent(true);
        }
        else if (taskType.equals("REMINDER")) {
          sendReminderEmail(name, date, personalEmail, workEmail);
          appointment.setReminderSent(true);
        }
        appointmentRepository.save(appointment);
      }
    }
  }

  private void sendReminderEmail(String name, String date, String... email) {
    try {
      String body = "Test EMAIL: Hola " + name + " Te recordamos que tienes una cita agendada para la fecha: <b>" + date + " </b>";
      emailService.sendEmail(email, "Recordatorio de cita WELLNESS TEST", body);
    } catch (Exception e) {
      logger.error("sendReminderEmail Error: " + e);
    }
  }

  private void sendConfirmationEmail(String name, String date, Long appointmentId, String... email) {
    try {
      String body = "Test EMAIL: Hola " + name + " Deseamos confirmar tu cita agendada para la fecha: <b>" + date + "</b> , confirmala en el siguiente enlace: <a href=\"" + frontEndUrl + "/confirmation/" + appointmentId + "\"> Confirmar </a>";
      emailService.sendEmail(email, "Confirmacion de cita WELLNESS TEST", body);
    } catch (Exception e) {
      logger.error("sendConfirmationEmail Error: " + e);
    }
  }
}
