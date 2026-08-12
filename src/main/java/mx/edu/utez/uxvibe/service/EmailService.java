package mx.edu.utez.uxvibe.service;

import java.util.Properties;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

  // Reads SMTP configuration from environment variables:
  // SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS, SMTP_FROM
  public static boolean sendPasswordResetEmail(String to, String newPassword) {
    String host = System.getenv("SMTP_HOST");
    String port = System.getenv("SMTP_PORT");
    String user = System.getenv("SMTP_USER");
    String pass = System.getenv("SMTP_PASS");
    String from = System.getenv("SMTP_FROM");

    if (host == null || port == null || user == null || pass == null || from == null) {
      // SMTP not configured
      return false;
    }

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);

    Session session = Session.getInstance(
      props,
      new jakarta.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(user, pass);
        }
      }
    );

    try {
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(from));
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
      message.setSubject("Recuperación de contraseña - UX Vibe");
      String text = "Se ha generado una nueva contraseña para tu cuenta:\n\n" + newPassword + "\n\nPor favor, inicia sesión y cambia tu contraseña después de iniciar sesión.";
      message.setText(text);
      Transport.send(message);
      return true;
    } catch (MessagingException ex) {
      ex.printStackTrace();
      return false;
    }
  }
}
