# Chat Bot
**Сложность**: L

Реализовать бота для телеграмма. Учет правил пожарной безопасности.

## Минимальные требования (сложность: L)
- Регистрация / удаление пользователя;
- Проведение первичного инструктажа (в формате вопрос - варианты ответов);
- Повторный инструктаж через заданное время (время настраивается);
- Повторный инструктаж проходит каждый раз, когда пройдет фиксированное время (относительно даты трудоустройства);
- Внесение номера модели и срока годности устройство самоспасателя (УС);
- Уведомление в телеграмме пользователя по факту просрочки срока годности УСа или необходимости пройти инструктаж;

## Рекомендации 
- Используйте Spring Boot;
- Для автоматического тестирования используйте Spring Boot Test;
- Для развертывания инфраструктуры в тестах используйте [testcontainers](https://www.testcontainers.org/);
- Для развертывания необходимой инфраструктуры используйте Docker Desktop;
- Пишите JavaDoc.

## DoD
- Написан код, согласно требованиям;
- Форк от текущего репозитория, оформление PR;
- Состояние хранится в БД (можно H2);
- Вся функциональность покрыта Feature-тестами (при необходимости Unit);
- Документация (описание вашего решения в текущем файле, см. секцию ниже)

# Документация решения (инструкция и описание)

## Дизайн решения:
Основной класс приложения с методом main для запуска телеграм-бота - Application.java
Старт приложения:
- Создает (при необходимости) таблицы в БД: таблица пользователей, таблица вопросов инструктажа, таблица самоспасателей,
таблица уведомлений для пользователей.
- Восстанавливает данные из БД в сущности классов: коллекцию пользователей телеграм-бота - List<User> (+ вложенную 
сущность самоспасателя SelfCareDevice и коллекцию уведомлений пользователя List<Scheduler>), а также коллекцию вопросов 
инструктажа List<Question>.
- Если таблица с вопросами пустая, восстанавливает записи в таблицу из enum QuestionBackUp.
- Создает и регистрирует объект телеграм-бота, запускает сессию телеграм-бота.
Далее созданный бот ожидает входящих сообщений от пользователя.

Обработка входящих сообщений:
Осуществляется в классе Bot в методе onUpdateReceived().
Из входящего сообщения пользователя извлекается идентификатор чата (chatId) и происходит поиск пользователя с таким 
идентификатором в коллекции List<User>. Если пользователь с таким идентификатором не найден - создается новый объект 
класса User (режим пользователя при создании - NONE), добавляется в коллекцию и сохраняется в БД (на данном этапе
сохраняется только chatId - идентификатор пользователя, и дата-время создания пользователя).
Далее, в зависимости от режима пользователя (см.далее), определяется класс-обработчик ответного сообщения, наследуемый 
от абстрактного класса Message. Класс Message содержит метод processUpdate(), перегруженный с разными сигнатурами и 
переопределенный в классах-наследниках.
После того, как класс-обработчик сформировал ответ, бот пытается отправить этот ответ пользователю.

Режимы пользователя (классы-обработчики входящих сообщений):
По умолчанию (при создании и восстановлении пользователя из базы) у всех пользователей бота стартовый режим (Mode = NONE).

- Стартовый режим (NONE): 
Обрабатывается в классе NoneModeMessage.java
Принимает на вход команды, описанные в классе Command.enum, и переводит пользователя в один из режимов (изменением поля
Mode объекта User). Если пользователь прислал сообщение, отличное от одной из обрабатываемых команд - отправляет в ответ
приветственный текст с краткой инструкцией о возможных действиях пользователя.

- Режим регистрации (REGISTRATION):
Обрабатывается в классе RegistrationModeMessage.java
После перехода в режим регистрации, пользователю в режиме "вопрос - ответ пользователя - подтверждение ответа на 
inline-клавиатуре" предлагается ввести Имя, Фамилию и дату трудоустройства. Данные сохраняются в объект User, 
переданный в метод processUpdate(). Если пользователь ошибся, то дополнительная для данного режима команда "/clear" 
позволит удалить введенную информацию из объекта User. В этом случае регистрация начинается заново.
После подтверждения регистрации данные пользователя сохраняются в базу (поля firstName, lastName, employmentDate), а 
пользователю предлагается выбрать частоту напоминаний о прохождении инструктажа. Если пользователь выбрал один из 
вариантов "раз в неделю", "раз в месяц", "раз в три месяца", то создается объект класса Scheduler с текстом напомнинания
об инструктаже, добавляется в коллекцию User.List<Scheduler> и сохраняется в базу.

- Режим регистрации самоспасателя (SELFCARE_DEVICE_INFO):
Обрабатывается в классе SelfcareDeviceInfoModeMessage.java
При переходе в режим регистрации самоспасателя пользователю в режиме "вопрос - ответ пользователя - подтверждение ответа
на inline-клавиатуре" предлагается ввести модель самоспасателя и срок годности самоспасателя. Данные сохраняются во 
вложенный объект selfCareDevice объекта User, переданного в метод processUpdate(). Если поля selfCareDevice у 
пользователя еще нет, то создается новый selfCareDevice. Если пользователь ошибся, то дополнительная для данного режима
команда "/clear" позволит удалить введенную информацию из объекта SelfCareDevice. В этом случае регистрация начинается заново.
После подтверждения окончания регистрации самоспасателя данные самоспасателя сохраняются в базу, и происходит проверка
срока годности самоспасателя. Если самоспасатель просрочен - пользователь получает об этом сообщение сообщение, если 
самоспасатель не просрочен, то создается объект класса Scheduler с текстом о просрочке самоспасателя, добавляется в 
коллекцию User.List<Scheduler> и сохраняется в базу.

- Режим прохождения инструктажа (TRAINING):
Обрабатывается в классе TrainingModeMessage.java
Инструктаж осуществляется в следующем формате: пользователю, после подтверждения перехода к режиму инструктажа, 
предлагается вопрос и 3 варианта ответа на reply-клавиатуре. После выбора ответа, бот присылает результат "Правильно" или
"Неправильно" + правильный вариант ответа, и предлагает перейти к следующему вопросу.
На ввод пользователем любой из команд, бот предоставляет выбор: прервать инструктаж или продолжить и вывести следующий вопрос.
Если пользователь прерывает инструктаж, счетчик вопросов пользователя сбрасывается (новый инструктаж начнется с 1-ого
вопроса), а пользователь возвращается в стартовый режим.

- Режим удаления пользователя (DELETE_USER):
Обрабатывается в классе DeleteModeMessage.java
Пользователю предоставляется выбор на inline-клавиатуре: подтвердить или отменить удаление. Если пользователь
подтверждает удаление, выполняется отмена всех поставленных пользователю уведомлений (отмена задач TimerTask в 
коллекции User.List<Scheduler>), очистка полей объекта User и удаление пользователя из коллекции List<User>, а также
удаление всех, связанных с пользователем (по chatId) записей БД из таблиц USERS, DEVICES, SCHEDULES.
В случае отказа - пользователь возвращается в стартовый режим.

## Сборка проекта:
Осуществляется с помощью плагина spring-boot-maven-plugin командой:
mvn package

## Запуск проекта: 
Собранный .jar запускается из терминала командой:
java -jar katkova.chatbot-0.0.1-SNAPSHOT.jar

## TO DO (не сделано):
- полноценное подключение Spring Boot;
- покрытие кода Unit/Feature-тестами;
- расширение логирования;
- развертывание необходимой инфраструктуры;
- настройка уведомлений пользователем (изменение ранее выбранного варианта уведомлений, возможность задать свою дату);