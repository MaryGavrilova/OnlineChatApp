# Проект "Сетевой чат"
## Общее описание проекта
**Сетевой чат** представляет собой приложение, написанное на языке Java, позволяющее обмениваться текстовыми сообщениями по сети с помощью консоли (терминала) между двумя и более пользователями.
### Приложение содержит две части:
1. **Сервер чата** - одновременно ожидает подключения пользователей, принимает и рассылает поступившие сообщения участникам чата.
2. **Клиент чата** - подключается к серверу чата и параллельно осуществляет отправку и получение новых сообщений из чата.
### Фунционал сервера:
* установка порта для подключения клиентов через файл настроек;
* возможность для клиента подключиться к серверу в любой момент и присоединиться к чату;
* отправка сообщений всем клиентам, подключившимся к чату;
* запись всех отправленных через сервер сообщений с указанием имени участника чата и времени отправки, (при каждом запуске приложения файл дополняется).
### Фунционал клиента:
* выбор имени для участия в чате;
* установка порта для подключения к серверу через файл настроек;
* подключение к серверу, отправка сообщений в чат, получение и вывод в консоль сообщений из чата;
* выход из чата путем ввода в консоль команды выхода - “/exit”;
* запись всех отправленных и полученных сообщений с указанием имени участника чата и времени отправки, (при каждом запуске приложения файл дополняется).
## Технологии
* Java 16;
* библиотека GSON для работы с файлами формата JSON;
* библиотека Apache Commons Lang 3; 
* библиотека JUnit для модульного тестирования;
* фреймворк Mockito для тестирования с использованием заглушек.
## Архитектура приложения
**Реализация многопоточности**
* При старте сервера запускается основной поток, который отвечает за подключение пользователей к чату. Затем задача по дальнейшей обработке сообщений от конкретного клиента отправляется в отдельный поток пула.
* При старте клиента запускается основной поток, который отвечает за подключение к серверу и выбор имени для участия в чате, после чего добавляются еще два потока:
  1. Chat Writer (отвечает за считывание сообщений из консоли и отправки их на сервер), 
  2. Chat Reader (отвечает за получение сообщений от сервера и вывод их в консоль).
**Протокол обмена сообщениями между клиентом и сервером**
Сообщение от клиента, содержащее информацию об имени участника чата и сам текст сообщения, конвертируются в JSON строку, после чего отправляются на сервер, где при получении обратно конвертируются в экземпляр Java класса. Таким образом, сервер и клиент обмениваются данными в формате JSON.
## Тестирование приложения
Приложение было протестировано на двух уровнях:
1. Модульное тестирование: проверка, что каждый модуль работает в отдельности: код покрыт unit-тестами (успешно пройдено 40 тестов);
2. Интеграционное тестирование: проверка, что модули работают вместе: проведены успешно тест сервера, с помощью telnet, тест на взаимодействие сервера и клиента, а также тест работоспособности приложения при подключении нескольких клиентов.
