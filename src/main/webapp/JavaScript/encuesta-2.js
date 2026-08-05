(function () {
  var form = document.getElementById("surveyForm");
  if (!form) {
    return;
  }

  var contextPath = document.body.dataset.contextPath || "";
  var questions = ["q6", "q7", "q8", "q9", "q10"];

  form.addEventListener("submit", function (e) {
    e.preventDefault();

    for (var i = 0; i < questions.length; i++) {
      if (!form.elements[questions[i]].checked) {
        alert("Por favor, responde todas las preguntas");
        return;
      }
    }

    window.location.href = contextPath + "/html/04%20SAM%20Line/encuesta-3.jsp";
  });

  document.querySelectorAll('.scale-option input[type="radio"]').forEach(function (radio) {
    radio.addEventListener("change", function (e) {
      var parent = this.closest(".scale-options");
      parent.querySelectorAll(".scale-option").forEach(function (option) {
        option.classList.remove("selected");
      });
      e.target.closest(".scale-option").classList.add("selected");
    });
  });
}());
