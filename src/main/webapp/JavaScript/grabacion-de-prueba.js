(function () {
  var pauseButton = document.querySelector(".material-symbolspause-icon");
  var pauseMenu = document.getElementById("pauseMenu");
  var timerLabel = document.querySelector(".h2");
  var restartButton = document.querySelector(".pause-menu__item--button");
  var contextPath = document.body.dataset.contextPath || "";
  var storageKey = "uxvibe-grabacion-tiempo";
  var timerSeconds = Number.parseInt(
    sessionStorage.getItem(storageKey) || "0",
    10,
  );
  var timerId = null;
  var mediaRecorder = null;
  var chunks = [];
  var stream = null;
  var isRecording = false;
  var recordingStartedAt = null;

  function formatTime(totalSeconds) {
    var minutes = Math.floor(totalSeconds / 60);
    var seconds = totalSeconds % 60;
    return (
      String(minutes).padStart(2, "0") + ":" + String(seconds).padStart(2, "0")
    );
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
      timerSeconds += 1;
      sessionStorage.setItem(storageKey, String(timerSeconds));
      if (timerLabel) {
        timerLabel.textContent = formatTime(timerSeconds);
      }
    }, 1000);
  }

  function closeMenu() {
    if (pauseMenu) {
      pauseMenu.hidden = true;
    }
    if (pauseButton) {
      pauseButton.classList.remove("is-paused");
      pauseButton.setAttribute("aria-expanded", "false");
    }
  }

  function openMenu() {
    if (pauseMenu) {
      pauseMenu.hidden = false;
    }
    if (pauseButton) {
      pauseButton.classList.add("is-paused");
      pauseButton.setAttribute("aria-expanded", "true");
    }
    stopTimer();
  }

  function stopMediaTracks() {
    if (stream) {
      stream.getTracks().forEach(function (track) {
        track.stop();
      });
      stream = null;
    }
  }

  function uploadRecording(blob) {
    var fileName = "recording-" + Date.now() + ".webm";
    var reader = new FileReader();
    reader.onloadend = function () {
      var base64 = reader.result.split(",")[1];
      var payload = {
        fileName: fileName,
        audioUrl: base64,
      };
      fetch(contextPath + "/recording-upload", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
        body:
          "fileName=" +
          encodeURIComponent(fileName) +
          "&audioUrl=" +
          encodeURIComponent(base64),
      }).catch(function () {
        console.warn("No se pudo enviar el audio al servidor.");
      });
    };
    reader.readAsDataURL(blob);
  }

  function beginRecording() {
    if (isRecording) {
      return;
    }
    if (navigator.mediaDevices?.getUserMedia) {
      navigator.mediaDevices
        .getUserMedia({ audio: true })
        .then(function (micStream) {
          stream = micStream;
          chunks = [];
          mediaRecorder = new MediaRecorder(stream);
          mediaRecorder.ondataavailable = function (event) {
            if (event.data.size > 0) {
              chunks.push(event.data);
            }
          };
          mediaRecorder.onstop = function () {
            var blob = new Blob(chunks, { type: "audio/webm" });
            uploadRecording(blob);
            stopMediaTracks();
          };
          mediaRecorder.start();
          isRecording = true;
          recordingStartedAt = Date.now();
          startTimer();
        })
        .catch(function () {
          if (timerLabel) {
            timerLabel.textContent = "Mic no disponible";
          }
        });
    }
  }

  function stopRecording() {
    if (mediaRecorder && isRecording) {
      mediaRecorder.stop();
      isRecording = false;
    }
  }

  if (timerLabel) {
    timerLabel.textContent = formatTime(timerSeconds);
  }
  beginRecording();

  if (pauseButton) {
    pauseButton.addEventListener("click", function () {
      if (pauseMenu?.hidden) {
        openMenu();
        stopRecording();
        return;
      }
      closeMenu();
      beginRecording();
    });
  }

  if (restartButton) {
    restartButton.addEventListener("click", function () {
      sessionStorage.removeItem(storageKey);
      timerSeconds = 0;
      if (timerLabel) {
        timerLabel.textContent = formatTime(timerSeconds);
      }
      stopRecording();
      closeMenu();
      beginRecording();
    });
  }

  window.addEventListener("beforeunload", function () {
    stopRecording();
    stopMediaTracks();
  });
})();
