(function () {
  const pauseDetails = document.getElementById("pauseDetails");
  const pauseBtn = document.getElementById("pauseBtn");
  const timerLabel = document.querySelector(".h2");
  const restartBtn = document.getElementById("restartBtn");
  const startSurveyLink = document.getElementById("startSurveyLink");
  const contextPath = document.body.dataset.contextPath || "";
  const storageKey = "uxvibe-grabacion-tiempo";

  let timerSeconds = Number.parseInt(sessionStorage.getItem(storageKey) || "0", 10);
  let timerId = null;
  let mediaRecorder = null;
  let chunks = [];
  let stream = null;
  let isRecording = false;

  function formatTime(totalSeconds) {
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
      timerSeconds += 1;
      sessionStorage.setItem(storageKey, String(timerSeconds));
      if (timerLabel) {
        timerLabel.textContent = formatTime(timerSeconds);
      }
    }, 1000);
  }

  function stopMediaTracks() {
    if (stream) {
      stream.getTracks().forEach(function (track) {
        track.stop();
      });
      stream = null;
    }
  }

  function uploadRecording(blob, callback) {
    const fileName = "test-session-" + Date.now() + ".webm";
    const reader = new FileReader();
    reader.onloadend = function () {
      const base64 = reader.result.split(",")[1];
      try {
        sessionStorage.setItem("uxvibe_audio_base64", base64);
        sessionStorage.setItem("uxvibe_audio_filename", fileName);
      } catch (e) {}

      fetch(contextPath + "/recording-upload", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
        body: "fileName=" + encodeURIComponent(fileName) + "&audioUrl=" + encodeURIComponent(base64),
      })
        .then(function () {
          if (typeof callback === "function") callback();
        })
        .catch(function () {
          if (typeof callback === "function") callback();
        });
    };
    reader.readAsDataURL(blob);
  }

  function beginRecording() {
    if (isRecording) {
      return;
    }
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
      navigator.mediaDevices
        .getUserMedia({ audio: true })
        .then(function (micStream) {
          stream = micStream;
          chunks = [];
          mediaRecorder = new MediaRecorder(stream);
          mediaRecorder.ondataavailable = function (event) {
            if (event.data && event.data.size > 0) {
              chunks.push(event.data);
            }
          };
          mediaRecorder.onstop = function () {
            const blob = new Blob(chunks, { type: "audio/webm" });
            uploadRecording(blob);
            stopMediaTracks();
          };
          mediaRecorder.start();
          isRecording = true;
          startTimer();
        })
        .catch(function (err) {
          console.warn("Micrófono no disponible en grabación de prueba:", err);
          startTimer();
        });
    } else {
      startTimer();
    }
  }

  function stopRecording(callback) {
    if (mediaRecorder && mediaRecorder.state !== "inactive") {
      mediaRecorder.onstop = function () {
        const blob = new Blob(chunks, { type: "audio/webm" });
        uploadRecording(blob, callback);
        stopMediaTracks();
      };
      mediaRecorder.stop();
      isRecording = false;
    } else {
      if (typeof callback === "function") callback();
    }
  }

  if (timerLabel) {
    timerLabel.textContent = formatTime(timerSeconds);
  }
  beginRecording();

  if (pauseDetails) {
    pauseDetails.addEventListener("toggle", function () {
      if (pauseDetails.open) {
        stopTimer();
      } else {
        startTimer();
      }
    });
  }

  if (startSurveyLink) {
    startSurveyLink.addEventListener("click", function (event) {
      event.preventDefault();
      stopTimer();
      stopRecording(function () {
        window.location.href = contextPath + "/cuestionario-sb-1";
      });
    });
  }

  if (restartBtn) {
    restartBtn.addEventListener("click", function () {
      sessionStorage.removeItem(storageKey);
      timerSeconds = 0;
      if (timerLabel) {
        timerLabel.textContent = formatTime(timerSeconds);
      }
      stopRecording(function () {
        if (pauseDetails) {
          pauseDetails.open = false;
        }
        beginRecording();
      });
    });
  }

  window.addEventListener("beforeunload", function () {
    stopRecording();
    stopMediaTracks();
  });
})();
