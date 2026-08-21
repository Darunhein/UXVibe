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
    const surveyUrl = contextPath + "/cuestionario-sb-1";

    // 1. Open the external system link in a new tab if provided
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

    // 2. Open test recording in a child window so it can be programmatically closed later
    try {
      window.open(recordingUrl, "UXVibeRecorder", "width=800,height=600,menubar=no,toolbar=no,location=no,status=no");
    } catch (err) {
      console.warn("Popup blocked, navigating directly:", err);
    }

    // 3. Navigate the main tab into the first survey questionnaire
    window.location.href = surveyUrl;
  });

  checkbox.addEventListener("change", updateState);
  updateState();
});