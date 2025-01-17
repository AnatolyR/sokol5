# language: ru
Функция: Входящий документ

    Сценарий: Создание входящего документа
        Дано пользователь 'admin' с паролем 'admin' выполнивший вход
        Если открыт Список документов
        И нажать меню 'Создать'
        И нажать пункт меню 'Входящий'
        То в заголовке карточки документа будет
            | Входящий № от () |
            | Новый документ   |
            | Статус: Черновик |
        И в поле 'Заголовок' ввести 'Автоматическое тестирование'
        И в поле 'Комментарий' ввести 'Комментарий'
        И в селектор 'Вид документа' ввести 'Акт'
        И в селектор 'Адресат' ввести 'Поляков И'
        И в селектор 'Адресаты (копии)' ввести 'Ивашов В'
        И в селектор 'Адресаты (копии)' ввести 'Беломестов И'
        #------------------------------------------
        И в селектор 'Корреспондент' ввести 'КАПИТАЛ'
        И в селекторе с возможностью добавления 'Кем подписано' ввести 'Васин'
        И в селекторе с возможностью добавления 'Исполнитель (исх)' ввести 'Кирилченко'
        И в поле 'Исходящий номер' ввести 'ИС-2019-123'
        И в поле даты 'Исходящая дата' ввести '14.03.2019 0:00'
        #------------------------------------------
        И в поле 'Номер документа' ввести 'ВХ-20-123'
        И в поле 'Дело' ввести '03-04'
        И в поле 'Количество листов' ввести '2'
        И в поле 'Количество приложений' ввести '0'
        #------------------------------------------
        И в поле даты 'Дата регистрации' ввести '15.03.2019 0:00'
        И в селектор 'Регистратор' ввести 'Карандашов Б'
        И в поле даты 'Дата исполнения' ввести '16.03.2019 0:00'
        И в селектор 'Исполнитель' ввести 'Балашова Д'
        И в селектор 'Контроллер' ввести 'Алексеева А'
        И в селектор 'Способ доставки' ввести 'Почта'
        #------------------------------------------
        И прокрутить страницу наверх
        И нажать кнопку 'Сохранить'
        #------------------------------------------
        То будет выведено информационное сообщение 'Документ сохранен'
        И в поле 'Заголовок' будет 'Новый документАвтоматическое тестирование'
        И в поле 'Комментарий' будет 'Комментарий'
        И в селекторе 'Вид документа' будет 'Акт'
        И в селекторе 'Адресат' будет 'Поляков И. В.'
        И в селекторе 'Адресаты (копии)' будет 'Ивашов В. Н.,Беломестов И. М.'
        #------------------------------------------
        И в селекторе 'Корреспондент' будет 'КАПИТАЛ, деловой центр'
        И в селекторе 'Кем подписано' будет 'Васин И. Ю.'
        И в селекторе 'Исполнитель (исх)' будет 'Кирилченко И. П.'
        И в поле 'Исходящий номер' будет 'ИС-2019-123'
        И в селекторе 'Исходящая дата' будет '14.03.2019'
        #------------------------------------------
        И в поле 'Номер документа' будет 'ВХ-20-123'
        И в поле 'Дело' будет '03-04'
        И в поле 'Количество листов' будет '2'
        И в поле 'Количество приложений' будет '0'
        #------------------------------------------
        И в селекторе 'Дата регистрации' будет '15.03.2019'
        И в селекторе 'Регистратор' будет 'Карандашов Б. С.'
        И в селекторе 'Дата исполнения' будет '16.03.2019'
        И в селекторе 'Исполнитель' будет 'Балашова Д. М.'
        И в селекторе 'Контроллер' будет 'Алексеева А. Е.'
        И в селекторе 'Способ доставки' будет 'Почта'
        #------------------------------------------

    Сценарий: У созданного входящего документа все поля обязательные
        Дано пользователь 'test1' с паролем 'test1' выполнивший вход
        Если открыт Список документов
        И нажать меню 'Создать'
        И нажать пункт меню 'Входящий'
        То в заголовке карточки документа будет
            | Входящий № от () |
            | Новый документ   |
            | Статус: Черновик |
        И нажать кнопку 'Сохранить'
        То будет выведено модальное окно с заголовком 'Некорректно заполненные поля' и сообщением 'Адресат, Вид документа, Дата регистрации, Комментарий, Дело, Регистратор, Номер документа, Способ доставки, Дата исполнения, Исполнитель, Контроллер, documentGroup, Исходящая дата, Исходящий номер, Кем подписано, Исполнитель (исх), Корреспондент'

    Сценарий: Открытие существующего входящего документа
        Дано пользователь 'admin' с паролем 'admin' выполнивший вход
        Если открыт Список документов
        И открыть папку 'Входящие'
        И нажать на название документа 'Запрос даных для выполнения работ 1'
        То в заголовке карточки документа будет
            | Входящий № 131 от 03.03.2019 (Запрос) |
            | Запрос даных для выполнения работ 1 |
            | Статус: Черновик |
        И в поле 'Заголовок' будет 'Запрос даных для выполнения работ 1'
        И в поле 'Комментарий' будет 'Необходимо предоставить все нужные данные'
        И в селекторе 'Вид документа' будет 'Запрос'
        И в селекторе 'Адресат' будет 'Ивашова А. Е.'
        И в селекторе 'Адресаты (копии)' будет 'Агапов Н. В.,Виноградова А. А.'
        #------------------------------------------
        И в селекторе 'Корреспондент' будет 'КАПИТАЛ, деловой центр'
        И в селекторе 'Кем подписано' будет 'Васин И. Ю.'
        И в селекторе 'Исполнитель (исх)' будет 'Кирилченко И. П.'
        И в поле 'Исходящий номер' будет '0012'
        И в селекторе 'Исходящая дата' будет '14.02.2019'
        #------------------------------------------
        И в поле 'Номер документа' будет '131'
        И в поле 'Дело' будет '52 Запросы'
        И в поле 'Количество листов' будет '3'
        И в поле 'Количество приложений' будет '1'
        #------------------------------------------
        И в селекторе 'Дата регистрации' будет '03.03.2019'
        И в селекторе 'Регистратор' будет 'Енотина А. В.'
        И в селекторе 'Дата исполнения' будет '14.08.2019'
        И в селекторе 'Исполнитель' будет 'Луков Б. П.'
        И в селекторе 'Контроллер' будет 'Карандашов К. Н.'
        И в селекторе 'Способ доставки' будет 'Почта'
        #------------------------------------------

    Сценарий: Очищение полей входящего документа
        Дано пользователь 'admin' с паролем 'admin' выполнивший вход
        Если открыт Список документов
        И открыть папку 'Входящие'
        И нажать на название документа 'Запрос даных для выполнения работ 2'
        То в заголовке карточки документа будет
            | Входящий № 131 от 03.03.2019 (Запрос) |
            | Запрос даных для выполнения работ 2 |
            | Статус: Черновик |
        И в поле 'Комментарий' будет 'Необходимо предоставить все нужные данные'
        И в селекторе 'Вид документа' будет 'Запрос'
        И в селекторе 'Адресат' будет 'Ивашова А. Е.'
        И в селекторе 'Адресаты (копии)' будет 'Агапов Н. В.,Виноградова А. А.'
        #------------------------------------------
        И в селекторе 'Корреспондент' будет 'КАПИТАЛ, деловой центр'
        И в селекторе 'Кем подписано' будет 'Васин И. Ю.'
        И в селекторе 'Исполнитель (исх)' будет 'Кирилченко И. П.'
        И в поле 'Исходящий номер' будет '0012'
        И в селекторе 'Исходящая дата' будет '14.02.2019'
        #------------------------------------------
        И в поле 'Номер документа' будет '131'
        И в поле 'Дело' будет '52 Запросы'
        И в поле 'Количество листов' будет '3'
        И в поле 'Количество приложений' будет '1'
        #------------------------------------------
        И в селекторе 'Дата регистрации' будет '03.03.2019'
        И в селекторе 'Регистратор' будет 'Енотина А. В.'
        И в селекторе 'Дата исполнения' будет '14.08.2019'
        И в селекторе 'Исполнитель' будет 'Луков Б. П.'
        И в селекторе 'Контроллер' будет 'Карандашов К. Н.'
        И в селекторе 'Способ доставки' будет 'Почта'
        #------------------------------------------
        И нажать кнопку 'Редактировать'
        То в заголовке карточки документа будет
            | Входящий № 131 от 03.03.2019 (Запрос) |
            | Запрос даных для выполнения работ 2 |
            | Статус: Черновик |
        И очистить поле 'Комментарий'
        И очистить селектор 'Вид документа'
        И очистить селектор 'Адресат'
        И очистить селектор 'Адресаты (копии)'
