(function () {
  var form = document.getElementById("questionnaireForm");
  if (!form) {
    return;
  }

  var contextPath = document.body.dataset.contextPath || "";
  var nextButton = document.getElementById("sbNextButton");
  var backButton = document.getElementById("sbBackButton");

  if (nextButton) {
    nextButton.addEventListener("click", function () {
      var stress = document.querySelector('input[name="stress"]:checked');
      var relaxation = document.querySelector(
        'input[name="relaxation"]:checked',
      );
      if (!stress || !relaxation) {
        alert("Por favor, responde todas las preguntas");
        return;
      }

      window.location.href = contextPath + "/html/04%20SAM%20Line/sam-1.jsp";
    });
  }

  if (backButton) {
    backButton.addEventListener("click", function (event) {
      event.preventDefault();
      window.history.back();
    });
  }
})();
