document.addEventListener("DOMContentLoaded", function () {
  const btnExitTrigger = document.getElementById("btnExitTrigger");
  const evaluatorModal = document.getElementById("evaluatorModal");
  const btnCancelModal = document.getElementById("btnCancelModal");
  const evaluatorAudioPlayer = document.getElementById("evaluatorAudioPlayer");
  const noAudioWarning = document.getElementById("noAudioWarning");
  const evaluatorDownloadLink = document.getElementById("evaluatorDownloadLink");
  const btnSaveAll = document.getElementById("btnSaveAll");
  const btnRestartTest = document.getElementById("btnRestartTest");
  const btnDeleteTest = document.getElementById("btnDeleteTest");

  const recChannel = typeof BroadcastChannel !== "undefined" ? new BroadcastChannel("uxvibe_recording_channel") : null;

  function configureAudioSource(src, filename) {
    if (!evaluatorAudioPlayer) return;
    evaluatorAudioPlayer.src = src;
    evaluatorAudioPlayer.volume = 1.0;
    evaluatorAudioPlayer.load();
    if (noAudioWarning) {
      noAudioWarning.style.display = "none";
    }
    if (evaluatorDownloadLink) {
      evaluatorDownloadLink.href = src;
      if (filename) evaluatorDownloadLink.download = filename;
      evaluatorDownloadLink.style.display = "inline-block";
    }
  }

  // 1. Listen for audio completed event from recording tab via BroadcastChannel
  if (recChannel) {
    recChannel.onmessage = function (event) {
      if (event && event.data && event.data.type === "RECORDING_READY") {
        if (event.data.base64) {
          const mime = event.data.mimeType || "audio/webm";
          const dataUri = "data:" + mime + ";base64," + event.data.base64;
          configureAudioSource(dataUri, event.data.fileName || "grabacion-sesion.webm");
        }
      }
    };
  }

  // 2. Cross-tab storage fallback
  window.addEventListener("storage", function (e) {
    if (e.key === "uxvibe_audio_latest_base64" && e.newValue) {
      const mime = localStorage.getItem("uxvibe_audio_latest_mime") || "audio/webm";
      const name = localStorage.getItem("uxvibe_audio_latest_filename") || "grabacion-sesion.webm";
      const dataUri = "data:" + mime + ";base64," + e.newValue;
      configureAudioSource(dataUri, name);
    }
  });

  // 3. Initial check on load: server src, sessionStorage or localStorage
  if (evaluatorAudioPlayer && evaluatorAudioPlayer.src && evaluatorAudioPlayer.src.length > 30 && evaluatorAudioPlayer.src !== window.location.href) {
    evaluatorAudioPlayer.volume = 1.0;
    if (noAudioWarning) noAudioWarning.style.display = "none";
    if (evaluatorDownloadLink) {
      evaluatorDownloadLink.href = evaluatorAudioPlayer.src;
      evaluatorDownloadLink.style.display = "inline-block";
    }
  } else {
    try {
      const localAudioBase64 = sessionStorage.getItem("uxvibe_audio_base64") || localStorage.getItem("uxvibe_audio_latest_base64");
      const localMime = sessionStorage.getItem("uxvibe_audio_mimetype") || localStorage.getItem("uxvibe_audio_latest_mime") || "audio/webm";
      const localFileName = sessionStorage.getItem("uxvibe_audio_filename") || localStorage.getItem("uxvibe_audio_latest_filename") || "grabacion-sesion.webm";
      if (localAudioBase64) {
        const fullDataUri = "data:" + localMime + ";base64," + localAudioBase64;
        configureAudioSource(fullDataUri, localFileName);
      }
    } catch (e) { }
  }

  // 4. Trigger modal and prompt background recording tab to deliver audio
  if (btnExitTrigger && evaluatorModal) {
    btnExitTrigger.addEventListener("click", function () {
      if (recChannel) {
        if (noAudioWarning && (!evaluatorAudioPlayer.src || evaluatorAudioPlayer.src === window.location.href)) {
          noAudioWarning.textContent = "⏳ Obteniendo grabación de audio de la sesión...";
          noAudioWarning.style.color = "#2563eb";
          noAudioWarning.style.display = "block";
        }
        recChannel.postMessage({ type: "STOP_RECORDING_AND_UPLOAD" });
      }

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

  function clearSessionData() {
    try {
      sessionStorage.removeItem("uxvibe_audio_base64");
      sessionStorage.removeItem("uxvibe_audio_filename");
      sessionStorage.removeItem("uxvibe_audio_mimetype");
      sessionStorage.removeItem("uxvibe-grabacion-tiempo");
      localStorage.removeItem("uxvibe_audio_latest_base64");
      localStorage.removeItem("uxvibe_audio_latest_filename");
      localStorage.removeItem("uxvibe_audio_latest_mime");
    } catch (e) { }
  }

  function broadcastCloseRecordingTab() {
    if (recChannel) {
      recChannel.postMessage({ type: "CLOSE_RECORDING_TAB" });
    }
  }

  if (btnSaveAll) {
    btnSaveAll.addEventListener("click", function () {
      broadcastCloseRecordingTab();
      clearSessionData();
    });
  }

  if (btnRestartTest) {
    btnRestartTest.addEventListener("click", function () {
      broadcastCloseRecordingTab();
      clearSessionData();
    });
  }

  if (btnDeleteTest) {
    btnDeleteTest.addEventListener("click", function () {
      broadcastCloseRecordingTab();
      clearSessionData();
    });
  }
});
