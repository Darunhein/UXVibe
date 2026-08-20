package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.ParticipantStore;

@WebServlet(value = "/recording-upload")
@jakarta.servlet.annotation.MultipartConfig
public class RecordingUploadServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute("currentUser");
    String testName = (String) session.getAttribute("currentTestName");
    String participantName = (String) session.getAttribute(
        "currentParticipantName");
    if (participantName == null || participantName.trim().isEmpty()) {
      participantName = "Participante " + (System.currentTimeMillis() % 1000);
    }

    String fileName = req.getParameter("fileName");
    String audioBase64 = null;

    try {
      jakarta.servlet.http.Part filePart = req.getPart("file");
      if (filePart != null && filePart.getSize() > 0) {
        if (fileName == null || fileName.trim().isEmpty()) {
          String submitted = filePart.getSubmittedFileName();
          if (submitted != null)
            fileName = submitted;
        }
        try (java.io.InputStream is = filePart.getInputStream()) {
          byte[] bytes = is.readAllBytes();
          audioBase64 = java.util.Base64.getEncoder().encodeToString(bytes);
        }
      } else {
        // fallback to form field (for older clients)
        String audioUrl = req.getParameter("audioUrl");
        if (audioUrl != null && audioUrl.startsWith("data:")) {
          int comma = audioUrl.indexOf(',');
          if (comma >= 0)
            audioBase64 = audioUrl.substring(comma + 1);
        } else
          audioBase64 = audioUrl;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (audioBase64 == null) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().write("No se recibió archivo de audio");
      return;
    }

    ParticipantStore.getInstance().saveAudioAsset(
        account.getEmail(),
        testName,
        participantName,
        fileName,
        audioBase64);

    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().write("OK");
  }
}
