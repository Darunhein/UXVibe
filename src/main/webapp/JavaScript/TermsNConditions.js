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

    e.preventDefault();

    const contextPath = document.body.getAttribute("data-context-path") || "";
    const recordingUrl = contextPath + "/test-recording";

    // 1. Open the system link in a new tab if provided
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

    // 2. Open Test Recording as a script-opened window (this enables programmatic window.close() later!)
    try {
      window.open(recordingUrl, "_blank");
    } catch (err) {
      console.warn("Could not open recording window:", err);
    }

    // 3. Navigate the main window directly into the first questionnaire
    window.location.href = contextPath + "/cuestionario-sb-1";
  });

  checkbox.addEventListener("change", updateState);
  updateState();
});