# java-myblog

Приложение-блог с использованием Spring Framework с базовой функциональностью: публикации, комметании, лайки, теги, поиск (по тексту и названию)

## Структура приложения

### Главная лента

**Превью поста** включает:
* Заголовок поста
* Миниатюру изображения
* Краткое описание (до 3 строк первого абзаца)
* Количество комментариев
* Количество лайков
* Теги поста

**Функционал ленты**:
* Отображение постов сверху вниз
* Фильтрация по тегам
* Пагинация (10 постов на странице)

### Добавление поста

В ленте присутствует кнопка добавления поста, открывающая форму с полями:
* Заголовок поста
* Загрузка изображения
* Текстовое поле для основного контента
* Поле для добавления тегов

### Страница поста

**Основные элементы**:
* Заголовок поста
* Изображение
* Разбитый на абзацы текст
* Теги поста
* Кнопки управления (редактирование и удаление)
* Кнопка добавления комментария
* Кнопка лайка с счётчиком
* Список комментариев

**Функционал комментариев**:
* Добавление новых комментариев
* Редактирование существующих комментариев
* Удаление комментариев

**Примечание**: функционал редактирования поста идентичен процессу создания нового поста.

## Эндпойнты

| **Endpoint** | **Parameters** | **Response** |
|--------------|----------------|--------------|
| GET "/" | - | Редирект на "/posts" |
| GET "/posts" | search (строка поиска, по умолчанию: "")<br>pageSize (число постов на странице, по умолчанию: 10)<br>pageNumber (номер страницы, по умолчанию: 1) | Шаблон "posts.html"<br>Модель: posts (List<Post>), search, paging (pageNumber, pageSize, hasNext, hasPrevious) |
| GET "/posts/{id}" | id | Шаблон "post.html"<br>Модель: post (id, title, text, imagePath, likesCount, comments) |
| GET "/posts/add" | - | Шаблон "add-post.html" |
| POST "/posts" | multipart/form-data<br>title (название поста)<br>text (текст поста)<br>image (MultipartFile)<br>tags (список тегов, по умолчанию: "") | Редирект на созданный "/posts/{id}" |
| GET "/images/{id}" | id | Набор байт картинки поста |
| POST "/posts/{id}/like" | id<br>like (true/false) | Редирект на "/posts/{id}" |
| POST "/posts/{id}/edit" | id | Редирект на "add-post.html"<br>Модель: post (id, title, text, imagePath, likesCount, comments) |
| POST "/posts/{id}" | multipart/form-data<br>id<br>title<br>text<br>image (может быть null)<br>tags (по умолчанию: "") | Редирект на отредактированный "/posts/{id}" |
| POST "/posts/{id}/comments" | id<br>text (текст комментария) | Редирект на "/posts/{id}" |
| POST "/posts/{id}/comments/{commentId}" | id<br>commentId<br>text (текст комментария) | Редирект на "/posts/{id}" |
| POST "/posts/{id}/comments/{commentId}/delete" | id<br>commentId | Редирект на "/posts/{id}" |
| POST "/posts/{id}/delete" | id | Редирект на "/posts" |
