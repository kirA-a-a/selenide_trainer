#!/bin/bash

echo "=== Тестирование JWT с автоматической солью ==="
echo

# Базовый URL
BASE_URL="http://localhost:8080"

echo "1. Получение JWT токена с автоматической солью..."
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "jwt", "password": "jwt"}')

echo "Ответ: $TOKEN_RESPONSE"
echo

# Извлекаем токен из ответа
TOKEN=$(echo $TOKEN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    echo "2. Получен токен: ${TOKEN:0:50}..."
    echo

    echo "3. Проверка информации о токене..."
    TOKEN_INFO=$(curl -s -X GET "$BASE_URL/auth/token-info" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Информация о токене: $TOKEN_INFO"
    echo

    echo "4. Проверка валидности токена..."
    VALIDATE_RESPONSE=$(curl -s -X GET "$BASE_URL/auth/validate" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Результат валидации: $VALIDATE_RESPONSE"
    echo

    echo "5. Проверка Bearer токена..."
    BEARER_RESPONSE=$(curl -s -X GET "$BASE_URL/auth/check-bearer-header" \
      -H "Authorization: Bearer $TOKEN")
    
    echo "Результат проверки Bearer: $BEARER_RESPONSE"
    echo

    echo "6. Тест с пользовательской солью..."
    CUSTOM_SALT_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
      -H "Content-Type: application/json" \
      -d '{"username": "jwt", "password": "jwt", "salt": "my_custom_salt"}')
    
    echo "Ответ с пользовательской солью: $CUSTOM_SALT_RESPONSE"
    echo

else
    echo "Ошибка: Не удалось получить токен"
fi

echo "=== Тест завершен ===" 