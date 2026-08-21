package mx.edu.utez.uxvibe;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import mx.edu.utez.uxvibe.service.PasswordResetStore;
import mx.edu.utez.uxvibe.service.UserStore;

@WebListener
public class SchemaStartupListener implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    PasswordResetStore.getInstance();
    UserStore.getInstance().upgradePlaintextPasswords();
  }
}
