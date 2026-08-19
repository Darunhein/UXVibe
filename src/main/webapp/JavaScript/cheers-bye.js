document.addEventListener("DOMContentLoaded", function () {
  const btnExitTrigger = document.getElementById("btnExitTrigger");
  const evaluatorModal = document.getElementById("evaluatorModal");
  const btnCancelModal = document.getElementById("btnCancelModal");
  const evaluatorAudioPlayer = document.getElementById("evaluatorAudioPlayer");
  const noAudioWarning = document.getElementById("noAudioWarning");
  const btnSaveAll = document.getElementById("btnSaveAll");

  // If server didn't have audio url yet, check client-side sessionStorage
  if (evaluatorAudioPlayer && (!evaluatorAudioPlayer.src || evaluatorAudioPlayer.src === window.location.href)) {
    try {
      const localAudioBase64 = sessionStorage.getItem("uxvibe_audio_base64");
      if (localAudioBase64) {
        evaluatorAudioPlayer.src = "data:audio/webm;base64," + localAudioBase64;
        if (noAudioWarning) noAudioWarning.style.display = "none";
      }
    } catch (e) {}
  }

  if (btnExitTrigger && evaluatorModal) {
    btnExitTrigger.addEventListener("click", function () {
      evaluatorModal.classList.add("active");
      evaluatorModal.setAttribute("aria-hidden", "false");
    });
  }

  if (btnCancelModal && evaluatorModal) {
    btnCancelModal.addEventListener("click", function () {
      evaluatorModal.classList.remove("active");
      evaluatorModal.setAttribute("aria-hidden", "true");
    });
  }

  if (btnSaveAll) {
    btnSaveAll.addEventListener("click", function () {
      try {
        sessionStorage.removeItem("uxvibe_audio_base64");
        sessionStorage.removeItem("uxvibe_audio_filename");
        sessionStorage.removeItem("uxvibe-grabacion-tiempo");
      } catch (e) {}
    });
  }
});
