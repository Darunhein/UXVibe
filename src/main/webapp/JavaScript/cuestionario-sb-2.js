document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("questionnaireForm");
  if (!form) {
    return;
  }

  form.addEventListener("submit", function (event) {
    const stress = document.querySelector('input[name="stress"]:checked');
    const relaxation = document.querySelector('input[name="relaxation"]:checked');

    if (!stress) {
      event.preventDefault();
      alert("Por favor, responde con qué frecuencia te sientes estresado/a.");
      return;
    }

    if (!relaxation) {
      event.preventDefault();
      alert("Por favor, responde con qué frecuencia te sientes relajado/a.");
      return;
    }
  });
});
