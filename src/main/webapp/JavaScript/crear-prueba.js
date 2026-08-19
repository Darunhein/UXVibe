document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("createTestForm");
  if (form) {
    form.addEventListener("submit", function (event) {
      const nameInput = document.getElementById("nombre-prueba");
      if (nameInput && !nameInput.value.trim()) {
        event.preventDefault();
        alert("Por favor, ingresa el nombre de la prueba.");
        nameInput.focus();
      }
    });
  }
});
