document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("controlForm");
  if (!form) {
    return;
  }

  form.addEventListener("submit", function (event) {
    const rating = document.querySelector('input[name="control"]:checked');
    if (!rating) {
      event.preventDefault();
      alert("Por favor, selecciona una valoración del 1 al 9.");
    }
  });

  document.querySelectorAll('input[name="control"]').forEach(function (radio) {
    radio.addEventListener("change", function (e) {
      document.querySelectorAll(".scale-option").forEach(function (option) {
        option.classList.remove("selected");
      });
      const parent = e.target.closest(".scale-option");
      if (parent) {
        parent.classList.add("selected");
      }
    });
  });
});
