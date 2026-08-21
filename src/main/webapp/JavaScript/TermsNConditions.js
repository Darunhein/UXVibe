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
      return;
    }

    const systemLink = btnComenzar.getAttribute("data-system-link");
    if (systemLink && systemLink.trim().length > 0) {
      let finalUrl = systemLink.trim();
      if (!/^https?:\/\//i.test(finalUrl)) {
        finalUrl = "https://" + finalUrl;
      }
      try {
        window.open(finalUrl, "_blank", "noopener,noreferrer");
      } catch (err) {
        console.warn("Could not open system link window:", err);
      }
    }
  });

  checkbox.addEventListener("change", updateState);
  updateState();
});