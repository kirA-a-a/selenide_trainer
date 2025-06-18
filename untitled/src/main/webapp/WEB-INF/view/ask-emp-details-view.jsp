<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
    <title>Регистрация на курсе по автотестированию</title>
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
        /* Форма */
        .safronov-form {
            background: white;
            border-radius: 16px;
            padding: 40px;
            width: 100%;
            max-width: 450px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            position: relative;
            max-height: 96vh;
            overflow-y: auto;
            padding-bottom: 48px;
            margin: 32px 0 0 0;
        }
        /* Иконка курса (сделана кликабельной с анимацией) */
        .course-icon {
            position: absolute;
            top: 0 !important;
            left: 50%;
            transform: translateX(-50%);
            width: 50px;
            height: 50px;
            background: white;
            border-radius: 50%;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            z-index: 2;
            font-size: 0;
        }
        .course-icon::before,
        .course-icon::after {
            content: "🎓";
            position: absolute;
            font-size: 40px;
            color: #333;
            transition: opacity 0.3s ease, transform 0.3s ease;
        }
        .course-icon::after {
            content: "←";
            opacity: 0;
            transform: scale(0.8);
        }
        .course-icon:hover::before {
            opacity: 0;
            transform: scale(0.8);
        }
        .course-icon:hover::after {
            opacity: 1;
            transform: scale(1);
        }
        .course-icon:hover {
            transform: translateX(-50%) rotate(360deg);
            box-shadow: 0 4px 12px rgba(74, 144, 226, 0.3);
        }
        /* Заголовок */
        .safronov-form h2 {
            color: #333;
            text-align: center;
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 32px;
            letter-spacing: 0.8px;
            position: relative;
            margin-top: 20px;
        }
        .safronov-form h2::after {
            content: "";
            display: block;
            width: 60px;
            height: 3px;
            background: #4A90E2;
            margin: 12px auto 0;
            border-radius: 2px;
        }
        /* Инпут */
        .safronov-input {
            width: 100%;
            padding: 14px 18px;
            margin-bottom: 24px;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            font-size: 16px;
            transition: all 0.3s ease;
            background-color: white;
            -webkit-appearance: none;
            appearance: none;
        }
        .safronov-input:focus {
            border-color: #4A90E2;
            outline: none;
            box-shadow: 0 0 0 4px rgba(74, 144, 226, 0.15);
        }
        /* Кнопка */
        .safronov-btn {
            width: 100%;
            padding: 14px;
            background-color: #4A90E2;
            color: white;
            font-size: 18px;
            font-weight: 600;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }
        .safronov-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(74, 144, 226, 0.25);
        }
        /* Специальный стиль для выпадающего списка */
        select.safronov-input {
            -webkit-appearance: none;
            appearance: none;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24'%3E%3Cpath fill='%236699FF' d='M7 10l5 5 5-5H7z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 16px center; /* Сдвигаем влево */
            background-size: 16px 16px;
            padding-right: 36px; /* Добавляем место под стрелку */
            transition: transform 0.3s ease;
        }
        .safronov-btn::after {
            content: "📘";
            position: absolute;
            right: 28px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 20px;
            transition: transform 0.3s ease;
        }
        .safronov-btn:hover::after {
            transform: translateY(-50%) rotate(10deg);
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
        /* Улучшенный стиль для радиокнопок */
        .radio-group {
            margin-bottom: 24px;
            background: #F5F7FA;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            padding: 14px 18px;
            transition: all 0.3s ease;
        }
        .radio-group p {
            margin-bottom: 12px;
            color: #555;
            font-size: 16px;
            font-weight: 600;
        }
        .radio-group label {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
            cursor: pointer;
            transition: color 0.3s ease;
        }
        .radio-group input[type="radio"] {
            -webkit-appearance: none;
            appearance: none;
            margin-right: 12px;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            border: 2px solid #e0e0e0;
            background: white;
            transition: all 0.3s ease;
            position: relative;
        }
        .radio-group input[type="radio"]:checked {
            background: #4A90E2;
            border-color: #4A90E2;
        }
        .radio-group input[type="radio"]:checked::after {
            content: "";
            position: absolute;
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: white;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
        }
        .radio-group label:hover {
            color: #4A90E2;
        }
        .radio-group input[type="radio"]:hover {
            border-color: #4A90E2;
        }
        /* Улучшенный стиль для чекбоксов */
        .checkbox-group {
            margin-bottom: 24px;
            background: #F5F7FA;
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            padding: 14px 18px;
            transition: all 0.3s ease;
        }
        .checkbox-group p {
            margin-bottom: 12px;
            color: #555;
            font-size: 16px;
            font-weight: 600;
        }
        .checkbox-group label {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
            cursor: pointer;
            transition: color 0.3s ease;
        }
        .checkbox-group input[type="checkbox"] {
            -webkit-appearance: none;
            appearance: none;
            margin-right: 12px;
            width: 20px;
            height: 20px;
            border: 2px solid #e0e0e0;
            background: white;
            transition: all 0.3s ease;
            position: relative;
        }
        .checkbox-group input[type="checkbox"]:checked {
            border-color: #4A90E2;
        }
        .checkbox-group input[type="checkbox"]:checked::after {
            content: "✔";
            position: absolute;
            top: -4px;
            left: -4px;
            width: 28px;
            height: 28px;
            font-size: 18px;
            line-height: 28px;
            text-align: center;
            color: #4A90E2;
            transform: rotate(0deg);
        }
        .checkbox-group label:hover {
            color: #4A90E2;
        }
        .checkbox-group input[type="checkbox"]:hover {
            border-color: #4A90E2;
        }

        /* Стили для ошибок валидации */
        .error {
            color: #E53935;
            font-size: 14px;
            margin: -20px 0 20px 0;
            padding: 8px 12px;
            background: #FFEBEE;
            border-radius: 8px;
            border-left: 4px solid #E53935;
            display: flex;
            align-items: center;
            animation: slideIn 0.3s ease;
        }

        .error::before {
            content: "⚠️";
            margin-right: 8px;
            font-size: 16px;
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* Подсветка полей с ошибками */
        .safronov-input.error-field {
            border-color: #E53935;
            background-color: #FFF5F5;
        }

        .safronov-input.error-field:focus {
            box-shadow: 0 0 0 4px rgba(229, 57, 53, 0.15);
        }

        .radio-group.error-group,
        .checkbox-group.error-group {
            border-color: #E53935;
            background-color: #FFF5F5;
        }
    </style>
</head>
<body>
    <div class="safronov-form" data-test-id="safronov-form-container">
        <!-- Кликабельная иконка курса -->
        <div class="course-icon" data-test-id="back-to-mainPage" onclick="window.location.href='http://localhost:8080/'" title="Вернуться на главную"></div>
        <h2 data-test-id="form-title">Регистрация в курсе</h2>
        <form:form method="post" action="showDetails" modelAttribute="employee" data-test-id="registration-form">
            <form:input path="employeeName" placeholder="Введите ваше имя" cssClass="safronov-input ${status.error ? 'error-field' : ''}" data-test-id="employee-name-input"/>
            <form:errors path="employeeName" cssClass="error"/>
            <form:input path="employeeSurname" placeholder="Введите вашу фамилию" cssClass="safronov-input ${status.error ? 'error-field' : ''}" data-test-id="employee-surname-input"/>
            <form:errors path="employeeSurname" cssClass="error"/>
            <form:input path="email" placeholder="Введите ваш email" cssClass="safronov-input ${status.error ? 'error-field' : ''}" data-test-id="employee-email-input"/>
            <form:errors path="email" cssClass="error"/>
            <form:select path="javaVersion" cssClass="safronov-input ${status.error ? 'error-field' : ''}" data-test-id="java-version-select">
                <form:option value="" label="Выберите версию Java"/>
                <form:option value="17" label="Java 17"/>
                <form:option value="21" label="Java 21"/>
            </form:select>
            <form:errors path="javaVersion" cssClass="error"/>
            <div class="radio-group ${status.error ? 'error-group' : ''}" data-test-id="auto-test-experience">
                <p>Занимались ли Вы автотестированием ранее?</p>
                <label>
                    <form:radiobutton path="autoTestExperience" value="да"/> Да
                </label>
                <label>
                    <form:radiobutton path="autoTestExperience" value="нет"/> Нет
                </label>
            </div>
            <form:errors path="autoTestExperience" cssClass="error"/>
            <div class="checkbox-group ${status.error ? 'error-group' : ''}" data-test-id="programming-languages">
                <p>Какими языками программирования владеете?</p>
                <label>
                    <form:checkbox path="programmingLanguages" value="java"/> Java
                </label>
                <label>
                    <form:checkbox path="programmingLanguages" value="python"/> Python
                </label>
                <label>
                    <form:checkbox path="programmingLanguages" value="javascript"/> JavaScript
                </label>
                <label>
                    <form:checkbox path="programmingLanguages" value="none"/> Не знаю языка программирования
                </label>
            </div>
            <form:errors path="programmingLanguages" cssClass="error"/>
            <button type="submit" class="safronov-btn" data-test-id="continue-button">Продолжить</button>
        </form:form>
    </div>
    <div class="footer" data-test-id="trainer-footer">Safronov_ID • Тренажер для AQA</div>
    <script>
    function setCookie(name, value, days = 7) {
        const d = new Date();
        d.setTime(d.getTime() + (days*24*60*60*1000));
        document.cookie = name + "=" + encodeURIComponent(value) + ";expires=" + d.toUTCString() + ";path=/";
    }
    function getCookie(name) {
        const v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
        return v ? decodeURIComponent(v[2]) : null;
    }

    function saveAll() {
        const name = document.querySelector('[name="employeeName"]').value;
        const surname = document.querySelector('[name="employeeSurname"]').value;
        const java = document.querySelector('[name="javaVersion"]').value;
        const exp = document.querySelector('[name="autoTestExperience"]:checked')?.value || '';
        const langs = Array.from(document.querySelectorAll('[name="programmingLanguages"]:checked')).map(cb => cb.value);

        const data = {name, surname, java, exp, langs};
        localStorage.setItem('regForm', JSON.stringify(data));
        sessionStorage.setItem('regForm', JSON.stringify(data));
        setCookie('regForm', JSON.stringify(data));
    }

    function loadAll() {
        let data = localStorage.getItem('regForm') || sessionStorage.getItem('regForm') || getCookie('regForm');
        if (!data) return;
        try { data = JSON.parse(data); } catch { return; }
        if (data.name) document.querySelector('[name="employeeName"]').value = data.name;
        if (data.surname) document.querySelector('[name="employeeSurname"]').value = data.surname;
        if (data.java) document.querySelector('[name="javaVersion"]').value = data.java;
        if (data.exp) document.querySelector(`[name="autoTestExperience"][value="${data.exp}"]`)?.click();
        if (data.langs) {
            document.querySelectorAll('[name="programmingLanguages"]').forEach(cb => {
                cb.checked = data.langs.includes(cb.value);
            });
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        loadAll();
        document.querySelectorAll('input, select').forEach(el => {
            el.addEventListener('change', saveAll);
        });
    });
    </script>
</body>
</html>