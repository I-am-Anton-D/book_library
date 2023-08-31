# Тестирование UI с использованием Karibu Testing

## Полезные ссылки
- [Ссылка на библиотеку на Github](https://github.com/mvysny/karibu-testing)
- [Базовый пример теста от разработчика](https://github.com/mvysny/t-shirt-shop-example/blob/master/src/test/java/com/vaadin/tshirtshop/ApplicationTest.java)

Karibu-testing - библиотека для эффективного тестирования на стороне сервера
без запуска headless-браузера, лишней отрисовки страницы, и.т.п.

Для данного и схожих проектов (Spring boot + Vaadin 24 + JUnit 5)
был создан абстрактный класс `AbstractUITest`, прячущий "под капот" детали
работы с библиотекой.

Если требуется изменить путь, по которому лежат `View` в поекте, сделать это можно
либо в самом классе, либо в отдельном тесте, переопределив поле `Routes routes`. 

**Важно**: При наследовании от `AbstractUITest`, **UI чистится** между **каждыми** методами-тестами,
если это поведение нежелательно, можно работать с `MockVaadin` вручную.

## Краткая справка
### Общий вид класса:

```java
import com.vaadin.flow.component.UI;
import ru.ntik.book.library.view.AbstractUITest;

class MyViewTest extends AbstractUITest {
    @Test
    void myTest() {
        UI.getCurrent().navigate(MyView.class);
        // your test code...
    }
}
```

### Как получить компонент:
В случае единственного компонента:
```java
@Test
void myTest() {
    _get(parentComponent, Component.class, spec->{/* селектор */});
    /* На выходе получаем соответствующий Vaadin'овский
       компонент и работаем уже с ним */
}
```

Метод `_get()` вернет исключение, если компонентов больше одного.<br>
Если элементов **может быть больше одного** следует использовать метод:
```java
@Test
void myTest() {
    _find(parentComponent, Component.class, spec->{/* селектор */});
    /* На выходе получаем **список** соответствующих Vaadin'овских
       компонентов*/
}
```

### Как изменить значение компонента:

`_setValue(Component, value)`

### Тестирование Grid / TreeGrid
Karibu Testing, в отличие от систем авто-тестирования, основанных на прямой эмуляции работы браузера (Selenium)
не "отрисовывает" контент целиком, вследствие чего для функции `_get()` остается недоступным содержимое
`Grid<T>` `TreeGrid<T>` и, возможно, аналогичных компонентов.

Класс `GridKt` позволяет обойти это ограничение и получить доступ к их содержимому.

Важно отметить, что для доступа к конкретному столбцу `Grid`/`TreeGrid` предварительно
**требуется добавить ключ**, по которому впоследствии его можно будет выбирать:
```java
Grid<T> grid = new Grid<>();
// ...
grid.addColumn(...).setKey("column_key_name");
```

Пока проверены следующие случаи использования:
- `GridKt._clickItem(Grid<T> grid, int rowIndex, String columnKey);` - простейший случай - нажать на саму ячейку
- `GridKt._getCellComponent(Grid<T> grid, int rowIndex, String columnKey)` - получить содержимое ячейки. Возвращает `com.vaadin.flow.component.Component`, который можно привести к требуемому типу
- `GridKt._selectRow(Grid<T> grid, int rowIndex)` - выбрать строку по индексу (**Внимание**: мультиселект **не** поддерживается, но доступен его частный случай при помощи `GridKt._selectAll(Grid<T> grid)`)
- `GridKt._selectAll(Grid<T> grid)` - выбрать все элементы `Grid<T>`. **Внимание**: порядок выбора элементов как в layout **не гарантируется**.
- `GridKt.expectRow(Grid<T>, int rowIndex, String value)` - простой assert на соответствие видимого значения строки `Grid<T>` текстовому значению `value`. Поведение в ситуации с **множеством** столбцов **не проверялось**.