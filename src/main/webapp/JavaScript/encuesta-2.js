document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("surveyForm2");
  if (!form) {
    return;
  }

  const questions = ["q6", "q7", "q8", "q9", "q10"];

  form.addEventListener("submit", function (event) {
    for (let i = 0; i < questions.length; i++) {
      const selected = form.querySelector('input[name="' + questions[i] + '"]:checked');
      if (!selected) {
        event.preventDefault();
        alert("Por favor, responde todas las preguntas de la sección (pregunta " + (i + 6) + " pendiente).");
        return;
      }
    }
  });

  document.querySelectorAll('.scale-option input[type="radio"]').forEach(function (radio) {
    radio.addEventListener("change", function () {
      const parent = this.closest(".scale-options");
      if (parent) {
        parent.querySelectorAll(".scale-option").forEach(function (option) {
          option.classList.remove("selected");
        });
      }
      const optionContainer = this.closest(".scale-option");
      if (optionContainer) {
        optionContainer.classList.add("selected");
      }
    });
  });
});