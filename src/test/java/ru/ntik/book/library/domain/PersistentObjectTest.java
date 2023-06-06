package ru.ntik.book.library.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.ntik.book.library.testutils.TestUtils.*;

@DisplayName("Тесты сущности PersistentObject")
@Execution(ExecutionMode.CONCURRENT)

class PersistentObjectTest {

    @Test
    @DisplayName("Должен создаться экземпляр")
    void createInstanceTest() {
        BookDefinition bd = new BookDefinition(BOOK_NAME, BOOK_DESC, CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE);

        assertThat(bd).isNotNull();
        assertThat(bd.getId()).isNull();
        assertThat(bd.getName()).isEqualTo(BOOK_NAME);
        assertThat(bd.getDescription()).isEqualTo(BOOK_DESC);
        assertThat(bd.getCreator()).isEqualTo(CREATOR);
        assertThat(bd.getPrintInfo().getReleaseYear()).isEqualTo(RELEASE_YEAR);
        assertThat(bd.getPrintInfo().getCoverType()).isEqualTo(COVER_TYPE);
        assertThat(bd.getPrintInfo().getIsbn()).isEqualTo(ISBN);
        assertThat(bd.getCreated()).isNull();
    }

    @Test
    @DisplayName("Не создать без имени и с пустым")
    void nameIsMandatory() {
        assertThrows(NullPointerException.class,
                () -> new BookDefinition(null, BOOK_DESC, CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE));
        assertThrows(IllegalArgumentException.class,
                () -> new BookDefinition("", BOOK_DESC, CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE));

        assertThatCode(
                () -> new BookDefinition("A", BOOK_DESC, CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE)).doesNotThrowAnyException();
    }


    @Test
    @DisplayName("Не создать с длинным именем")
    void checkLengthName() {
        String s127 = "10LTSVQcLtJCsEq3oz6kVHk5y4dkLkAbX4hVhK0qAYIlz7tzedyQ4J27BNlo6Hlje2mGWUyBjqw6UD5lFrXodUFZ97XgT0gE3uGmdrdVeRAhQBdH84sUr6Qsd0JYkRo";
        String s128 = "10LTSVQcLtJCsEq3oz6kVHk5y4dkLkAbX4hVhK0qAYIlz7tzedyQ4J27BNlo6Hlje2mGWUyBjqw6UD5lFrXodUFZ97XgT0gE3uGmdrdVeRAhQBdH84sUr6Qsd0JYkRoC";
        String s129 = "10LTSVQcLtJCsEq3oz6kVHk5y4dkLkAbX4hVhK0qAYIlz7tzedyQ4J27BNlo6Hlje2mGWUyBjqw6UD5lFrXodUFZ97XgT0gE3uGmdrdVeRAhQBdH84sUr6Qsd0JYkRoCF";

        assertThat(s127).hasSize(127);
        assertThat(s128).hasSize(128);
        assertThat(s129).hasSize(129);

        assertThatCode(() ->
                new BookDefinition(s127, BOOK_DESC, CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE)).doesNotThrowAnyException();
        assertThatCode(() ->
                new BookDefinition(s128, BOOK_DESC, CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE)).doesNotThrowAnyException();
        assertThrows(IllegalArgumentException.class,
                () -> new BookDefinition(s129, BOOK_DESC, CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE));
    }

    @Test
    @DisplayName("Description не обязателен, не не может быть пустым")
    void descriptionIsOptional() {
        assertThatCode(() ->
                new BookDefinition(BOOK_NAME, null, CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE)).doesNotThrowAnyException();
        assertThrows(IllegalArgumentException.class,
                () -> new BookDefinition(BOOK_NAME, "", CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE));
    }

