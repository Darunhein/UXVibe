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
      var fullName = document.getElementById("fullName").value.trim();
      var age = document.getElementById("age").value;
      var gender = document.querySelector('input[name="gender"]:checked');
      var education = document.querySelector('input[name="education"]:checked');

      if (!fullName || !age || !gender || !education) {
        alert("Por favor, completa todos los campos");
        return;
      }

      fetch(contextPath + "/participant-identity", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
        },
        body: "participantName=" + encodeURIComponent(fullName),
      }).finally(function () {
        window.location.href =
          contextPath + "/html/04%20SAM%20Line/cuestionario-sb-2.jsp";
      });
    });
  }

  if (backButton) {
    backButton.addEventListener("click", function (event) {
      event.preventDefault();
      window.history.back();
    });
  }
})();
