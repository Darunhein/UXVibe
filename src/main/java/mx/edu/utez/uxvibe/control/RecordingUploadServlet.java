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
      "currentParticipantName"
    );
    if (participantName == null || participantName.trim().isEmpty()) {
      participantName = "Participante " + (System.currentTimeMillis() % 1000);
    }
    String fileName = req.getParameter("fileName");
    String audioUrl = req.getParameter("audioUrl");

    ParticipantStore.getInstance().saveAudioAsset(
      account.getEmail(),
      testName,
      participantName,
      fileName,
      audioUrl
    );

    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
