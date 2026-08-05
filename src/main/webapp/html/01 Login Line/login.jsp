<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
    <title>login del evaluador</title>
    <meta charset="utf-8"/>
    <meta name="viewport" content="initial-scale=1, width=device-width"/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/global.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/login.css"/>
    <link
            rel="stylesheet"
            href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap"
    />
</head>
<body>
<div class="login-del-evaluador">
    <h1 class="bienvenido">Bienvenido</h1>
    <main class="frame">
        <div class="frame2">
            <div class="merged-field merged-field-email">
                <div class="field-label-group">
                    <h2 class="email">Email</h2>
                </div>
                <div class="merged-image-wrapper">
                    <img
                            class="merged-asset-1-icon"
                            alt=""
                            src="${pageContext.request.contextPath}/public/login/merged-asset-1@2x.png"
                    />
                    <div class="email-field-group">
                        <input class="form-input ingresa-tu-email" type="email" name="email"
                               placeholder="Ingresa tu email" autocomplete="email" required/>
                    </div>
                </div>
            </div>
            <div class="merged-field merged-field-password">
                <div class="field-label-group">
                    <h2 class="contrasea">Contraseña</h2>
                </div>
                <div class="merged-image-wrapper">
                    <img src="${pageContext.request.contextPath}/public/registro/mdi-light-eye@2x.png" alt=""/>

                    <div class="password-field-group">
                        <input class="form-input ingresa-tu-contraseña" type="password" name="password"
                               placeholder="Ingresa tu contraseña" autocomplete="current-password" required/>
                    </div>
                </div>
            </div>
            <section class="actions-group">
                <div class="actions-group2">
                    <button class="entrar">
                        <div class="entrar-text">Entrar</div>
                    </button>
                    <button class="crear-cuenta" id="crearCuentaText">
                        Crear cuenta
                    </button>
                    <button class="olvid-mi-contrasea" id="olvidMiContrasea">
                        Olvidé mi contraseña
                    </button>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>

