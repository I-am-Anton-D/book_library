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

Общий вид класса:

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

Как получить элемент:
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