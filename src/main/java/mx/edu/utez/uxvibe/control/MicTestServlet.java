package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/mic-test", "/prueba-microfono"})
public class MicTestServlet extends HttpServlet {

  private static final String MIC_TEST_VIEW = "/WEB-INF/views/prueba-microfono.jsp";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute("currentUser") == null) {
      resp.sendRedirect(req.getContextPath() + "/login");
      return;
    }

    req.getRequestDispatcher(MIC_TEST_VIEW).forward(req, resp);
  }
}
