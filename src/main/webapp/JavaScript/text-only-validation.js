document.addEventListener("DOMContentLoaded", function () {
  const allowedPattern = /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/;
  const textFields = Array.from(
    document.querySelectorAll('input[data-text-only="true"], textarea[data-text-only="true"]')
  );

  const sanitizeValue = function (value) {
    return value.replace(/[^A-Za-zÁÉÍÓÚáéíóúÑñ\s]/g, "");
  };

  textFields.forEach(function (field) {
    field.setAttribute("pattern", allowedPattern.source);
    field.setAttribute("title", "Solo se permiten letras y espacios");

    field.addEventListener("input", function () {
      const sanitized = sanitizeValue(this.value);
      if (this.value !== sanitized) {
        this.value = sanitized;
      }
    });

    field.addEventListener("paste", function (event) {
      event.preventDefault();
      const clipboardText = (
        event.clipboardData || window.clipboardData
      ).getData("text");
      const sanitized = sanitizeValue(clipboardText);
      document.execCommand("insertText", false, sanitized);
    });
  });
});
