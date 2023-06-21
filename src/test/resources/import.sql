-- noinspection SqlInsertValuesForFile

INSERT INTO public.author (created, creator, id, description, name) VALUES ('2023-06-07 14:52:59.000000', 10, 6, null, 'Alex Puskin');
INSERT INTO public.author (created, creator, id, description, name) VALUES ('2023-06-07 14:52:59.000000', 10, 7, null, 'Lev Tolstoy');
INSERT INTO public.author (created, creator, id, description, name) VALUES ('2023-06-07 14:52:59.000000', 10, 8, null, 'Maxim Gorkiy');
INSERT INTO public.author (created, creator, id, description, name) VALUES ('2023-06-07 14:52:59.000000', 10, 9, null, 'Sergey Esenin');

INSERT INTO public.publisher (created, creator, id, description, name) VALUES ('2023-06-05 19:43:27.000000', 10, 10, 'Big Publisher', 'Piter');
INSERT INTO public.publisher (created, creator, id, description, name) VALUES ('2023-06-05 19:41:48.000000', 10, 11, '«Эксмо» сегодня — это одно из крупнейших издательств в Европе', 'Эксмо');
INSERT INTO public.publisher (created, creator, id, description, name) VALUES ('2023-06-05 19:40:49.000000', 10, 12, null, 'Диалектика-Вильямс');
INSERT INTO public.publisher (created, creator, id, description, name) VALUES ('2023-06-05 19:43:52.000000', 10, 13, 'Москва', 'ДМК Пресс');

INSERT INTO public.category (created, creator, id, parent_id, description, name) VALUES ('2023-06-18 18:12:21.000000', 10, 17, null, 'root category', 'root');
INSERT INTO public.category (created, creator, id, parent_id, description, name) VALUES ('2023-06-18 18:12:22.000000', 10, 18, 17, null, 'cat A');
INSERT INTO public.category (created, creator, id, parent_id, description, name) VALUES ('2023-06-18 18:12:23.000000', 10, 19, 17, null, 'cat B');
INSERT INTO public.category (created, creator, id, parent_id, description, name) VALUES ('2023-06-18 18:12:24.000000', 10, 20, 18, null, 'child of first child');
INSERT INTO public.category (created, creator, id, parent_id, description, name) VALUES ('2023-06-18 18:12:24.000000', 10, 21, 18, null, 'child of first child');
INSERT INTO public.category (created, creator, id, parent_id, description, name) VALUES ('2023-06-18 18:12:24.000000', 10, 22, 18, null, 'child of first child');

INSERT INTO public.book_definition (page_count, release_year, created, creator, id, cover_type, description, isbn, language, name, publisher_id,  common_rating, vote_count, category_id, instance_count) VALUES (1000, 2019, '2023-06-04 14:41:14.221314', 10, 1, 'paperback', 'BOOK_DESCRIPTION', '978-5-4461-0512-1', 'RUSSIAN', 'BOOK_C', 10, 3.0, 3, 18, 3);
INSERT INTO public.book_definition (page_count, release_year,  created, creator, id, cover_type, description, isbn, language, name, publisher_id, common_rating, vote_count, category_id, instance_count) VALUES (100, 2012,  '2023-06-04 14:41:15.221314', 10, 2, 'paperback', 'BOOK_DESCRIPTION 2', '978-5-4461-0512-2', 'RUSSIAN', 'BOOK_Z', 10, 3.5, 1, 18, 2);
INSERT INTO public.book_definition (page_count, release_year,  created, creator, id, cover_type, description, isbn, language, name, publisher_id, common_rating, vote_count, category_id, instance_count) VALUES (200, 2003, '2023-06-04 14:41:16.221314', 10, 3, 'paperback', 'BOOK_DESCRIPTION 3', '978-5-4461-0512-3', 'RUSSIAN', 'BOOK_A', 10, 5.0, 1, 18, 0);
INSERT INTO public.book_definition (page_count, release_year, created, creator, id, cover_type, description, isbn, language, name, common_rating, vote_count, category_id, instance_count) VALUES (50, 2005, '2023-06-04 14:41:17.221314', 10, 4, 'paperback', 'BOOK_DESCRIPTION 3 ', '978-5-4461-0512-4', 'RUSSIAN', 'BOOK_4', 3.0, 2, 19, 0);
INSERT INTO public.book_definition (page_count, release_year, created, creator, id, cover_type, description, isbn, language, name, common_rating, vote_count, category_id, instance_count) VALUES (2000, 2006, '2023-06-04 14:41:18.221314', 10, 5, 'paperback', 'BOOK_DESCRIPTION', '978-5-4461-0512-5', 'RUSSIAN', 'BOOK_5', 4.0, 5, 21, 0);

INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (6, 1);
INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (7, 1);
INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (8, 1);
INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (6, 2);
INSERT INTO public.book_to_author (authors_id, book_definitions_id) VALUES (6, 3);

INSERT INTO public.book_to_book (book_one, book_two) VALUES (1, 2);
INSERT INTO public.book_to_book (book_one, book_two) VALUES (1, 3);
INSERT INTO public.book_to_book (book_one, book_two) VALUES (3, 4);

INSERT INTO public.review (rating, book_definition_id, created, creator, id, text) VALUES (4, 1, '2023-06-18 14:46:49.000000', 10, 14, 'Some Cool Review');
INSERT INTO public.review (rating, book_definition_id, created, creator, id, text) VALUES (3, 1, '2023-06-18 14:46:48.000000', 10, 15, 'Some Cool Review 2');
INSERT INTO public.review (rating, book_definition_id, created, creator, id, text) VALUES (2, 1, '2023-06-18 14:46:47.000000', 10, 16, 'Some Cool Review 3');

INSERT INTO public.book_instance (is_company, book_definition_id, created, creator, id, owner) VALUES (false, 1, '2023-06-21 15:41:19.000000', 10, 23, 12);
INSERT INTO public.book_instance (is_company, book_definition_id, created, creator, id, owner) VALUES (false, 1, '2023-06-21 15:41:19.000000', 10, 24, 12);
INSERT INTO public.book_instance (is_company, book_definition_id, created, creator, id, owner) VALUES (false, 1, '2023-06-21 15:41:19.000000', 10, 25, 12);
INSERT INTO public.book_instance (is_company, book_definition_id, created, creator, id, owner) VALUES (false, 2, '2023-06-21 15:41:19.000000', 10, 26, 12);
INSERT INTO public.book_instance (is_company, book_definition_id, created, creator, id, owner) VALUES (false, 3, '2023-06-21 15:41:19.000000', 10, 27, 12);
