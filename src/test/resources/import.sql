-- noinspection SqlInsertValuesForFile

INSERT INTO public.author (created, creator, id, description, name) VALUES ('2023-06-07 14:52:59.000000', 10, 6, null, 'Alex Puskin');
INSERT INTO public.author (created, creator, id, description, name) VALUES ('2023-06-07 14:52:59.000000', 10, 7, null, 'Lev Tolstoy');
INSERT INTO public.author (created, creator, id, description, name) VALUES ('2023-06-07 14:52:59.000000', 10, 8, null, 'Maxim Gorkiy');
INSERT INTO public.author (created, creator, id, description, name) VALUES ('2023-06-07 14:52:59.000000', 10, 9, null, 'Sergey Esenin');

INSERT INTO public.publisher (created, creator, id, description, name) VALUES ('2023-06-05 19:43:27.000000', 10, 10, 'Big Publisher', 'Piter');
INSERT INTO public.publisher (created, creator, id, description, name) VALUES ('2023-06-05 19:41:48.000000', 10, 11, '«Эксмо» сегодня — это одно из крупнейших издательств в Европе', 'Эксмо');
INSERT INTO public.publisher (created, creator, id, description, name) VALUES ('2023-06-05 19:40:49.000000', 10, 12, null, 'Диалектика-Вильямс');
INSERT INTO public.publisher (created, creator, id, description, name) VALUES ('2023-06-05 19:43:52.000000', 10, 13, 'Москва', 'ДМК Пресс');

INSERT INTO public.book_definition (page_count, release_year, created, creator, id, cover_type, description, isbn, language, name, publisher_id) VALUES (1000, 2019, '2023-06-04 14:41:14.221314', 10, 1, 'paperback', 'BOOK_DESCRIPTION', '978-5-4461-0512-1', 'RUSSIAN', 'BOOK_1', 10);
INSERT INTO public.book_definition (page_count, release_year,  created, creator, id, cover_type, description, isbn, language, name, publisher_id) VALUES (100, 2012,  '2023-06-04 14:41:15.221314', 10, 2, 'paperback', 'BOOK_DESCRIPTION 2', '978-5-4461-0512-2', 'RUSSIAN', 'BOOK_2', 10);
INSERT INTO public.book_definition (page_count, release_year,  created, creator, id, cover_type, description, isbn, language, name, publisher_id) VALUES (200, 2003, '2023-06-04 14:41:16.221314', 10, 3, 'paperback', 'BOOK_DESCRIPTION 3', '978-5-4461-0512-3', 'RUSSIAN', 'BOOK_3', 10);
INSERT INTO public.book_definition (page_count, release_year, created, creator, id, cover_type, description, isbn, language, name) VALUES (50, 2005, '2023-06-04 14:41:17.221314', 10, 4, 'paperback', 'BOOK_DESCRIPTION 3 ', '978-5-4461-0512-4', 'RUSSIAN', 'BOOK_4');
INSERT INTO public.book_definition (page_count, release_year, created, creator, id, cover_type, description, isbn, language, name) VALUES (2000, 2006, '2023-06-04 14:41:18.221314', 10, 5, 'paperback', 'BOOK_DESCRIPTION', '978-5-4461-0512-5', 'RUSSIAN', 'BOOK_5');

INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (6, 1);
INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (7, 1);
INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (8, 1);
INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (6, 2);
INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (6, 3);

INSERT INTO public.book_to_book (book_one, book_two) VALUES (1, 2);
INSERT INTO public.book_to_book (book_one, book_two) VALUES (1, 3);
INSERT INTO public.book_to_book (book_one, book_two) VALUES (3, 4);