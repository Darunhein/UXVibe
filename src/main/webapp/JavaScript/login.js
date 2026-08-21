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

  // Cross-tab Synchronization: Listen for password reset completion in other tab
  try {
    function handleAuthEvent(data) {
      if (data && (data.type === "PASSWORD_RESET_SUCCESS" || data.type === "PASSWORD_RESET_SUBMITTED")) {
        // Automatically refresh or highlight login page
        window.location.reload();
      }
    }

    if (typeof BroadcastChannel !== "undefined") {
      const authChannel = new BroadcastChannel("uxvibe_auth_channel");
      authChannel.onmessage = function (event) {
        if (event && event.data) {
          handleAuthEvent(event.data);
        }
      };
    }

    window.addEventListener("storage", function (event) {
      if (event.key === "uxvibe_auth_event" && event.newValue) {
        try {
          const parsed = JSON.parse(event.newValue);
          handleAuthEvent(parsed);
        } catch (e) {}
      }
    });
  } catch (err) {}
});
