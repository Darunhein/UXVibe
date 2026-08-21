package mx.edu.utez.uxvibe.util;

public final class HtmlEscape {
  private HtmlEscape() {
  }

  public static String text(Object value) {
    if (value == null) {
      return "";
    }
    return String.valueOf(value)
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}
