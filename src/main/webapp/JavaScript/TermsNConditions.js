document.addEventListener("DOMContentLoaded", function () {
  const checkbox = document.getElementById("acceptanceCheckbox");
  const btnComenzar = document.getElementById("btnComenzar");

  if (!checkbox || !btnComenzar) {
    return;
  }

  function updateState() {
    if (checkbox.checked) {
      btnComenzar.classList.remove("disabled");
      btnComenzar.removeAttribute("aria-disabled");
    } else {
      btnComenzar.classList.add("disabled");
      btnComenzar.setAttribute("aria-disabled", "true");
    }
  }

  btnComenzar.addEventListener("click", function (e) {
    if (!checkbox.checked) {
      e.preventDefault();
      checkbox.focus();
      alert("Debes aceptar los términos y condiciones antes de comenzar la prueba.");
    }
  });

  checkbox.addEventListener("change", updateState);
  updateState();
});