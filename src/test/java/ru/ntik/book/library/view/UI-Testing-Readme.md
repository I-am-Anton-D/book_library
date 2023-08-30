# Тестирование UI с использованием Karibu Testing

## Полезные ссылки
- [Ссылка на библиотеку на Github](https://github.com/mvysny/karibu-testing)
- [Базовый пример теста от разработчика](https://github.com/mvysny/t-shirt-shop-example/blob/master/src/test/java/com/vaadin/tshirtshop/ApplicationTest.java)

## Краткая справка

Karibu-testing - библиотека для эффективного тестирования на стороне сервера
без запуска headless-браузера, лишней отрисовки страницы, и.т.п.

Для данного и схожих проектов (Spring boot + Vaadin 24 + JUnit 5)
был создан абстрактный класс UI-тестов,
от следует наследоваться всем прочим UI-тестам.

**Внимание:** между каждыми тестами UI чистится, если это поведение нежелательно,
можно не наследоваться от `AbstractUITest`.

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

### Как получить элемент:
```java
@Test
void myTest() {
    _get(Element.class, spec->{/* селектор */});
    /* На выходе получаем соответствующий Vaadin'овский
       компонент и работаем уже с ним */
}
```

Для изменения значения элемента можно использовать метод:

`_setValue(Element, value)`.

### Тестирование Grid / TreeGrid
Karibu Testing, в отличие от систем авто-тестирования, основанных на прямой симуляции работы браузера
не "отрисовывает" контент целиком, вследствие чего функции _get() остается недоступным содержимое
`Grid<T>` `TreeGrid<T>` и, возможно, аналогичных компонентов.

Класс `GridKt` позволяет обойти это ограничение и получить доступ к их содержимому.

Важно отметить, что для доступа к конкретному столбцу `Grid`/`TreeGrid` требуется предварительно
добавить ему параметр - ключ, по которому впоследствии его можно будет выбирать:
```java
Grid<T> grid = new Grid<>();
// ...
grid.addColumn(...).setKey("column_key_name");
```

Пока проверены следующие случаи использования:
- `GridKt._clickItem(Grid<T> grid, int rowIndex, String columnKey);` - простейший случай - нажать на саму ячейку
- `GridKt._getCellComponent(Grid<T> grid, int rowIndex, String columnKey)` - получить содержимое ячейки. Возвращает `com.vaadin.flow.component.Component`, который можно привести к требуемому типу