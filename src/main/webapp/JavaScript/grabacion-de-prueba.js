(function () {
  const pauseDetails = document.getElementById("pauseDetails");
  const pauseBtn = document.getElementById("pauseBtn");
  const timerLabel = document.querySelector(".h2");
  const restartBtn = document.getElementById("restartBtn");
  const startSurveyLink = document.getElementById("startSurveyLink");
  const liveRecBadge = document.getElementById("liveRecBadge");
  const liveRecText = document.getElementById("liveRecText");
  const waveSpans = document.querySelectorAll("#waveCardInner .wave-wave");
  const contextPath = document.body.dataset.contextPath || "";
  const storageKey = "uxvibe-grabacion-tiempo";

  let timerSeconds = Number.parseInt(sessionStorage.getItem(storageKey) || "0", 10);
  let timerId = null;
  let mediaRecorder = null;
  let chunks = [];
  let stream = null;
  let isRecording = false;
  let isPaused = false;
  let recordedMimeType = "audio/webm";

  // Web Audio API for live waveform reactivity
  let audioContext = null;
  let analyser = null;
  let animFrameId = null;

  function getBestSupportedMimeType() {
    const candidateTypes = [
      "audio/webm;codecs=opus",
      "audio/webm",
      "audio/ogg;codecs=opus",
      "audio/ogg",
      "audio/mp4",
      "audio/aac",
    ];
    if (typeof MediaRecorder === "undefined") {
      return "";
    }
    for (let i = 0; i < candidateTypes.length; i++) {
      if (MediaRecorder.isTypeSupported(candidateTypes[i])) {
        return candidateTypes[i];
      }
    }
    return "";
  }

  function formatTime(totalSeconds) {
    if (isNaN(totalSeconds) || totalSeconds < 0) totalSeconds = 0;
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return String(minutes).padStart(2, "0") + ":" + String(seconds).padStart(2, "0");
  }

  function stopTimer() {
    if (timerId) {
      clearInterval(timerId);
      timerId = null;
    }
  }

  function startTimer() {
    stopTimer();
    timerId = setInterval(function () {
      if (!isPaused) {
        timerSeconds += 1;
        sessionStorage.setItem(storageKey, String(timerSeconds));
        if (timerLabel) {
          timerLabel.textContent = formatTime(timerSeconds);
        }
      }
    }, 1000);
  }

  function setupWaveAnalyser(micStream) {
    try {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (AudioCtx) {
        audioContext = new AudioCtx();
        if (audioContext.state === "suspended") {
          audioContext.resume().catch(function () { });
        }
        analyser = audioContext.createAnalyser();
        analyser.fftSize = 64;
        analyser.smoothingTimeConstant = 0.75;
        const source = audioContext.createMediaStreamSource(micStream);
        source.connect(analyser);
        animateLiveWaves();
      }
    } catch (e) {
      console.warn("Wave visualizer init note:", e);
    }
  }

  function animateLiveWaves() {
    if (!analyser || waveSpans.length === 0) {
      return;
    }

    const dataArray = new Uint8Array(analyser.frequencyBinCount);
    analyser.getByteFrequencyData(dataArray);

    let avg = 0;
    for (let i = 0; i < dataArray.length; i++) {
      avg += dataArray[i];
    }
    avg = avg / dataArray.length;
    const norm = Math.max(0.05, Math.min(1, avg / 180));

    const baseHeights = [18, 24, 13, 28, 20, 26, 14, 22];

    waveSpans.forEach(function (span, i) {
      const base = baseHeights[i % baseHeights.length];
      const factor = 0.5 + Math.sin(i * 0.7 + Date.now() * 0.008) * 0.5;
      const h = Math.max(8, Math.min(32, base * (0.6 + norm * 1.6 * factor)));
      span.style.height = h + "px";
    });

    animFrameId = requestAnimationFrame(animateLiveWaves);
  }

  function stopMediaTracks() {
    if (animFrameId) {
      cancelAnimationFrame(animFrameId);
      animFrameId = null;
    }
    if (audioContext && audioContext.state !== "closed") {
      audioContext.close().catch(function () { });
      audioContext = null;
      analyser = null;
    }
    if (stream) {
      stream.getTracks().forEach(function (track) {
        track.stop();
      });
      stream = null;
    }
  }

  function uploadRecording(blob, callback) {
    const fileName = "test-session-" + Date.now() + ".webm";
    const mimeType = blob.type || recordedMimeType || "audio/webm";
    const reader = new FileReader();
    reader.onloadend = function () {
      const fullDataUri = reader.result;
      const base64 = typeof fullDataUri === "string" && fullDataUri.indexOf(",") >= 0
        ? fullDataUri.split(",")[1]
        : "";

      try {
        sessionStorage.setItem("uxvibe_audio_base64", base64);
        sessionStorage.setItem("uxvibe_audio_filename", fileName);
        sessionStorage.setItem("uxvibe_audio_mimetype", mimeType);
        localStorage.setItem("uxvibe_audio_latest_base64", base64);
        localStorage.setItem("uxvibe_audio_latest_filename", fileName);
        localStorage.setItem("uxvibe_audio_latest_mime", mimeType);
      } catch (e) { }

      const formData = new FormData();
      formData.append("file", blob, fileName);
      formData.append("recordingType", "TEST_SESSION");
      const csrf = document.body.dataset.csrf || "";
      if (csrf) {
        formData.append("_csrf", csrf);
      }

      fetch(contextPath + "/recording-upload", {
        method: "POST",
        headers: csrf ? { "X-CSRF-Token": csrf } : {},
        body: formData
      })
        .then(function () {
          if (typeof callback === "function") callback(base64, fileName, mimeType);
        })
        .catch(function () {
          if (typeof callback === "function") callback(base64, fileName, mimeType);
        });
    };
    reader.readAsDataURL(blob);
  }

  function beginRecording() {
    if (isRecording) {
      return;
    }

    const mimeType = getBestSupportedMimeType();
    recordedMimeType = mimeType || "audio/webm";

    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
      const constraints = {
        audio: {
          echoCancellation: true,
          noiseSuppression: true,
          autoGainControl: true,
        },
      };

      navigator.mediaDevices
        .getUserMedia(constraints)
        .catch(function () {
          return navigator.mediaDevices.getUserMedia({ audio: true });
        })
        .then(function (micStream) {
          stream = micStream;
          chunks = [];

          try {
            mediaRecorder = mimeType
              ? new MediaRecorder(stream, { mimeType: mimeType })
              : new MediaRecorder(stream);
          } catch (e) {
            mediaRecorder = new MediaRecorder(stream);
          }

          mediaRecorder.ondataavailable = function (event) {
            if (event.data && event.data.size > 0) {
              chunks.push(event.data);
            }
          };

          mediaRecorder.onstop = function () {
            const blob = new Blob(chunks, { type: mediaRecorder.mimeType || recordedMimeType });
            uploadRecording(blob);
            stopMediaTracks();
          };

          // Periodic 500ms chunk collection ensures constant data availability
          mediaRecorder.start(500);
          isRecording = true;
          isPaused = false;
          startTimer();
          setupWaveAnalyser(micStream);
          updateBadgeState(true);
        })
        .catch(function (err) {
          console.warn("Microphone not available or permission denied:", err);
          if (liveRecBadge) {
            liveRecBadge.classList.add("is-error");
          }
          if (liveRecText) {
            liveRecText.textContent = "Micrófono no conectado (prueba sin audio)";
          }
          startTimer();
        });
    } else {
      startTimer();
    }
  }

  function updateBadgeState(recording) {
    if (!liveRecBadge) return;
    if (recording && !isPaused) {
      liveRecBadge.classList.remove("is-paused", "is-error");
      liveRecBadge.classList.add("is-active");
      if (liveRecText) liveRecText.textContent = "GRABANDO AUDIO EN VIVO";
    } else if (isPaused) {
      liveRecBadge.classList.remove("is-active");
      liveRecBadge.classList.add("is-paused");
      if (liveRecText) liveRecText.textContent = "GRABACIÓN EN PAUSA";
    }
  }

  function pauseRecording() {
    isPaused = true;
    stopTimer();
    if (mediaRecorder && mediaRecorder.state === "recording") {
      try {
        mediaRecorder.pause();
      } catch (e) { }
    }
    updateBadgeState(false);
  }

  function resumeRecording() {
    isPaused = false;
    startTimer();
    if (mediaRecorder && mediaRecorder.state === "paused") {
      try {
        mediaRecorder.resume();
      } catch (e) { }
    }
    updateBadgeState(true);
  }

  function stopRecording(callback) {
    if (mediaRecorder && mediaRecorder.state !== "inactive") {
      try {
        if (typeof mediaRecorder.requestData === "function") {
          mediaRecorder.requestData();
        }
      } catch (e) { }

      mediaRecorder.onstop = function () {
        const blob = new Blob(chunks, { type: mediaRecorder.mimeType || recordedMimeType });
        uploadRecording(blob, callback);
        stopMediaTracks();
      };
      mediaRecorder.stop();
      isRecording = false;
    } else {
      if (typeof callback === "function") callback();
    }
  }

  const playBtn = document.querySelector(".play-button");

  if (timerLabel) {
    timerLabel.textContent = formatTime(timerSeconds);
  }

  function handleSessionComplete() {
    stopTimer();
    stopRecording();
    stopMediaTracks();

    // Try closing the tab
    try {
      window.close();
    } catch (e) { }

    // Fallback: If browser prevents script window.close on top-level tabs, display a clean completed state
    setTimeout(function () {
      if (!window.closed) {
        document.body.innerHTML = `
          <div style="width:100vw;height:100vh;display:flex;flex-direction:column;align-items:center;justify-content:center;background:#bfc7cf;color:#fff;font-family:Inter,sans-serif;text-align:center;padding:20px;box-sizing:border-box;">
            <div style="background:#f6f5f3;color:#2c3e50;padding:44px 36px;border-radius:12px;box-shadow:0 12px 32px rgba(67,73,88,0.2);max-width:520px;width:100%;box-sizing:border-box;">
              <h2 style="margin-top:0;color:#5a7a8c;font-size:28px;font-weight:700;">Grabación Finalizada</h2>
              <p style="font-size:16px;color:#5d7b94;margin:16px 0 28px;line-height:1.4;">El audio de la sesión ha sido entregado y guardado con éxito. Ya puedes cerrar esta pestaña con seguridad.</p>
              <button id="btnFinishCloseTab" type="button" class="btn-primary" style="background:#5b7689;color:#ffffff;border:none;padding:14px 32px;border-radius:10px;font-size:16px;font-weight:600;cursor:pointer;font-family:Inter,sans-serif;box-shadow:0 3px 10px rgba(67,73,88,0.18);transition:all 0.22s cubic-bezier(0.4, 0, 0.2, 1);">Cerrar pestaña</button>
            </div>
          </div>
        `;
        const finishBtn = document.getElementById("btnFinishCloseTab");
        if (finishBtn) {
          finishBtn.addEventListener("mouseenter", function () {
            this.style.background = "#4a6b7c";
            this.style.transform = "translateY(-2px)";
            this.style.boxShadow = "0 6px 18px rgba(67, 73, 88, 0.28)";
          });
          finishBtn.addEventListener("mouseleave", function () {
            this.style.background = "#5b7689";
            this.style.transform = "translateY(0)";
            this.style.boxShadow = "0 3px 10px rgba(67, 73, 88, 0.18)";
          });
          finishBtn.addEventListener("mousedown", function () {
            this.style.transform = "translateY(0) scale(0.99)";
          });
          finishBtn.addEventListener("click", function () {
            window.close();
          });
        }
      }
    }, 200);
  }

  // Dual Signaling: Storage event fallback to close tab
  window.addEventListener("storage", function (e) {
    if (e.key === "uxvibe_close_recording_event" && e.newValue) {
      handleSessionComplete();
    }
  });

  // Cross-Tab Communication with Cheers Bye
  const recChannel = typeof BroadcastChannel !== "undefined" ? new BroadcastChannel("uxvibe_recording_channel") : null;
  if (recChannel) {
    recChannel.onmessage = function (event) {
      if (!event || !event.data) return;

      if (event.data.type === "STOP_RECORDING_AND_UPLOAD") {
        stopTimer();
        stopRecording(function (uploadedBase64, fileName, mimeType) {
          if (recChannel) {
            recChannel.postMessage({
              type: "RECORDING_READY",
              base64: uploadedBase64,
              fileName: fileName,
              mimeType: mimeType
            });
          }
        });
      } else if (event.data.type === "CLOSE_RECORDING_TAB") {
        handleSessionComplete();
      }
    };
  }

  // Start recording immediately on page load
  beginRecording();

  if (pauseDetails) {
    pauseDetails.addEventListener("toggle", function () {
      if (pauseDetails.open) {
        pauseRecording();
      } else {
        resumeRecording();
      }
    });
  }

  // Play button resumes recording & closes pause menu
  if (playBtn) {
    playBtn.addEventListener("click", function () {
      if (pauseDetails && pauseDetails.open) {
        pauseDetails.open = false;
      }
      resumeRecording();
    });
  }

  // When clicking "Empezar encuesta", unpause recording, update status, and open survey in new tab if not already open
  if (startSurveyLink) {
    startSurveyLink.addEventListener("click", function (event) {
      event.preventDefault();
      if (pauseDetails) {
        pauseDetails.open = false;
      }
      resumeRecording();

      if (liveRecText) {
        liveRecText.textContent = "GRABANDO EN SEGUNDO PLANO (ENCUESTA EN CURSO)";
      }

      window.open(contextPath + "/cuestionario-sb-1", "_blank");
    });
  }

  function broadcastCloseSurveyTab() {
    if (recChannel) {
      recChannel.postMessage({ type: "CLOSE_SURVEY_TAB" });
    }
    try {
      localStorage.setItem("uxvibe_close_survey_event", String(Date.now()));
    } catch (e) { }
  }

  if (restartBtn) {
    restartBtn.addEventListener("click", function (event) {
      event.preventDefault();
      broadcastCloseSurveyTab();
      sessionStorage.removeItem(storageKey);
      sessionStorage.removeItem("uxvibe_audio_base64");
      sessionStorage.removeItem("uxvibe_audio_filename");
      sessionStorage.removeItem("uxvibe_audio_mimetype");
      sessionStorage.removeItem("uxvibe-grabacion-tiempo");
      try {
        localStorage.removeItem("uxvibe_audio_latest_base64");
        localStorage.removeItem("uxvibe_audio_latest_filename");
        localStorage.removeItem("uxvibe_audio_latest_mime");
      } catch (e) { }

      stopRecording(function () {
        stopMediaTracks();
        window.location.href = contextPath + "/terms";
      });
    });
  }

  // Cancel test form submit handler
  const cancelTestForm = document.querySelector(".pause-menu__item-form");
  if (cancelTestForm) {
    cancelTestForm.addEventListener("submit", function () {
      broadcastCloseSurveyTab();
      sessionStorage.removeItem(storageKey);
      sessionStorage.removeItem("uxvibe_audio_base64");
      sessionStorage.removeItem("uxvibe_audio_filename");
      sessionStorage.removeItem("uxvibe_audio_mimetype");
      sessionStorage.removeItem("uxvibe-grabacion-tiempo");
      stopRecording();
      stopMediaTracks();
    });
  }

  window.addEventListener("beforeunload", function () {
    stopRecording();
    stopMediaTracks();
  });
})();
