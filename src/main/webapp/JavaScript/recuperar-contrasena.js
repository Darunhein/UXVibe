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
});
