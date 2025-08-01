<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Добро пожаловать</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* Базовый стиль */
        body {
            font-family: 'Inter', sans-serif;
            background: #F5F7FA;
            margin: 0;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            padding: 0;
            color: #333;
            position: relative;
            padding-bottom: 60px;
        }

        /* Контейнер приветствия */
        .safronov-welcome {
            background: white;
            border-radius: 16px;
            padding: 48px;
            width: 100%;
            max-width: 550px;
            text-align: center;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            position: relative;
            margin: 32px 0 0 0;
        }

        /* Иконка курса */
        .safronov-welcome::before {
            content: "🎓";
            position: absolute;
            top: -24px;
            left: 50%;
            transform: translateX(-50%);
            font-size: 40px;
            color: #333;
            background: white;
            padding: 4px 12px;
            border-radius: 50%;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        /* Заголовок */
        .safronov-welcome h1 {
            color: #333;
            font-size: 36px;
            font-weight: 700;
            margin-bottom: 24px;
            position: relative;
        }

        .safronov-welcome h1::after {
            content: "";
            display: block;
            width: 80px;
            height: 4px;
            background: #4A90E2;
            margin: 16px auto 0;
            border-radius: 2px;
        }

        /* Подзаголовок */
        .safronov-welcome p {
            color: #555;
            font-size: 20px;
            line-height: 1.6;
            margin-bottom: 32px;
            text-align: left;
            max-width: 400px;
            margin-left: auto;
            margin-right: auto;
        }

        /* Кнопка */
        .safronov-btn {
            display: inline-block;
            margin-top: 24px;
            padding: 14px 24px;
            background-color: #4A90E2;
            color: white;
            font-size: 16px;
            font-weight: 600;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
            text-align: center;
        }

        .safronov-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(74, 144, 226, 0.25);
        }

        /* Блок с текстом */
        .info-block {
            color: #555;
            font-size: 20px;
            line-height: 1.6;
            margin-bottom: 24px;
            text-align: left;
            max-width: 400px;
            margin-left: auto;
            margin-right: auto;
        }

        /* Футер */
        .footer {
            width: 100%;
            text-align: center;
            padding: 8px 0;
            background: #F5F7FA;
            color: #666;
            font-size: 14px;
            opacity: 0.9;
            position: fixed;
            bottom: 0;
            left: 0;
        }
    </style>
</head>
<body>
<div class="safronov-welcome" data-test-id="welcome-container">
    <h1 data-test-id="welcome-title">Добро пожаловать!</h1>
    <div data-test-id="welcome-message">
        <div class="info-block">Здравствуйте, ${employee.employeeName} ${employee.employeeSurname}.</div>
        <div class="info-block">Ваш email: ${employee.email}</div>
        <div class="info-block">Рад Вас приветствовать в тренажере по работе с селенидом.</div>
        <div class="info-block">Вы будете писать автотесты используя Java ${employee.javaVersion}.</div>

        <div class="info-block">
            ${employee.autoTestExperience.equals("да") ? "Вы уже занимались автотестированием" : "Это ваш первый опыт в автотестировании"}.
        </div>

        <c:set var="hasNone" value="false"/>
        <c:forEach var="lang" items="${employee.programmingLanguages}">
            <c:if test="${lang == 'none'}">
                <c:set var="hasNone" value="true"/>
            </c:if>
        </c:forEach>
        <div class="info-block">
            <c:choose>
                <c:when test="${hasNone}">
                    Языками программирования не владею
                </c:when>
                <c:otherwise>
                    Владеете языками программирования:
                    <c:forEach var="lang" items="${employee.programmingLanguages}" varStatus="status">
                        <c:if test="${lang != 'none'}">
                            ${lang}<c:if test="${!status.last}">, </c:if>
                        </c:if>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <button class="safronov-btn" data-test-id="back-button" onclick="window.location.href='/askDetails'">Назад</button>
</div>
<div class="footer" data-test-id="trainer-footer">Safronov_ID • Тренажер для AQA</div>
</body>
</html>