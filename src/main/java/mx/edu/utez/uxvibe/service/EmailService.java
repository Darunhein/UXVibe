package mx.edu.utez.uxvibe.service;

import jakarta.mail.Message;
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

  public static boolean sendPasswordResetLink(String to, String resetUrl) {
    String host = getSetting("SMTP_HOST", "mail.smtp.host");
    String port = getSetting("SMTP_PORT", "mail.smtp.port");
    String user = getSetting("SMTP_USER", "mail.smtp.user");
    String pass = getSetting("SMTP_PASS", "mail.smtp.pass");
    String from = getSetting("SMTP_FROM", "mail.smtp.from");

    if (host == null || port == null || user == null || pass == null || from == null) {
      LOGGER.info("==========================================================================");
      LOGGER.info("[DEV MODE / SMTP UNCONFIGURED] Password Reset Link for " + to + ":");
      LOGGER.info(resetUrl);
      LOGGER.info("==========================================================================");
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
      message.setSubject("Recuperación de Contraseña - UX Vibe");

      String htmlContent = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);\">" +
          "<div style=\"background: linear-gradient(135deg, #3f5669 0%, #244f6d 100%); padding: 32px 24px; text-align: center;\">" +
          "<h1 style=\"color: #ffffff; margin: 0; font-size: 26px; font-weight: 700; letter-spacing: 0.5px;\">UX Vibe</h1>" +
          "<p style=\"color: #e2e8f0; margin: 6px 0 0 0; font-size: 14px;\">Plataforma de Evaluación y Pruebas UX</p>" +
          "</div>" +
          "<div style=\"padding: 36px 28px;\">" +
          "<h2 style=\"color: #1f2937; font-size: 20px; font-weight: 600; margin-top: 0; margin-bottom: 16px;\">Solicitud de Restablecimiento de Contraseña</h2>" +
          "<p style=\"color: #4b5563; font-size: 15px; line-height: 1.6; margin: 0 0 20px 0;\">Hola,</p>" +
          "<p style=\"color: #4b5563; font-size: 15px; line-height: 1.6; margin: 0 0 28px 0;\">Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en <strong>UX Vibe</strong>. Haz clic en el siguiente botón para continuar:</p>" +
          "<div style=\"text-align: center; margin: 32px 0;\">" +
          "<a href=\"" + resetUrl + "\" target=\"_blank\" style=\"display: inline-block; background-color: #3f5669; color: #ffffff; font-size: 16px; font-weight: 600; text-decoration: none; padding: 14px 32px; border-radius: 8px; box-shadow: 0 4px 6px rgba(63, 86, 105, 0.25);\">Restablecer Contraseña</a>" +
          "</div>" +
          "<p style=\"color: #6b7280; font-size: 13px; line-height: 1.5; margin: 24px 0 12px 0;\">Si el botón no funciona, copia y pega el siguiente enlace en tu navegador web:</p>" +
          "<p style=\"word-break: break-all; background-color: #f8fafc; padding: 12px; border-radius: 6px; border: 1px solid #e2e8f0; font-size: 12px; color: #244f6d; margin: 0 0 24px 0;\"><a href=\"" + resetUrl + "\" style=\"color: #244f6d; text-decoration: underline;\">" + resetUrl + "</a></p>" +
          "<div style=\"background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 12px 16px; border-radius: 4px; margin: 24px 0;\">" +
          "<p style=\"margin: 0; color: #92400e; font-size: 13px;\">⏳ <strong>Importante:</strong> Este enlace expirará en <strong>30 minutos</strong> por razones de seguridad.</p>" +
          "</div>" +
          "<p style=\"color: #9ca3af; font-size: 13px; line-height: 1.5; margin: 24px 0 0 0; border-top: 1px solid #f3f4f6; padding-top: 20px;\">Si no solicitaste este cambio, puedes ignorar este mensaje; tu contraseña actual continuará siendo segura.</p>" +
          "</div>" +
          "<div style=\"background-color: #f9fafb; padding: 16px; text-align: center; border-top: 1px solid #f3f4f6;\">" +
          "<p style=\"margin: 0; color: #9ca3af; font-size: 12px;\">© UX Vibe. Todos los derechos reservados.</p>" +
          "</div>" +
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
