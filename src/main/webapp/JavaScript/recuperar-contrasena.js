document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("recoverForm");
  if (form) {
    form.addEventListener("submit", function (event) {
      const email = document.getElementById("recuperar-email");
      if (email && (!email.value.trim() || !email.value.includes("@") || !email.value.includes("."))) {
        event.preventDefault();
        alert("Por favor, ingresa un correo electrónico válido para recuperar tu contraseña.");
        email.focus();
      }
    });
  }

  // Cross-tab Synchronization: Listen for password reset takeover or completion
  const authChannel = typeof BroadcastChannel !== "undefined" ? new BroadcastChannel("uxvibe_auth_channel") : null;

  function handleAuthEvent(data) {
    if (!data) return;

    // 1. Takeover: If user opened the reset link in email, navigate THIS existing tab to the reset URL
    if (data.type === "TAKEOVER_RESET_PAGE" && data.targetUrl) {
      if (authChannel) {
        authChannel.postMessage({ type: "TAKEOVER_ACKNOWLEDGED" });
      }
      window.location.replace(data.targetUrl);
      return;
    }

    // 2. Success: If password was reset, return this tab to /login
    if (data.type === "PASSWORD_RESET_SUCCESS") {
      const currentUrl = window.location.href;
      const baseUrl = currentUrl.substring(0, currentUrl.lastIndexOf("/"));
      window.location.replace(baseUrl + "/login");
    }
  }

  try {
    if (authChannel) {
      authChannel.onmessage = function (event) {
        if (event && event.data) {
          handleAuthEvent(event.data);
        }
      };
    }

    window.addEventListener("storage", function (event) {
      if (event.key === "uxvibe_takeover_event" && event.newValue) {
        try {
          handleAuthEvent(JSON.parse(event.newValue));
        } catch (e) {}
      } else if (event.key === "uxvibe_auth_event" && event.newValue) {
        try {
          handleAuthEvent(JSON.parse(event.newValue));
        } catch (e) {}
      }
    });
  } catch (err) {}
});
