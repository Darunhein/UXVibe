package mx.edu.utez.uxvibe.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HtmlEscapeTest {

  @Test
  void escapesMarkupAndQuotes() {
    assertEquals("", HtmlEscape.text(null));
    assertEquals("ok", HtmlEscape.text("ok"));
    assertEquals("&lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt;", HtmlEscape.text("<script>alert('x')</script>"));
    assertEquals("&quot;quoted&quot;", HtmlEscape.text("\"quoted\""));
  }
}