    @Test
    @DisplayName("Длинное описание")
    void longDesc() {
        String longDesc = """
                Эта книга воплощает знания и опыт работы авторов с каркасом Spring Framework и сопутствующими технологиями удаленного взаимодействия,
                Hibernate, EJB и пр. Она дает возможность читателю не только усвоить основные понятия и принципы работы с Spring Framework, но и
                научиться рационально пользоваться этим каркасом для построения различных уровней и частей корпоративных приложений на языке Java,
                включая обработку транзакций, представление веб-содержимого и прочего содержимого, развертывание и многое другое. Полноценные примеры
                 подобных приложений, представленные в этой книге, наглядно демонстрируют особенности совместного применения различных технологий и
                 методик разработки приложений в Spring.

                Пятое издание этой книги, давно уже пользующейся успехом у читателей, обновлено по новой версии Spring Framework 5 и является
                самым исчерпывающим и полным руководством по применению Spring среди всех имеющихся. В нем представлен новый функциональный
                 каркас веб-приложений, микрослужбы, совместимость с версией Java 9 и прочие функциональные возможности Spring.

                Прочитав эту обстоятельную книгу, вы сможете включить в арсенал своих средств весь потенциал
                Spring для основательного построения сложных приложений. Гибкий, легковесный каркас Spring Framework
                 с открытым кодом продолжает оставаться фактически ведущим в области разработки корпоративных
                 приложений на языке Java и самым востребованным среди разработчиков и программирующих на Java.

                Он превосходно взаимодействует с другими гибкими, легковесными технологиями
                Java с открытым кодом, включая Hibernate, Groovy, MyBatis и прочие, а также с платформами Java EE и JPA 2.

                Эта книга окажет вам помощь в следующем:
                -Выявить новые функциональные возможности в версии Spring Framework 5

                -Научиться пользоваться Spring Framework вместе с Java 9

                -Овладеть механизмом доступа к данным и обработки транзакций

                -Освоить новый функциональный каркас веб-приложений

                -Научиться создавать микрослужбы и другие веб-службы


                Об авторах

                Юлиана Козмина (Iuliana Cosmina) - разработчик веб-приложений и профессиональный
                пользователь Spring, аттестованный в компании Pivotal, разработавшей Spring Framework,
                Boot и другие инструментальные средства. Ее перу принадлежит ряд книг, вышедших
                в издательстве Apress и посвященных аттестации и веб-разработке в Spring Framework.
                Она работает архитектором программного обеспечения в компании Bearing Point Software и
                активно участвует в разработке программного обеспечения с открытым кодом в GitHub, о
                бсуждении насущных вопросов программирования в Stack Overflow и прочих ресурсах.


                Роб Харроп (Rob Harrop) работает консультантом по программному обеспечению,
                специализируясь на выпуске высокопроизводительных корпоративных приложений
                с высокой степенью масштабируемости. У него имеется немалый опыт разработки
                архитектуры программного обеспечения и особая склонность уяснять и разрешать
                сложные вопросы проектирования. Обладая солидными знаниями платформ Java и .NET,
                он успешно осуществил на них немало проектов. У него имеется также немалый опыт и в
                других областях, включая розничную торговлю и государственную службу.
                Авторству Харропа принадлежит пять книг, в том числе настоящее,
                 пятое издание этой книги, повсеместно признанной как исчерпывающий источник по Spring Framework.


                Крис Шефер (Chris Schaefer) - главный разработчик программного обеспечения
                для проектов Spring в компании Pivotal, разработавшей Spring Framework,
                Boot и другие инструментальные средства.


                Кларенс Хо (Clarence Ho) работает ведущим архитектором приложений на Java
                в компании SkywideSoft Technology Limited, занимающейся консультациями по программному
                обеспечению и расположенной в Гонконге. Проработав в области информационных технологий более двадцати лет,
                Кларенс руководил многими проектами по разработке приложений для внутреннего потребления,
                а также оказывал консультационные услуги по корпоративным решениям.
                Комментарий
                5-е издание""";

        assertThatCode(() ->
                new BookDefinition(BOOK_NAME, longDesc, CREATOR, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE)).doesNotThrowAnyException();
    }

    @DisplayName("Не создать с нуловым creator")
    @Test
    void creatorIsNull() {
        assertThrows(NullPointerException.class, ()->
        new BookDefinition(BOOK_NAME, BOOK_DESC, null, RELEASE_YEAR, COVER_TYPE, ISBN, PAGE_COUNT, BOOK_LANGUAGE));
    }

    @Test
    @DisplayName("Проверят deepEquals")
    void deepEqualsTest() {
        ImplPo po1 = new ImplPo();
        ImplPo po2 = new ImplPo();

        assertThat(po1.deepEquals(po2)).isTrue();

        po1.setName("FirstName");
        po2.setName("SecondName");

        assertThat(po1.deepEquals(po2)).isFalse();

        po1.setName(po2.getName());
        po1.setDescription("First Description");
        po2.setDescription("Second Description");

        assertThat(po1.deepEquals(po2)).isFalse();

        po1.setDescription(po2.getDescription());
        assertThat(po1.deepEquals(po2)).isTrue();

        final Field id = ReflectionUtils.findField(ImplPo.class, "id", Long.class);
        assert id != null;
        id.setAccessible(true);
        ReflectionUtils.setField(id, po1, 10L);
        ReflectionUtils.setField(id, po2, 10L);
        assertThat(po1.deepEquals(po2)).isTrue();

        ReflectionUtils.setField(id, po2, 11L);
        assertThat(po1.deepEquals(po2)).isFalse();

        assertThat(po1.deepEquals(po1)).isTrue();
        assertThat(po1.deepEquals(String.valueOf(1L))).isFalse();
    }

    @Test
    @DisplayName("to String test")
    void toStringTest() {
        ImplPo po = new ImplPo();
        assertThat(po.getId()).isNull();
        assertThat(po.toString()).contains("ImplPo");
    }

    public static class ImplPo extends PersistentObject { }
}