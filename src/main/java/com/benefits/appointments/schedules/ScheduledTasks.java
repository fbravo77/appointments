package com.benefits.appointments.schedules;

import com.benefits.appointments.models.entities.Appointment;
import com.benefits.appointments.repositories.AppointmentRepository;
import com.benefits.appointments.services.EmailService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class ScheduledTasks {

  private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
  private final AppointmentRepository appointmentRepository;
  private final EmailService emailService;
  @Value("${front-end.url}")
  private String frontEndUrl;
  private static final int REMINDERS_DAY_INTERVAL = 3;
  private static final int CONFIRMATION_DAY_INTERVAL = 3;

  @Autowired
  public ScheduledTasks(AppointmentRepository appointmentRepository,
                        EmailService emailService) {
    this.appointmentRepository = appointmentRepository;
    this.emailService = emailService;
  }

  @Scheduled(cron = "0 0 8 * * *")
  public void reviewPendingAppointmentsAndSendReminder() {
    LocalDateTime now = LocalDateTime.now();
    List<Appointment> appointmentList = appointmentRepository.findByStartDateBetweenAndReminderSent(now,
        now.plusDays(REMINDERS_DAY_INTERVAL), false);
    processAppointments(appointmentList, "REMINDER", now, REMINDERS_DAY_INTERVAL);
    logger.info("reviewPendingAppointmentsAndSendReminder Task performed at {} emails sent: {}", now, appointmentList.size());
  }

  @Scheduled(cron = "0 0 8 * * *")
  public void reviewPendingAppointmentsAndSendConfirmation() {
    LocalDateTime now = LocalDateTime.now();
    List<Appointment> appointmentList = appointmentRepository.findByStartDateBetweenAndConfirmationSent(now,
        now.plusDays(CONFIRMATION_DAY_INTERVAL), false);
    processAppointments(appointmentList, "CONFIRMATION", now, CONFIRMATION_DAY_INTERVAL);
    logger.info("reviewPendingAppointmentsAndSendConfirmation Task performed at {} emails sent: {}", now, appointmentList.size());
  }

  //@Scheduled(cron = "0 0 8 * * *")
  //@Scheduled(fixedRate = 50000)
  public void changeToExpireAppointments() {
    LocalDateTime now = LocalDateTime.now();
    List<Appointment> appointmentList = appointmentRepository.findByStartDateBetweenOrderByStartDateDesc(now.minusDays(1),
        now);
    for(Appointment appointment : appointmentList){
      if(appointment.getStartDate().isBefore(now) && !appointment.isExpired()) {
        appointment.setExpired(true);
        appointmentRepository.save(appointment);
      }
    }
    logger.info("changeToExpireAppointments list size: {}", appointmentList.size());
  }

  private void processAppointments(List<Appointment> appointmentList, String taskType, LocalDateTime now, int interval) {
    for (Appointment appointment : appointmentList) {
      LocalDateTime appointmentDate = appointment.getStartDate();
      if (now.isBefore(appointmentDate) && Duration.between(now, appointmentDate).toDays() == interval) {
        String personalEmail = appointment.getPatient().getPersonalEmail();
        String workEmail = appointment.getPatient().getWorkEmail();
        String name = appointment.getPatient().getPatient().getPreferredName();
        String date = appointment.getStartDate().toString();
        if ("CONFIRMATION".equals(taskType)) {
          sendConfirmationEmail(name, date, appointment.getId(), personalEmail, workEmail);
          appointment.setConfirmationSent(true);
        } else if ("REMINDER".equals(taskType)) {
          sendReminderEmail(name, date, personalEmail, workEmail);
          appointment.setReminderSent(true);
        }
        appointmentRepository.save(appointment);
      }
    }
  }

  private void sendReminderEmail(String name, String date, String... emails) {
    try {
      String body = "Test EMAIL: Hola " + name + " Te recordamos que tienes una cita agendada para la fecha: <b>" + date + " </b>";
      emailService.sendEmail(emails, "Recordatorio de cita WELLNESS TEST", body);
    } catch (Exception e) {
      logger.error("sendReminderEmail Error: {}", e.getMessage());
    }
  }

  private void sendConfirmationEmail(String name, String date, Long appointmentId, String... emails) {
    try {
      String body = "Test EMAIL: Hola " + name + " Deseamos confirmar tu cita agendada para la fecha: <b>" + date + "</b> , confirmala en el siguiente enlace: <a href=\"" + frontEndUrl + "/confirmation/" + appointmentId + "\"> Confirmar </a>";
      emailService.sendEmail(emails, "Confirmacion de cita WELLNESS TEST", body);
    } catch (Exception e) {
      logger.error("sendConfirmationEmail Error: {}", e.getMessage());
    }
  }
}
