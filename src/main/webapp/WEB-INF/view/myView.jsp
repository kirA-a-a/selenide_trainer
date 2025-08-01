<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Список страниц</title>
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

        /* Контейнер таблицы */
        .safronov-table {
            background: white;
            border-radius: 16px;
            padding: 40px;
            width: 100%;
            max-width: 600px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            position: relative;
            margin: 32px 0 0 0;
        }

        /* Иконка таблицы */
        .safronov-table::before {
            content: "📘";
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

        /* Заголовок таблицы */
        .safronov-table h2 {
            color: #333;
            text-align: center;
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 32px;
            letter-spacing: 0.8px;
            position: relative;
        }

        .safronov-table h2::after {
            content: "";
            display: block;
            width: 60px;
            height: 3px;
            background: #4A90E2;
            margin: 12px auto 0;
            border-radius: 2px;
        }

        /* Стили таблицы */
        .safronov-table table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 24px;
        }

        .safronov-table th,
        .safronov-table td {
            padding: 16px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        .safronov-table th {
            color: #666;
            font-weight: 600;
            background: #f9f9f9;
        }

        /* Стиль ссылки в таблице */
        .safronov-link {
            color: #4A90E2;
            text-decoration: none;
            cursor: pointer;
            font-weight: 600;
            transition: color 0.3s ease;
        }

        .safronov-link:hover {
            color: #357ABD;
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
<div class="safronov-table" data-test-id="safronov-table-container">
    <h2 data-test-id="table-title">Список страниц для тренировки</h2>
    <table data-test-id="training-pages-table">
        <tr>
            <th>Название страницы</th>
            <th>Описание</th>
        </tr>
        <tr>
            <td>
                <span class="safronov-link" data-test-id="ask-details-link" onclick="window.location.href='http://localhost:8080/askDetails'">
                    Форма регистрации
                </span>
            </td>
            <td>Страница для практики работы с распространенными UI элементами</td>
        </tr>
    </table>
</div>
<div class="footer" data-test-id="trainer-footer">Safronov_ID • Тренажер для AQA</div>
</body>
</html>