document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll(".participante-delete").forEach(function (form) {
    form.addEventListener("submit", function (event) {
      const input = form.querySelector('input[name="participantName"]');
      const participantName = input ? input.value : "este participante";
      if (!confirm("¿Estás seguro de que deseas borrar al participante \"" + participantName + "\" y todas sus respuestas de la prueba?")) {
        event.preventDefault();
      }
    });
  });
});
