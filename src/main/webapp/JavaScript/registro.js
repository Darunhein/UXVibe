document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll("[data-password-toggle]").forEach(function (toggleButton) {
    const inputSelector = toggleButton.getAttribute("data-target");
    const fieldLabel = toggleButton.getAttribute("data-field-label") || "contraseña";
    const input = inputSelector ? document.querySelector(inputSelector) : null;
    if (!input) {
      return;
    }

    toggleButton.addEventListener("click", function () {
      const isHidden = input.type === "password";
      input.type = isHidden ? "text" : "password";
      toggleButton.setAttribute("aria-pressed", String(isHidden));
      toggleButton.setAttribute("aria-label", (isHidden ? "Ocultar " : "Mostrar ") + fieldLabel);
    });
  });

  const form = document.getElementById("registroForm");
  if (form) {
    form.addEventListener("submit", function (event) {
      const password = document.getElementById("contrasena");
      const confirmPassword = document.getElementById("confirmar-contrasena");
      if (password && confirmPassword) {
        if (password.value.length < 8) {
          event.preventDefault();
          alert("La contraseña debe tener al menos 8 caracteres.");
          password.focus();
          return;
        }
        if (password.value !== confirmPassword.value) {
          event.preventDefault();
          alert("Las contraseñas no coinciden. Por favor verifícalas.");
          confirmPassword.focus();
        }
      }
    });
  }
});
