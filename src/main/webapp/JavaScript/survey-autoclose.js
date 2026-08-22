(function () {
  const channel = typeof BroadcastChannel !== "undefined" ? new BroadcastChannel("uxvibe_recording_channel") : null;

  function closeSurveyWindow() {
    try {
      window.close();
    } catch (e) { }
  }

  if (channel) {
    channel.onmessage = function (event) {
      if (event && event.data && event.data.type === "CLOSE_SURVEY_TAB") {
        closeSurveyWindow();
      }
    };
  }

  window.addEventListener("storage", function (e) {
    if (e.key === "uxvibe_close_survey_event" && e.newValue) {
      closeSurveyWindow();
    }
  });
})();
