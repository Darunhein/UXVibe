document.addEventListener("DOMContentLoaded", function () {
  const btnExitTrigger = document.getElementById("btnExitTrigger");
  const evaluatorModal = document.getElementById("evaluatorModal");
  const btnCancelModal = document.getElementById("btnCancelModal");
  const evaluatorAudioPlayer = document.getElementById("evaluatorAudioPlayer");
  const noAudioWarning = document.getElementById("noAudioWarning");
  const evaluatorDownloadLink = document.getElementById("evaluatorDownloadLink");
  const btnSaveAll = document.getElementById("btnSaveAll");

  function configureAudioSource(src, filename) {
    if (!evaluatorAudioPlayer) return;
    evaluatorAudioPlayer.src = src;
    evaluatorAudioPlayer.volume = 1.0;
    evaluatorAudioPlayer.load();
    if (noAudioWarning) noAudioWarning.style.display = "none";
    if (evaluatorDownloadLink) {
      evaluatorDownloadLink.href = src;
      if (filename) evaluatorDownloadLink.download = filename;
      evaluatorDownloadLink.style.display = "inline";
    }
  }

  // Check if audio element already has a valid non-empty data / webm src from server
  if (evaluatorAudioPlayer && evaluatorAudioPlayer.src && evaluatorAudioPlayer.src.length > 30 && evaluatorAudioPlayer.src !== window.location.href) {
    evaluatorAudioPlayer.volume = 1.0;
    if (evaluatorDownloadLink) {
      evaluatorDownloadLink.href = evaluatorAudioPlayer.src;
      evaluatorDownloadLink.style.display = "inline";
    }
  } else {
    // Check client-side sessionStorage backup
    try {
      const localAudioBase64 = sessionStorage.getItem("uxvibe_audio_base64");
      const localMime = sessionStorage.getItem("uxvibe_audio_mimetype") || "audio/webm";
      const localFileName = sessionStorage.getItem("uxvibe_audio_filename") || "grabacion-sesion.webm";
      if (localAudioBase64) {
        const fullDataUri = "data:" + localMime + ";base64," + localAudioBase64;
        configureAudioSource(fullDataUri, localFileName);
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
        sessionStorage.removeItem("uxvibe_audio_mimetype");
        sessionStorage.removeItem("uxvibe-grabacion-tiempo");
      } catch (e) {}
    });
  }
});
