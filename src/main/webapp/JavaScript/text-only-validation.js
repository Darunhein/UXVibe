document.addEventListener("DOMContentLoaded", function () {
  const allowedPattern = /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]+$/;
  const textFields = Array.from(
    document.querySelectorAll('input[type="text"], textarea'),
  ).filter(function (field) {
    return !field.name || field.name !== "systemLink";
  });

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

    field.addEventListener("keydown", function (event) {
      const key = event.key;
      const allowedKeys = [
        "Backspace",
        "Tab",
        "ArrowLeft",
        "ArrowRight",
        "ArrowUp",
        "ArrowDown",
        "Delete",
        "Enter",
        "Escape",
        " ",
      ];
      if (allowedKeys.includes(key)) {
        return;
      }

      if (/^[A-Za-zÁÉÍÓÚáéíóúÑñ]$/.test(key)) {
        return;
      }

      event.preventDefault();
    });
  });

  const forms = Array.from(document.querySelectorAll("form"));
  forms.forEach(function (form) {
    form.addEventListener("submit", function (event) {
      textFields.forEach(function (field) {
        if (field.value && !allowedPattern.test(field.value)) {
          field.setAttribute("aria-invalid", "true");
          event.preventDefault();
          field.focus();
        }
      });
    });
  });
});
