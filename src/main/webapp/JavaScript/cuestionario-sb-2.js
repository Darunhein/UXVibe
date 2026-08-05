(function () {
  var form = document.getElementById("questionnaireForm");
  if (!form) {
    return;
  }

  var contextPath = document.body.dataset.contextPath || "";
  var backButton = document.getElementById("sbBackButton");

  form.addEventListener("submit", function (e) {
    e.preventDefault();

    var stress = document.querySelector('input[name="stress"]:checked');
    var relaxation = document.querySelector('input[name="relaxation"]:checked');
    if (!stress || !relaxation) {
      alert("Por favor, responde todas las preguntas");
      return;
    }

    window.location.href = contextPath + "/html/04%20SAM%20Line/cheers-bye.jsp";
  });

  if (backButton) {
    backButton.addEventListener("click", function () {
      window.location.href = contextPath + "/html/03%20Execution%20Line/grabacion-de-prueba.jsp";
    });
  }
}());
