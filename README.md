# telegram-bot

Telegram бот для автоматизации задач старосты группы.

## Как запустить?

1. Создать нового бота у [@BotFather](https://t.me/BotFather) при помощи команды `/newbot`.
2. В файле `docker-compose.yml` присвоить переменным окружения значения токена и имени пользователя бота.
3. Запустить приложение при помощи Docker Compose:

  ```
  $ docker compose up -d db
  $ docker compose up -d app
  ```

## Лицензия

MIT
