package mx.edu.utez.uxvibe.model;

import java.io.Serializable;

public class UserAccount implements Serializable {

  private String fullName;
  private String email;
  private String password;
  private String role = UserRole.EVALUATOR;

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return UserRole.isValid(role) ? role : UserRole.EVALUATOR;
  }

  public void setRole(String role) {
    this.role = UserRole.normalize(role);
  }
}
