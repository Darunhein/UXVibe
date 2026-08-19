(function () {
  const waveformContainer = document.querySelector(".waveform-container");
  const statusValue = document.querySelector(
    ".status-section .status-item:nth-of-type(1) .status-value"
  );
  const durationLabel = document.getElementById("test-duration");
  const deviceNameLabel = document.getElementById("device-name");
  const btnRecord = document.getElementById("btn-record");
  const btnPlay = document.getElementById("btn-play");
  const recordStatus = document.getElementById("record-status");
  const previewAudio = document.getElementById("preview-audio");

  let isRecording = false;
  let bars = [];
  let audioContext = null;
  let analyser = null;
  let stream = null;
  let animationFrame = null;
  let mediaRecorder = null;
  let recordedChunks = [];
  let recordingStart = 0;
  let recordingTimer = null;

  if (!waveformContainer || !statusValue) {
    return;
  }

  function buildWaveform() {
    const barWrapper = document.createElement("div");
    barWrapper.className = "waveform-bars";

    for (let index = 0; index < 36; index += 1) {
      const bar = document.createElement("span");
      bar.className = "wave-bar";
      bar.style.setProperty("--wave-height", "18%");
      barWrapper.appendChild(bar);
      bars.push(bar);
    }

    waveformContainer.innerHTML = "";
    waveformContainer.appendChild(barWrapper);
  }

  function setStatus(message, isActive) {
    statusValue.textContent = message;
    statusValue.classList.toggle("is-active", Boolean(isActive));
    statusValue.classList.toggle("is-error", !isActive && message.indexOf("No se pudo") !== -1);
  }

  function stopAudio() {
    if (animationFrame) {
      cancelAnimationFrame(animationFrame);
      animationFrame = null;
    }
    if (stream) {
      stream.getTracks().forEach(function (track) {
        track.stop();
      });
      stream = null;
    }
    if (audioContext && audioContext.state !== "closed") {
      audioContext.close().catch(function () {});
      audioContext = null;
      analyser = null;
    }
  }

  function animateWaveform() {
    if (!analyser) {
      return;
    }

    const dataArray = new Uint8Array(analyser.frequencyBinCount);
    analyser.getByteFrequencyData(dataArray);

    let average = 0;
    for (let index = 0; index < dataArray.length; index += 1) {
      average += dataArray[index];
    }
    average = average / dataArray.length;

    const normalized = Math.max(0.05, Math.min(1, average / 255));

    bars.forEach(function (bar, index) {
      const waveFactor = 0.45 + Math.sin(index * 0.28) * 0.28 + (index % 3) * 0.06;
      const targetHeight = 12 + normalized * 78 * waveFactor;
      bar.style.setProperty("--wave-height", targetHeight + "%");
      bar.style.height = targetHeight + "%";
    });

    animationFrame = requestAnimationFrame(animateWaveform);
  }

  function initMicStream() {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      setStatus("Tu navegador no admite acceso al micrófono", false);
      if (recordStatus) recordStatus.textContent = "Micrófono no soportado";
      return Promise.reject(new Error("no-media"));
    }

    return navigator.mediaDevices
      .getUserMedia({ audio: true })
      .then(function (micStream) {
        stream = micStream;
        audioContext = new (window.AudioContext || window.webkitAudioContext)();
        analyser = audioContext.createAnalyser();
        analyser.fftSize = 256;

        const source = audioContext.createMediaStreamSource(micStream);
        source.connect(analyser);

        if (micStream.getAudioTracks && micStream.getAudioTracks().length) {
          const label = micStream.getAudioTracks()[0].label || "Micrófono predeterminado";
          if (deviceNameLabel) deviceNameLabel.textContent = label;
        }

        setStatus("Micrófono activo", true);
        if (recordStatus) recordStatus.textContent = "Micrófono conectado y funcionando";
        animateWaveform();
        return micStream;
      })
      .catch(function () {
        setStatus("No se pudo acceder al micrófono. Permite el acceso para continuar.", false);
        if (recordStatus) recordStatus.textContent = "Acceso a micrófono denegado";
      });
  }

  function toggleRecording() {
    if (!isRecording) {
      startRecording();
    } else {
      stopRecording();
    }
  }

  function startRecording() {
    if (!stream) {
      initMicStream().then(function (s) {
        if (s) beginMediaRecorder();
      });
      return;
    }
    beginMediaRecorder();
  }

  function beginMediaRecorder() {
    recordedChunks = [];
    try {
      mediaRecorder = new MediaRecorder(stream);
    } catch (e) {
      setStatus("Grabación no soportada en este navegador", false);
      return;
    }

    mediaRecorder.ondataavailable = function (e) {
      if (e.data && e.data.size > 0) {
        recordedChunks.push(e.data);
      }
    };

    mediaRecorder.onstop = function () {
      const blob = new Blob(recordedChunks, { type: "audio/webm" });
      const url = URL.createObjectURL(blob);
      if (previewAudio) {
        previewAudio.src = url;
      }
      if (btnPlay) {
        btnPlay.disabled = false;
      }
      isRecording = false;
      if (btnRecord) {
        btnRecord.textContent = "Grabar de nuevo";
        btnRecord.classList.remove("is-recording");
      }
      if (recordStatus) {
        recordStatus.textContent = "Grabación lista para escuchar";
      }

      // Convert and send to server as backup
      const reader = new FileReader();
      reader.onloadend = function () {
        const base64 = reader.result.split(",")[1];
        const ctx = document.body.dataset.contextPath || "";
        const fileName = "mic-test-" + Date.now() + ".webm";
        fetch(ctx + "/recording-upload", {
          method: "POST",
          headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
          },
          body: "fileName=" + encodeURIComponent(fileName) + "&audioUrl=" + encodeURIComponent(base64),
        }).catch(function () {});
      };
      reader.readAsDataURL(blob);
    };

    mediaRecorder.start();
    isRecording = true;
    recordingStart = Date.now();
    startTimer();
    if (btnRecord) {
      btnRecord.textContent = "Detener grabación";
      btnRecord.classList.add("is-recording");
    }
    if (recordStatus) {
      recordStatus.textContent = "Grabando prueba de audio...";
    }
    setStatus("Grabando prueba de voz...", true);
  }

  function stopRecording() {
    if (mediaRecorder && mediaRecorder.state !== "inactive") {
      mediaRecorder.stop();
    }
    stopTimer();
    setStatus("Micrófono activo", true);
  }

  function startTimer() {
    stopTimer();
    recordingTimer = setInterval(function () {
      const elapsed = (Date.now() - recordingStart) / 1000;
      if (durationLabel) {
        durationLabel.textContent = formatDuration(elapsed);
      }
    }, 250);
  }

  function stopTimer() {
    if (recordingTimer) {
      clearInterval(recordingTimer);
      recordingTimer = null;
    }
  }

  function formatDuration(seconds) {
    const total = Math.floor(seconds);
    const mm = Math.floor(total / 60);
    const ss = total % 60;
    return String(mm).padStart(2, "0") + ":" + String(ss).padStart(2, "0");
  }

  function playPreview() {
    if (!previewAudio || !previewAudio.src) {
      return;
    }
    previewAudio.play();
    if (recordStatus) {
      recordStatus.textContent = "Reproduciendo grabación de prueba...";
    }
    previewAudio.onended = function () {
      if (recordStatus) {
        recordStatus.textContent = "Reproducción finalizada";
      }
    };
  }

  buildWaveform();
  initMicStream();

  if (btnRecord) {
    btnRecord.addEventListener("click", toggleRecording);
  }
  if (btnPlay) {
    btnPlay.addEventListener("click", playPreview);
  }

  window.addEventListener("beforeunload", stopAudio);
})();
