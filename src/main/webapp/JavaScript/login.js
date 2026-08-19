document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("login-password");
  const toggleButton = document.getElementById("login-password-toggle");

  if (input && toggleButton) {
    toggleButton.addEventListener("click", function (event) {
      event.preventDefault();
      const isHidden = input.type === "password";
      input.type = isHidden ? "text" : "password";
      toggleButton.setAttribute("aria-pressed", String(isHidden));
      toggleButton.setAttribute("aria-label", (isHidden ? "Ocultar" : "Mostrar") + " contraseña");
      input.focus();
    });
  }

  const form = document.getElementById("loginForm");
  if (form) {
    form.addEventListener("submit", function (event) {
      const email = document.getElementById("login-email");
      if (email && (!email.value.trim() || !email.value.includes("@"))) {
        event.preventDefault();
        alert("Por favor, ingresa un correo electrónico válido.");
        email.focus();
      }
    });
  }
});
