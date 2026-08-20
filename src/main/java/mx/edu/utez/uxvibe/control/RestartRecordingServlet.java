package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@WebServlet(value = "/restart-recording")
public class RestartRecordingServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    HttpSession session = req.getSession(false);
    if (session != null) {
      session.setAttribute("currentTestStartedAt", LocalDateTime.now(ZoneId.of("America/Mexico_City")));
      session.setAttribute("currentTestCompletionRecorded", Boolean.FALSE);
    }

    try {
      resp.sendRedirect(req.getContextPath() + "/test-recording");
    } catch (IOException e) {
      resp.sendError(
          HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          "No se pudo redirigir a la grabación.");
    }
  }
}
