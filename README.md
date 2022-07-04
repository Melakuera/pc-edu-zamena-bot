## Описание

Данный бот автоматически посылает **замены**  соответствующих групп в телеграмм-группы, которые подписались на **замены**

> Под словом замены имеется ввиду перестановка пар по разным причинам

Деятельность человека и использование дорогих бумаг сведено до минимума. Студентам больше нет необходимости отправлять, закреплять замены и предупреждать однокурсников при входящих замен. Место всего это делает данный бот, что обеспечивает автоматизацию

## ZamenaPinner
- работает через **webhook** 
- развернут в **Heroku**
- хранит свое состояние в **MongoDB**, который свою очередь развёрнут в **Mongo Atlas**
- для динамических запросов в **Telegram Bot API** использована данная [**библиотека**](https://github.com/rubenlagus/TelegramBots)
- обрабатывает PDF документы с помощью **Apache PDFBox** 
- парсит сайт с использованием **Jsoup**
-  держится все это благодаря мощному фреймворку **Spring Boot**

Опробовать: [**@zamena_pinner_bot**](https://t.me/zamena_pinner_bot)


## Отдельное спасибо
Азиму, который внёс эту идею

