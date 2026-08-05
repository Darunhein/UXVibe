(function () {
  var pauseButton = document.querySelector(".material-symbolspause-icon");
  var pauseMenu = document.getElementById("pauseMenu");
  var timerLabel = document.querySelector(".h2");
  var restartButton = document.querySelector(".pause-menu__item--button");
  var contextPath = document.body.dataset.contextPath || "";
  var storageKey = "uxvibe-grabacion-tiempo";
  var timerSeconds = parseInt(sessionStorage.getItem(storageKey) || "0", 10);
  var timerId = null;

  function formatTime(totalSeconds) {
    var minutes = Math.floor(totalSeconds / 60);
    var seconds = totalSeconds % 60;
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

  if (timerLabel) {
    timerLabel.textContent = formatTime(timerSeconds);
  }
  startTimer();

  if (pauseButton) {
    pauseButton.addEventListener("click", function () {
      if (pauseMenu && pauseMenu.hidden) {
        openMenu();
        return;
      }
      closeMenu();
      startTimer();
    });
  }

  if (restartButton) {
    restartButton.addEventListener("click", function () {
      sessionStorage.removeItem(storageKey);
      timerSeconds = 0;
      if (timerLabel) {
        timerLabel.textContent = formatTime(timerSeconds);
      }
      closeMenu();
      startTimer();
    });
  }
}());
