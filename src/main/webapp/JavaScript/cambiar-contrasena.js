document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("resetPasswordForm");
  const passwordInput = document.getElementById("reset-password");
  const confirmInput = document.getElementById("reset-confirm-password");
  const matchError = document.getElementById("password-match-error");

  const toggleNewBtn = document.getElementById("toggle-new-password");
  const toggleConfirmBtn = document.getElementById("toggle-confirm-password");

  function setupPasswordToggle(btn, input) {
    if (!btn || !input) return;
    btn.addEventListener("click", function () {
      const isPassword = input.type === "password";
      input.type = isPassword ? "text" : "password";
      btn.style.opacity = isPassword ? "1" : "0.65";
    });
  }

  setupPasswordToggle(toggleNewBtn, passwordInput);
  setupPasswordToggle(toggleConfirmBtn, confirmInput);

  function checkMatch() {
    if (!passwordInput || !confirmInput || !matchError) return true;
    if (confirmInput.value.length > 0 && passwordInput.value !== confirmInput.value) {
      matchError.style.display = "block";
      return false;
    } else {
      matchError.style.display = "none";
      return true;
    }
  }

  if (confirmInput) {
    confirmInput.addEventListener("input", checkMatch);
  }
  if (passwordInput) {
    passwordInput.addEventListener("input", checkMatch);
  }

  // Cross-tab Takeover: If another UXVibe tab is open (e.g. recover or login), notify it to take over
  const authChannel = typeof BroadcastChannel !== "undefined" ? new BroadcastChannel("uxvibe_auth_channel") : null;
  const currentFullUrl = window.location.href;

  try {
    if (authChannel) {
      // Send signal that user opened the reset password page
      authChannel.postMessage({
        type: "TAKEOVER_RESET_PAGE",
        targetUrl: currentFullUrl
      });

      // Listen for acknowledgement from existing tab
      authChannel.onmessage = function (event) {
        if (event && event.data && event.data.type === "TAKEOVER_ACKNOWLEDGED") {
          // The existing tab has successfully redirected itself to this reset URL
          document.body.innerHTML = '<div style="display:flex;align-items:center;justify-content:center;height:100vh;font-family:Inter,sans-serif;color:#244f6d;text-align:center;padding:20px;"><div><h2>¡Pestaña sincronizada!</h2><p>El restablecimiento de contraseña se ha abierto en tu pestaña original de UX Vibe.</p><p style="color:#6b7280;font-size:14px;">Puedes cerrar esta ventana de forma segura.</p></div></div>';
          setTimeout(function () {
            window.close();
          }, 1500);
        }
      };
    }

    localStorage.setItem("uxvibe_takeover_event", JSON.stringify({
      type: "TAKEOVER_RESET_PAGE",
      targetUrl: currentFullUrl,
      time: Date.now()
    }));
  } catch (e) {}

  if (form) {
    form.addEventListener("submit", function (e) {
      if (!passwordInput || !confirmInput) return;

      const pwd = passwordInput.value;
      const confirm = confirmInput.value;

      if (pwd.length < 8) {
        e.preventDefault();
        alert("La contraseña debe tener al menos 8 caracteres.");
        passwordInput.focus();
        return;
      }

      if (pwd !== confirm) {
        e.preventDefault();
        if (matchError) matchError.style.display = "block";
        confirmInput.focus();
        return;
      }

      // Signal other open UXVibe tabs that password reset is taking place
      try {
        if (authChannel) {
          authChannel.postMessage({ type: "PASSWORD_RESET_SUCCESS" });
        }
        localStorage.setItem("uxvibe_auth_event", JSON.stringify({
          type: "PASSWORD_RESET_SUCCESS",
          time: Date.now()
        }));
      } catch (err) {}
    });
  }
});
