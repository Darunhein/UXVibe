package mx.edu.utez.uxvibe.control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(value = "/participant-identity")
public class ParticipantIdentityServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    String participantName = req.getParameter("participantName");
    if (participantName != null) {
      participantName = participantName.trim();
    }
    if (participantName == null || participantName.isEmpty()) {
      participantName = "Participante";
    }

    session.setAttribute("currentParticipantName", participantName);
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
