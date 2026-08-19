package mx.edu.utez.uxvibe.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailService {

  private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

  public static boolean isConfigured() {
    String host = getSetting("SMTP_HOST", "mail.smtp.host");
    String port = getSetting("SMTP_PORT", "mail.smtp.port");
    String user = getSetting("SMTP_USER", "mail.smtp.user");
    String pass = getSetting("SMTP_PASS", "mail.smtp.pass");
    String from = getSetting("SMTP_FROM", "mail.smtp.from");

    return host != null && !host.isBlank() &&
           port != null && !port.isBlank() &&
           user != null && !user.isBlank() &&
           pass != null && !pass.isBlank() &&
           from != null && !from.isBlank();
  }

  public static boolean sendPasswordResetEmail(String to, String newPassword) {
    String host = getSetting("SMTP_HOST", "mail.smtp.host");
    String port = getSetting("SMTP_PORT", "mail.smtp.port");
    String user = getSetting("SMTP_USER", "mail.smtp.user");
    String pass = getSetting("SMTP_PASS", "mail.smtp.pass");
    String from = getSetting("SMTP_FROM", "mail.smtp.from");

    if (host == null || port == null || user == null || pass == null || from == null) {
      LOGGER.info("SMTP configuration not fully defined. Password reset generated: " + newPassword + " for " + to);
      return false;
    }

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "false");
    props.put("mail.smtp.host", host.trim());
    props.put("mail.smtp.port", port.trim());
    props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
    props.put("mail.smtp.ssl.trust", host.trim());

    Session session = Session.getInstance(
      props,
      new jakarta.mail.Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(user.trim(), pass.trim());
        }
      }
    );

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(from.trim(), "UX Vibe"));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.trim()));
      message.setSubject("Recuperación de contraseña - UX Vibe");
      
      String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;'>" +
          "<h2 style='color: #333;'>Recuperación de Contraseña - UX Vibe</h2>" +
          "<p>Hola,</p>" +
          "<p>Has solicitado restablecer tu contraseña para ingresar a la plataforma <strong>UX Vibe</strong>.</p>" +
          "<div style='background-color: #f4f6f9; padding: 15px; border-left: 4px solid #4a90e2; margin: 20px 0;'>" +
          "<p style='margin: 0; font-size: 14px; color: #666;'>Tu nueva contraseña temporal es:</p>" +
          "<p style='margin: 8px 0 0 0; font-size: 20px; font-weight: bold; letter-spacing: 2px; color: #222;'>" + newPassword + "</p>" +
          "</div>" +
          "<p>Puedes copiar y pegar esta contraseña directamente en el formulario de inicio de sesión.</p>" +
          "<p style='color: #888; font-size: 12px; margin-top: 30px;'>Si no solicitaste este cambio, por favor ignora este mensaje.</p>" +
          "</div>";

      message.setContent(htmlContent, "text/html; charset=UTF-8");
      Transport.send(message);
      LOGGER.info("Password reset email sent successfully to " + to);
      return true;
    } catch (Exception ex) {
      LOGGER.log(Level.SEVERE, "Failed to send password reset email to " + to, ex);
      return false;
    }
  }

  private static String getSetting(String envKey, String propKey) {
    String val = System.getenv(envKey);
    if (val != null && !val.isBlank()) {
      return val;
    }
    val = System.getProperty(propKey);
    if (val != null && !val.isBlank()) {
      return val;
    }
    return null;
  }
}
