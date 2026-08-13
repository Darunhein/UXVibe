(function () {
  var waveformContainer = document.querySelector(".waveform-container");
  var statusValue = document.querySelector(
    ".status-section .status-item:nth-of-type(1) .status-value"
  );
  var durationLabel = document.getElementById("test-duration");
  var deviceNameLabel = document.getElementById("device-name");
  var btnRecord = document.getElementById("btn-record");
  var btnPlay = document.getElementById("btn-play");
  var recordStatus = document.getElementById("record-status");
  var previewAudio = document.getElementById("preview-audio");

  var isRecording = false;

  var bars = [];
  var audioContext = null;
  var analyser = null;
  var stream = null;
  var animationFrame = null;
  var mediaRecorder = null;
  var recordedChunks = [];
  var recordingStart = 0;
  var recordingTimer = null;

  if (!waveformContainer || !statusValue) {
    return;
  }

  function buildWaveform() {
    var barWrapper = document.createElement("div");
    barWrapper.className = "waveform-bars";

    for (var index = 0; index < 36; index += 1) {
      var bar = document.createElement("span");
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

    if (audioContext) {
      audioContext.close().catch(function () {
        // Ignore cleanup errors.
      });
      audioContext = null;
      analyser = null;
    }
  }

  function animateWaveform() {
    if (!analyser) {
      return;
    }

    var dataArray = new Uint8Array(analyser.frequencyBinCount);
    analyser.getByteFrequencyData(dataArray);

    var average = 0;
    for (var index = 0; index < dataArray.length; index += 1) {
      average += dataArray[index];
    }
    average = average / dataArray.length;

    var normalized = Math.max(0.05, Math.min(1, average / 255));

    bars.forEach(function (bar, index) {
      var waveFactor = 0.45 + Math.sin(index * 0.28) * 0.28 + (index % 3) * 0.06;
      var targetHeight = 12 + normalized * 78 * waveFactor;
      bar.style.setProperty("--wave-height", targetHeight + "%");
      bar.style.height = targetHeight + "%";
    });

    animationFrame = requestAnimationFrame(animateWaveform);
  }

  function startMicStream(attachToAnalyzer) {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      setStatus("El navegador no admite acceso al micrófono", false);
      return Promise.reject(new Error("no-media"));
    }

    return navigator.mediaDevices.getUserMedia({ audio: true }).then(function (micStream) {
      stream = micStream;
      audioContext = new (window.AudioContext || window.webkitAudioContext)();
      analyser = audioContext.createAnalyser();
      analyser.fftSize = 256;

      var source = audioContext.createMediaStreamSource(micStream);
      source.connect(analyser);

      // device info if available
      if (micStream.getAudioTracks && micStream.getAudioTracks().length) {
        var label = micStream.getAudioTracks()[0].label || 'Dispositivo de audio';
        if (deviceNameLabel) deviceNameLabel.textContent = label;
      }

      setStatus("Micrófono activo", true);
      if (attachToAnalyzer) animateWaveform();
      return micStream;
    });
  }

  function startLiveTest() {
    // just start live stream and visualization
    startMicStream(true).catch(function (e) {
      setStatus("No se pudo acceder al micrófono. Permita acceso y vuelva a intentar.", false);
    });
  }

  function startRecording() {
    if (!stream) {
      startMicStream(false).then(function () {
        // ready to record
        beginRecording();
      }).catch(function () {
        setStatus('No se pudo iniciar el micrófono para grabar', false);
      });
    } else {
      beginRecording();
    }
  }

  function beginRecording() {
    if (!stream) return;
    recordedChunks = [];
    try {
      mediaRecorder = new MediaRecorder(stream);
    } catch (e) {
      setStatus('Tu navegador no soporta la grabación', false);
      return;
    }
    mediaRecorder.ondataavailable = function (e) {
      if (e.data && e.data.size > 0) recordedChunks.push(e.data);
    };
    mediaRecorder.onstop = function () {
      var blob = new Blob(recordedChunks, { type: 'audio/webm' });
      var url = URL.createObjectURL(blob);
      previewAudio.src = url;
      previewAudio.style.display = '';
      btnPlay.disabled = false;
      isRecording = false;
      btnRecord.textContent = 'Grabar';
      recordStatus.textContent = 'Grabación lista — ' + formatDuration((Date.now() - recordingStart) / 1000);

      // Upload recorded blob as multipart/form-data (blob) so server can accept large files
      try {
        var fileName = 'mic-test-' + Date.now() + '.webm';
        recordStatus.textContent = 'Subiendo grabación...';
        var form = new FormData();
        form.append('fileName', fileName);
        form.append('file', blob, fileName);

        var ctx = document.body && document.body.dataset ? document.body.dataset.contextPath || '' : '';
        fetch(ctx + '/recording-upload', {
          method: 'POST',
          body: form,
          credentials: 'same-origin'
        })
          .then(function (resp) {
            return resp.text().then(function (t) { return { ok: resp.ok, text: t }; });
          })
          .then(function (result) {
            if (result.ok) {
              recordStatus.textContent = 'Grabación subida';
            } else {
              recordStatus.textContent = 'Error al subir la grabación: ' + (result.text || '');
            }
          })
          .catch(function (err) {
            console.error('upload error', err);
            recordStatus.textContent = 'Error al subir la grabación';
          });
      } catch (e) {
        console.error('upload error', e);
        recordStatus.textContent = 'Error al subir la grabación';
      }
    };

    mediaRecorder.start();
    recordingStart = Date.now();
    startRecordingTimer();
    btnRecord.disabled = true;
    btnStop.disabled = false;
    setStatus('Grabando...', true);
    // animate live while recording
    if (audioContext && analyser) animateWaveform();
    else startMicStream(true).then(function(){ if (analyser) animateWaveform(); });
  }

  function stopRecording() {
    if (mediaRecorder && mediaRecorder.state !== 'inactive') {
      mediaRecorder.stop();
    }
    stopRecordingTimer();
    btnRecord.disabled = false;
    btnStop.disabled = true;
    setStatus('Grabación detenida', true);
  }

  function startRecordingTimer() {
    var start = recordingStart;
    if (!durationLabel) return;
    recordingTimer = setInterval(function () {
      durationLabel.textContent = formatDuration((Date.now() - start) / 1000);
    }, 250);
  }

  function stopRecordingTimer() {
    if (recordingTimer) {
      clearInterval(recordingTimer);
      recordingTimer = null;
    }
  }

  function formatDuration(seconds) {
    seconds = Math.floor(seconds);
    var mm = Math.floor(seconds / 60);
    var ss = seconds % 60;
    return String(mm).padStart(2, '0') + ':' + String(ss).padStart(2, '0');
  }

  function playPreview() {
    if (!previewAudio.src) return;
    // attach analyser to playback for waveform animation
    if (audioContext && audioContext.state === 'suspended') {
      audioContext.resume().catch(function(){});
    }
    if (!audioContext) {
      audioContext = new (window.AudioContext || window.webkitAudioContext)();
    }
    if (!analyser) {
      analyser = audioContext.createAnalyser();
      analyser.fftSize = 256;
    }
    try {
      var sourceNode = audioContext.createMediaElementSource(previewAudio);
      sourceNode.connect(analyser);
      analyser.connect(audioContext.destination);
    } catch (e) {
      // some browsers disallow creating multiple media element sources; ignore if fails
    }
    previewAudio.play();
    animateWaveform();
    previewAudio.onended = function () {
      // stop animation when playback ends
      if (animationFrame) {
        cancelAnimationFrame(animationFrame);
        animationFrame = null;
      }
    };
  }

  buildWaveform();
  startLiveTest();

  // wire buttons
  if (btnStart) btnStart.addEventListener('click', function () {
    startLiveTest();
    btnRecord.disabled = false;
    btnStart.disabled = true;
    setStatus('Micrófono activo para prueba', true);
  });
  if (btnRecord) btnRecord.addEventListener('click', startRecording);
  if (btnStop) btnStop.addEventListener('click', stopRecording);
  if (btnPlay) btnPlay.addEventListener('click', playPreview);

  window.addEventListener('beforeunload', stopAudio);
})();
