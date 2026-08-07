package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.TestStore;

@WebServlet(value = "/create-test")
public class CreateTestServlet extends HttpServlet {

  private static final String CREATE_TEST_VIEW =
    "/WEB-INF/views/create-test.jsp";
  private static final String CURRENT_USER_ATTR = "currentUser";
  private static final String ERROR_MESSAGE_ATTR = "errorMessage";
  private static final String TEST_NAME_PARAM = "testName";
  private static final String DESCRIPTION_PARAM = "description";
  private static final String SYSTEM_LINK_PARAM = "systemLink";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    if (!isLoggedIn(req)) {
      redirectToLogin(req, resp);
      return;
    }
    forwardToCreateTest(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute(CURRENT_USER_ATTR) == null) {
      redirectToLogin(req, resp);
      return;
    }

    String testName = req.getParameter(TEST_NAME_PARAM);
    String description = req.getParameter(DESCRIPTION_PARAM);
    String systemLink = req.getParameter(SYSTEM_LINK_PARAM);
    if (isBlank(testName)) {
      req.setAttribute(ERROR_MESSAGE_ATTR, "Ingresa el nombre de la prueba.");
      req.setAttribute(TEST_NAME_PARAM, testName);
      req.setAttribute(DESCRIPTION_PARAM, description);
      req.setAttribute(SYSTEM_LINK_PARAM, systemLink);
      forwardToCreateTest(req, resp);
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute(CURRENT_USER_ATTR);
    TestStore.getInstance().createTest(
      account.getEmail(),
      testName,
      description,
      systemLink
    );
    redirectToTests(req, resp);
  }

  private void redirectToLogin(
    HttpServletRequest req,
    HttpServletResponse resp
  ) {
    try {
      resp.sendRedirect(req.getContextPath() + "/login");
    } catch (IOException e) {
      writeError(resp, "No se pudo redirigir al inicio de sesión.");
    }
  }

  private void forwardToCreateTest(
    HttpServletRequest req,
    HttpServletResponse resp
  ) {
    try {
      req.getRequestDispatcher(CREATE_TEST_VIEW).forward(req, resp);
    } catch (ServletException | IOException e) {
      writeError(resp, "No se pudo mostrar la página de crear prueba.");
    }
  }

  private void redirectToTests(
    HttpServletRequest req,
    HttpServletResponse resp
  ) {
    try {
      resp.sendRedirect(req.getContextPath() + "/tests");
    } catch (IOException e) {
      writeError(resp, "No se pudo redirigir a la lista de pruebas.");
    }
  }

  private void writeError(HttpServletResponse resp, String message) {
    try {
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    } catch (IOException ignored) {
      // Fall through since the response is already in a failed state.
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private boolean isLoggedIn(HttpServletRequest req) {
    HttpSession session = req.getSession(false);
    return session != null && session.getAttribute(CURRENT_USER_ATTR) != null;
  }
}
