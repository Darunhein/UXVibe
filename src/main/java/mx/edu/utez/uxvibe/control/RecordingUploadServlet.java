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
@jakarta.servlet.annotation.MultipartConfig(
    maxFileSize = 20 * 1024 * 1024,
    maxRequestSize = 21 * 1024 * 1024,
    fileSizeThreshold = 1024 * 1024)
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
      participantName = mx.edu.utez.uxvibe.util.ParticipantIds.newFallbackName();
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
    } catch (IllegalStateException e) {
      resp.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
      resp.getWriter().write("El archivo de audio supera el límite de 20 MB");
      return;
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (audioBase64 == null) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      resp.getWriter().write("No se recibió archivo de audio");
      return;
    }

    String recordingType = req.getParameter("recordingType");
    if (recordingType == null || recordingType.isBlank()) {
      if (fileName != null && fileName.toLowerCase().contains("mic-test")) {
        recordingType = mx.edu.utez.uxvibe.util.QuestionNumbers.TYPE_MIC;
      } else {
        recordingType = mx.edu.utez.uxvibe.util.QuestionNumbers.TYPE_SESSION;
      }
    }

    boolean saved = ParticipantStore.getInstance().saveAudioAsset(
        account.getEmail(),
        testName,
        participantName,
        fileName,
        audioBase64,
        recordingType);

    if (!saved) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.getWriter().write("No se pudo guardar el audio en Oracle. Crea la prueba antes de grabar.");
      return;
    }

    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getWriter().write("OK");
  }
}
