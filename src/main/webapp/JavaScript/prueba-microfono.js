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

  const playbackWidget = document.getElementById("playback-widget");
  const widgetPlayToggle = document.getElementById("widget-play-toggle");
  const widgetSeekBar = document.getElementById("widget-seek-bar");
  const widgetCurrentTime = document.getElementById("widget-current-time");
  const widgetTotalTime = document.getElementById("widget-total-time");
  const widgetVolumeBar = document.getElementById("widget-volume-bar");

  let isRecording = false;
  let isPlaying = false;
  let bars = [];
  let audioContext = null;
  let analyser = null;
  let micSource = null;
  let stream = null;
  let animationFrame = null;
  let mediaRecorder = null;
  let recordedChunks = [];
  let recordedBlob = null;
  let recordedMimeType = "audio/webm";
  let recordingStart = 0;
  let recordingTimer = null;
  let recordedDurationSeconds = 0;

  // Web Audio fallback buffer playback
  let decodedAudioBuffer = null;
  let bufferSourceNode = null;
  let bufferGainNode = null;
  let bufferStartTime = 0;
  let bufferPauseOffset = 0;
  let bufferRafId = null;

  if (!waveformContainer || !statusValue) {
    return;
  }

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

  function ensureAudioContext() {
    if (!audioContext) {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (AudioCtx) {
        audioContext = new AudioCtx();
      }
    }
    if (audioContext && audioContext.state === "suspended") {
      audioContext.resume().catch(function () { });
    }
    return audioContext;
  }

  function buildWaveform() {
    const barWrapper = document.createElement("div");
    barWrapper.className = "waveform-bars";

    for (let index = 0; index < 36; index += 1) {
      const bar = document.createElement("span");
      bar.className = "wave-bar";
      bar.style.setProperty("--wave-height", "15%");
      barWrapper.appendChild(bar);
      bars.push(bar);
    }

    waveformContainer.innerHTML = "";
    waveformContainer.appendChild(barWrapper);
  }

  function setStatus(message, isActive) {
    if (statusValue) {
      statusValue.textContent = message;
      statusValue.classList.toggle("is-active", Boolean(isActive));
      statusValue.classList.toggle(
        "is-error",
        !isActive && message.indexOf("No se pudo") !== -1
      );
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

    const normalized = Math.max(0.04, Math.min(1, average / 200));

    bars.forEach(function (bar, index) {
      const waveFactor =
        0.35 + Math.sin(index * 0.28 + Date.now() * 0.005) * 0.35 + ((index % 4) * 0.08);
      const targetHeight = Math.max(10, Math.min(95, 10 + normalized * 85 * waveFactor));
      bar.style.setProperty("--wave-height", targetHeight + "%");
      bar.style.height = targetHeight + "%";
      if (normalized > 0.08) {
        bar.classList.add("wave-bar--active");
      } else {
        bar.classList.remove("wave-bar--active");
      }
    });

    animationFrame = requestAnimationFrame(animateWaveform);
  }

  function initMicStream() {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      setStatus("Tu navegador no admite acceso al micrófono", false);
      if (recordStatus) recordStatus.textContent = "Micrófono no soportado en este navegador";
      return Promise.reject(new Error("no-media"));
    }

    const constraints = {
      audio: {
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true,
      },
    };

    return navigator.mediaDevices
      .getUserMedia(constraints)
      .catch(function () {
        // Fallback to simple audio: true if constraints fail
        return navigator.mediaDevices.getUserMedia({ audio: true });
      })
      .then(function (micStream) {
        stream = micStream;
        ensureAudioContext();

        if (audioContext) {
          analyser = audioContext.createAnalyser();
          analyser.fftSize = 128;
          analyser.smoothingTimeConstant = 0.8;

          try {
            micSource = audioContext.createMediaStreamSource(micStream);
            micSource.connect(analyser);
          } catch (e) {
            console.warn("Could not connect media stream source:", e);
          }
        }

        if (micStream.getAudioTracks && micStream.getAudioTracks().length) {
          const track = micStream.getAudioTracks()[0];
          const label = track.label || "Micrófono predeterminado";
          if (deviceNameLabel) deviceNameLabel.textContent = label;
        }

        setStatus("Micrófono activo y conectado", true);
        if (recordStatus) {
          recordStatus.textContent = "Micrófono listo. Habla o presiona 'Grabar prueba'.";
        }
        animateWaveform();
        return micStream;
      })
      .catch(function (err) {
        console.error("Mic access error:", err);
        setStatus("No se pudo acceder al micrófono. Permite el permiso en tu navegador.", false);
        if (recordStatus) {
          recordStatus.textContent = "Permiso denegado o micrófono no disponible.";
        }
      });
  }

  function toggleRecording() {
    ensureAudioContext();
    if (!isRecording) {
      startRecording();
    } else {
      stopRecording();
    }
  }

  function startRecording() {
    if (isPlaying) {
      stopPlayback();
    }

    if (!stream || stream.getAudioTracks().every(function (t) { return t.readyState === "ended"; })) {
      initMicStream().then(function (s) {
        if (s) beginMediaRecorder();
      });
      return;
    }
    beginMediaRecorder();
  }

  function beginMediaRecorder() {
    ensureAudioContext();
    recordedChunks = [];
    recordedBlob = null;
    decodedAudioBuffer = null;

    const mimeType = getBestSupportedMimeType();
    recordedMimeType = mimeType || "audio/webm";

    try {
      mediaRecorder = mimeType
        ? new MediaRecorder(stream, { mimeType: mimeType })
        : new MediaRecorder(stream);
    } catch (e) {
      try {
        mediaRecorder = new MediaRecorder(stream);
      } catch (err) {
        setStatus("Grabación no soportada en este navegador", false);
        return;
      }
    }

    mediaRecorder.ondataavailable = function (e) {
      if (e.data && e.data.size > 0) {
        recordedChunks.push(e.data);
      }
    };

    mediaRecorder.onstop = function () {
      recordedBlob = new Blob(recordedChunks, { type: mediaRecorder.mimeType || recordedMimeType });
      const objectUrl = URL.createObjectURL(recordedBlob);

      if (previewAudio) {
        previewAudio.src = objectUrl;
        previewAudio.volume = getVolumeLevel();
        previewAudio.load();
      }

      // Pre-decode audio buffer for instant, fail-safe Web Audio API playback
      if (audioContext && recordedBlob) {
        recordedBlob.arrayBuffer().then(function (ab) {
          audioContext.decodeAudioData(
            ab,
            function (decoded) {
              decodedAudioBuffer = decoded;
              recordedDurationSeconds = decoded.duration;
              updateDurationDisplays(recordedDurationSeconds);
            },
            function (decodeErr) {
              console.warn("Audio buffer decoding note:", decodeErr);
            }
          );
        }).catch(function () { });
      }

      if (btnPlay) {
        btnPlay.disabled = false;
        updatePlayButtonState(false);
      }

      if (playbackWidget) {
        playbackWidget.style.display = "block";
      }

      isRecording = false;
      if (btnRecord) {
        const textSpan = btnRecord.querySelector(".btn-text") || btnRecord;
        textSpan.textContent = "Grabar de nuevo";
        btnRecord.classList.remove("is-recording");
      }

      if (recordStatus) {
        recordStatus.textContent = "✅ Grabación lista. Haz clic en 'Escuchar prueba' para oírte.";
      }
      setStatus("Grabación completada", true);

      // Send backup to server
      const ctx = document.body.dataset.contextPath || "";
      const fileName = "mic-test-" + Date.now() + ".webm";
      const formData = new FormData();
      formData.append("file", recordedBlob, fileName);
      formData.append("recordingType", "MIC_TEST");
      const csrf = document.body.dataset.csrf || "";
      if (csrf) {
        formData.append("_csrf", csrf);
      }

      fetch(ctx + "/recording-upload", {
        method: "POST",
        headers: csrf ? { "X-CSRF-Token": csrf } : {},
        body: formData
      }).then(function (response) {
        if (!response.ok) {
          if (recordStatus) {
            recordStatus.textContent = "Grabación lista en el navegador, pero no se guardó en Oracle.";
          }
          return;
        }
        if (recordStatus) {
          recordStatus.textContent = "✅ Grabación lista y guardada. Haz clic en 'Escuchar prueba' para oírte.";
        }
      }).catch(function () {
        if (recordStatus) {
          recordStatus.textContent = "Grabación lista en el navegador, pero no se pudo enviar al servidor.";
        }
      });
    };

    // Use 250ms periodic chunk flushing so data is always accumulated reliably
    mediaRecorder.start(250);
    isRecording = true;
    recordingStart = Date.now();
    startTimer();

    if (btnRecord) {
      const textSpan = btnRecord.querySelector(".btn-text") || btnRecord;
      textSpan.textContent = "Detener grabación";
      btnRecord.classList.add("is-recording");
    }
    if (btnPlay) {
      btnPlay.disabled = true;
    }
    if (playbackWidget) {
      playbackWidget.style.display = "none";
    }
    if (recordStatus) {
      recordStatus.textContent = "🎙️ Grabando audio de prueba... Habla al micrófono.";
    }
    setStatus("Grabando prueba de voz...", true);
  }

  function stopRecording() {
    if (mediaRecorder && mediaRecorder.state !== "inactive") {
      try {
        if (typeof mediaRecorder.requestData === "function") {
          mediaRecorder.requestData();
        }
      } catch (e) { }
      mediaRecorder.stop();
    }
    stopTimer();
  }

  function startTimer() {
    stopTimer();
    recordingTimer = setInterval(function () {
      const elapsed = (Date.now() - recordingStart) / 1000;
      recordedDurationSeconds = elapsed;
      if (durationLabel) {
        durationLabel.textContent = formatDuration(elapsed);
      }
      if (widgetTotalTime) {
        widgetTotalTime.textContent = formatDuration(elapsed);
      }
    }, 200);
  }

  function stopTimer() {
    if (recordingTimer) {
      clearInterval(recordingTimer);
      recordingTimer = null;
    }
  }

  function formatDuration(seconds) {
    if (isNaN(seconds) || seconds < 0) seconds = 0;
    const total = Math.floor(seconds);
    const mm = Math.floor(total / 60);
    const ss = total % 60;
    return String(mm).padStart(2, "0") + ":" + String(ss).padStart(2, "0");
  }

  function updateDurationDisplays(dur) {
    if (durationLabel) durationLabel.textContent = formatDuration(dur);
    if (widgetTotalTime) widgetTotalTime.textContent = formatDuration(dur);
  }

  function getVolumeLevel() {
    if (widgetVolumeBar) {
      return parseFloat(widgetVolumeBar.value) / 100;
    }
    return 1.0;
  }

  function updatePlayButtonState(playing) {
    isPlaying = playing;
    const playText = btnPlay ? btnPlay.querySelector(".btn-text") : null;
    const playIcon = btnPlay ? btnPlay.querySelector(".play-icon") : null;
    const widgetIcon = widgetPlayToggle ? widgetPlayToggle.querySelector(".widget-icon") : null;

    if (playing) {
      if (playText) playText.textContent = "Pausar prueba";
      if (playIcon) playIcon.textContent = "⏸";
      if (widgetIcon) widgetIcon.textContent = "⏸";
      if (btnPlay) btnPlay.classList.add("is-playing");
      if (recordStatus) recordStatus.textContent = "🔊 Reproduciendo grabación de prueba...";
    } else {
      if (playText) playText.textContent = "Escuchar prueba";
      if (playIcon) playIcon.textContent = "▶";
      if (widgetIcon) widgetIcon.textContent = "▶";
      if (btnPlay) btnPlay.classList.remove("is-playing");
      if (recordStatus && !isRecording) {
        recordStatus.textContent = "Grabación lista para escuchar.";
      }
    }
  }

  function togglePlayback() {
    ensureAudioContext();
    if (isPlaying) {
      pausePlayback();
    } else {
      startPlayback();
    }
  }

  function startPlayback() {
    if (!recordedBlob && !previewAudio.src && !decodedAudioBuffer) {
      return;
    }

    ensureAudioContext();
    updatePlayButtonState(true);

    const volume = getVolumeLevel();

    // Strategy 1: HTML5 Audio Element playback
    if (previewAudio && previewAudio.src) {
      previewAudio.volume = volume;
      const playPromise = previewAudio.play();
      if (playPromise !== undefined) {
        playPromise
          .then(function () {
            setupAudioElementListeners();
          })
          .catch(function (err) {
            console.warn("HTML5 audio playback blocked/failed, falling back to Web Audio API buffer:", err);
            playBufferFallback();
          });
      } else {
        setupAudioElementListeners();
      }
    } else {
      playBufferFallback();
    }
  }

  function setupAudioElementListeners() {
    if (!previewAudio) return;

    previewAudio.ontimeupdate = function () {
      if (previewAudio.duration && !isNaN(previewAudio.duration)) {
        const percent = (previewAudio.currentTime / previewAudio.duration) * 100;
        if (widgetSeekBar) widgetSeekBar.value = percent;
        if (widgetCurrentTime) widgetCurrentTime.textContent = formatDuration(previewAudio.currentTime);
        if (widgetTotalTime) widgetTotalTime.textContent = formatDuration(previewAudio.duration);
      }
    };

    previewAudio.onended = function () {
      updatePlayButtonState(false);
      if (widgetSeekBar) widgetSeekBar.value = 0;
      if (widgetCurrentTime) widgetCurrentTime.textContent = "00:00";
      if (recordStatus) recordStatus.textContent = "Reproducción finalizada.";
    };
  }

  function playBufferFallback() {
    if (!decodedAudioBuffer && recordedBlob && audioContext) {
      recordedBlob.arrayBuffer().then(function (ab) {
        audioContext.decodeAudioData(ab, function (buf) {
          decodedAudioBuffer = buf;
          executeBufferPlay();
        }).catch(function () { });
      });
      return;
    }
    executeBufferPlay();
  }

  function executeBufferPlay() {
    if (!decodedAudioBuffer || !audioContext) {
      updatePlayButtonState(false);
      return;
    }

    stopBufferSource();

    bufferSourceNode = audioContext.createBufferSource();
    bufferSourceNode.buffer = decodedAudioBuffer;

    bufferGainNode = audioContext.createGain();
    bufferGainNode.gain.value = getVolumeLevel();

    bufferSourceNode.connect(bufferGainNode);
    bufferGainNode.connect(audioContext.destination);

    // Also connect to analyser to visualize playback!
    if (analyser) {
      bufferGainNode.connect(analyser);
    }

    const startOffset = bufferPauseOffset % decodedAudioBuffer.duration;
    bufferStartTime = audioContext.currentTime - startOffset;

    bufferSourceNode.start(0, startOffset);

    bufferSourceNode.onended = function () {
      if (isPlaying) {
        updatePlayButtonState(false);
        bufferPauseOffset = 0;
        if (widgetSeekBar) widgetSeekBar.value = 0;
        if (widgetCurrentTime) widgetCurrentTime.textContent = "00:00";
        if (recordStatus) recordStatus.textContent = "Reproducción finalizada.";
        cancelAnimationFrame(bufferRafId);
      }
    };

    function trackBufferProgress() {
      if (!isPlaying || !audioContext) return;
      const current = audioContext.currentTime - bufferStartTime;
      const total = decodedAudioBuffer.duration;
      if (total > 0) {
        const percent = Math.min(100, (current / total) * 100);
        if (widgetSeekBar) widgetSeekBar.value = percent;
        if (widgetCurrentTime) widgetCurrentTime.textContent = formatDuration(current);
        if (widgetTotalTime) widgetTotalTime.textContent = formatDuration(total);
      }
      if (current < total) {
        bufferRafId = requestAnimationFrame(trackBufferProgress);
      }
    }
    bufferRafId = requestAnimationFrame(trackBufferProgress);
  }

  function pausePlayback() {
    if (previewAudio && !previewAudio.paused) {
      previewAudio.pause();
    }
    if (bufferSourceNode && audioContext) {
      bufferPauseOffset = audioContext.currentTime - bufferStartTime;
      stopBufferSource();
    }
    cancelAnimationFrame(bufferRafId);
    updatePlayButtonState(false);
  }

  function stopPlayback() {
    if (previewAudio) {
      previewAudio.pause();
      previewAudio.currentTime = 0;
    }
    stopBufferSource();
    cancelAnimationFrame(bufferRafId);
    bufferPauseOffset = 0;
    updatePlayButtonState(false);
    if (widgetSeekBar) widgetSeekBar.value = 0;
    if (widgetCurrentTime) widgetCurrentTime.textContent = "00:00";
  }

  function stopBufferSource() {
    if (bufferSourceNode) {
      try {
        bufferSourceNode.stop();
      } catch (e) { }
      bufferSourceNode.disconnect();
      bufferSourceNode = null;
    }
  }

  function stopAudio() {
    if (animationFrame) {
      cancelAnimationFrame(animationFrame);
      animationFrame = null;
    }
    stopPlayback();
    if (stream) {
      stream.getTracks().forEach(function (track) {
        track.stop();
      });
      stream = null;
    }
    if (audioContext && audioContext.state !== "closed") {
      audioContext.close().catch(function () { });
      audioContext = null;
      analyser = null;
    }
  }

  // Seek bar user interaction
  if (widgetSeekBar) {
    widgetSeekBar.addEventListener("input", function () {
      const targetPercent = parseFloat(widgetSeekBar.value);
      const totalDuration = (previewAudio && previewAudio.duration) || (decodedAudioBuffer && decodedAudioBuffer.duration) || recordedDurationSeconds;
      if (totalDuration > 0) {
        const seekTime = (targetPercent / 100) * totalDuration;
        if (previewAudio) {
          previewAudio.currentTime = seekTime;
        }
        bufferPauseOffset = seekTime;
        if (widgetCurrentTime) widgetCurrentTime.textContent = formatDuration(seekTime);
      }
    });
  }

  // Volume slider interaction
  if (widgetVolumeBar) {
    widgetVolumeBar.addEventListener("input", function () {
      const vol = getVolumeLevel();
      if (previewAudio) {
        previewAudio.volume = vol;
      }
      if (bufferGainNode) {
        bufferGainNode.gain.value = vol;
      }
    });
  }

  // Event bindings
  if (btnRecord) {
    btnRecord.addEventListener("click", toggleRecording);
  }
  if (btnPlay) {
    btnPlay.addEventListener("click", togglePlayback);
  }
  if (widgetPlayToggle) {
    widgetPlayToggle.addEventListener("click", togglePlayback);
  }

  // Auto-init on page load and on first user click/touch
  buildWaveform();
  initMicStream();

  document.addEventListener("click", function () {
    ensureAudioContext();
  }, { once: true });

  window.addEventListener("beforeunload", stopAudio);
})();
