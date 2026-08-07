(function () {
  var form = document.getElementById("surveyForm");
  if (!form) {
    return;
  }

  var contextPath = document.body.dataset.contextPath || "";
  var nextButton = document.getElementById("surveyNextButton");
  var backButton = document.getElementById("surveyBackButton");
  var questions = ["q6", "q7", "q8", "q9", "q10"];

  if (nextButton) {
    nextButton.addEventListener("click", function () {
      for (var i = 0; i < questions.length; i++) {
        var element = questions[i];
        var selected = form.querySelector(
          'input[name="' + element + '"]:checked',
        );
        if (!selected) {
          alert("Por favor, responde todas las preguntas antes de seguir.");
          return;
        }
      }

      fetch(contextPath + "/survey-submit", {
        method: "POST",
        body: new FormData(form),
      }).then(function () {
        window.location.href =
          contextPath + "/html/04%20SAM%20Line/encuesta-3.jsp";
      });
    });
  }

  if (backButton) {
    backButton.addEventListener("click", function (event) {
      event.preventDefault();
      window.history.back();
    });
  }

  document
    .querySelectorAll('.scale-option input[type="radio"]')
    .forEach(function (radio) {
      radio.addEventListener("change", function (e) {
        var parent = this.closest(".scale-options");
        parent.querySelectorAll(".scale-option").forEach(function (option) {
          option.classList.remove("selected");
        });
        e.target.closest(".scale-option").classList.add("selected");
      });
    });
})();
