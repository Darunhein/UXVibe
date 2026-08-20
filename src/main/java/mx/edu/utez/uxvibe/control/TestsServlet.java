package mx.edu.utez.uxvibe.control;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import mx.edu.utez.uxvibe.model.TestItem;
import mx.edu.utez.uxvibe.model.UserAccount;
import mx.edu.utez.uxvibe.service.TestStore;

@WebServlet(value = "/tests")
public class TestsServlet extends HttpServlet {

  private static final String TESTS_VIEW = "/WEB-INF/views/tests.jsp";
  private static final String CURRENT_USER_ATTR = "currentUser";

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    HttpSession session = req.getSession(false);
    if (session == null || session.getAttribute(CURRENT_USER_ATTR) == null) {
      try {
        resp.sendRedirect(req.getContextPath() + "/login");
      } catch (IOException e) {
        try {
          resp.sendError(
              HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
              "No se pudo redirigir al inicio de sesión.");
        } catch (IOException ignored) {
          // Fall through since the response is already in a failed state.
        }
      }
      return;
    }

    UserAccount account = (UserAccount) session.getAttribute(CURRENT_USER_ATTR);
    List<TestItem> allTests = TestStore.getInstance().listByUser(
        account.getEmail());
    int totalTests = allTests.size();
    int totalPages = PaginationSupport.countPages(
        totalTests,
        PaginationSupport.PAGE_SIZE);
    int currentPage = PaginationSupport.resolvePage(
        req.getParameter("page"),
        totalPages);

    req.setAttribute(
        "tests",
        PaginationSupport.paginate(
            allTests,
            currentPage,
            PaginationSupport.PAGE_SIZE));
    req.setAttribute("currentPage", currentPage);
    req.setAttribute("totalPages", totalPages);
    req.setAttribute("totalTests", totalTests);
    try {
      req.getRequestDispatcher(TESTS_VIEW).forward(req, resp);
    } catch (ServletException | IOException e) {
      try {
        resp.sendError(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "No se pudo mostrar la página de pruebas.");
      } catch (IOException ignored) {
        // Fall through since the response is already in a failed state.
      }
    }
  }
}
