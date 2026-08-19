document.addEventListener("DOMContentLoaded", function () {
  document.querySelectorAll(".test-section__action-form--delete").forEach(function (form) {
    form.addEventListener("submit", function (event) {
      const input = form.querySelector('input[name="testName"]');
      const testName = input ? input.value : "esta prueba";
      if (!confirm("¿Estás seguro de que deseas borrar la prueba \"" + testName + "\"? Esta acción eliminará permanentemente todos sus participantes y respuestas asociadas.")) {
        event.preventDefault();
      }
    });
  });
});
