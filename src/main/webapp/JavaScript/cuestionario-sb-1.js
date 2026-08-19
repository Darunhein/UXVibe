document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("questionnaireForm");
  if (!form) {
    return;
  }

  form.addEventListener("submit", function (event) {
    const fullName = document.getElementById("fullName");
    const age = document.getElementById("age");
    const gender = document.querySelector('input[name="gender"]:checked');
    const education = document.querySelector('input[name="education"]:checked');

    if (!fullName || !fullName.value.trim()) {
      event.preventDefault();
      alert("Por favor, ingresa tu nombre completo.");
      fullName.focus();
      return;
    }

    if (!age || !age.value) {
      event.preventDefault();
      alert("Por favor, ingresa tu edad.");
      age.focus();
      return;
    }

    const ageNum = parseInt(age.value, 10);
    if (isNaN(ageNum) || ageNum < 3 || ageNum > 120) {
      event.preventDefault();
      alert("Por favor, ingresa una edad válida (mínimo 3 años).");
      age.focus();
      return;
    }

    if (!gender) {
      event.preventDefault();
      alert("Por favor, selecciona tu sexo.");
      return;
    }

    if (!education) {
      event.preventDefault();
      alert("Por favor, selecciona tu nivel máximo de educación.");
      return;
    }
  });
});
