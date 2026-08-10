(function () {
  var waveformContainer = document.querySelector(".waveform-container");
  var statusValue = document.querySelector(
    ".status-section .status-item:nth-of-type(1) .status-value",
  );
  var bars = [];
  var audioContext = null;
  var analyser = null;
  var stream = null;
  var animationFrame = null;

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
    statusValue.classList.toggle(
      "is-error",
      !isActive && message.indexOf("No se pudo") !== -1,
    );
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

    var normalized = Math.max(0.16, Math.min(1, average / 255));

    bars.forEach(function (bar, index) {
      var waveFactor =
        0.45 + Math.sin(index * 0.28) * 0.28 + (index % 3) * 0.06;
      var targetHeight = 16 + normalized * 78 * waveFactor;
      bar.style.setProperty("--wave-height", targetHeight + "%");
      bar.style.height = targetHeight + "%";
    });

    animationFrame = requestAnimationFrame(animateWaveform);
  }

  function startMicTest() {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      setStatus("El navegador no admite acceso al micrófono", false);
      return;
    }

    setStatus("Probando micrófono...", true);

    navigator.mediaDevices
      .getUserMedia({ audio: true })
      .then(function (micStream) {
        stream = micStream;
        audioContext = new (window.AudioContext || window.webkitAudioContext)();
        analyser = audioContext.createAnalyser();
        analyser.fftSize = 256;

        var source = audioContext.createMediaStreamSource(micStream);
        source.connect(analyser);

        setStatus("Micrófono funcionando correctamente", true);
        animateWaveform();
      })
      .catch(function () {
        setStatus(
          "No se pudo acceder al micrófono. Permite el acceso y vuelve a intentar.",
          false,
        );
      });
  }

  buildWaveform();
  startMicTest();

  window.addEventListener("beforeunload", stopAudio);
})();