#        #------------------------------------------
        И очистить селектор 'Корреспондент'
        И очистить селектор 'Кем подписано'
        И очистить селектор 'Исполнитель (исх)'
        И очистить поле 'Исходящий номер'
        И очистить дату 'Исходящая дата'
#        #------------------------------------------
        И очистить поле 'Номер документа'
        И очистить поле 'Дело'
        И очистить поле 'Количество листов'
        И очистить поле 'Количество приложений'
#        #------------------------------------------
        И очистить дату 'Дата регистрации'
        И очистить селектор 'Регистратор'
        И очистить дату 'Дата исполнения'
        И очистить селектор 'Исполнитель'
        И очистить селектор 'Контроллер'
        И очистить селектор 'Способ доставки'
#        #------------------------------------------
        И прокрутить страницу наверх
        И нажать кнопку 'Сохранить'
#        #------------------------------------------
        То будет выведено информационное сообщение 'Документ сохранен'
        И в поле 'Комментарий' будет ''
        И в селекторе 'Вид документа' будет ''
        И в селекторе 'Адресат' будет ''
        И в селекторе 'Адресаты (копии)' будет ''
        #------------------------------------------
        И в селекторе 'Корреспондент' будет ''
        И в селекторе 'Кем подписано' будет ''
        И в селекторе 'Исполнитель (исх)' будет ''
        И в поле 'Исходящий номер' будет ''
        И в селекторе 'Исходящая дата' будет ''
        #------------------------------------------
        И в поле 'Номер документа' будет ''
        И в поле 'Дело' будет ''
        И в поле 'Количество листов' будет ''
        И в поле 'Количество приложений' будет ''
        #------------------------------------------
        И в селекторе 'Дата регистрации' будет ''
        И в селекторе 'Регистратор' будет ''
        И в селекторе 'Дата исполнения' будет ''
        И в селекторе 'Исполнитель' будет ''
        И в селекторе 'Контроллер' будет ''
        И в селекторе 'Способ доставки' будет ''
        #------------------------------------------