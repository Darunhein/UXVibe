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
    });
  }
});
