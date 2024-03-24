 -- genre
-- insert into genre (genre_id, genre_name)
-- values (1, 'Комедия'),
--        (2, 'Драма'),
--        (3, 'Мультфильм'),
--        (4, 'Триллер'),
--        (5, 'Документальный'),
--        (6, 'Боевик');

insert into genre (genre_id, genre_name)
select 1, 'Комедия'
where not exists(select 1 from genre where genre_id = 1 limit 1);

insert into genre (genre_id, genre_name)
select 2, 'Драма'
where not exists(select 1 from genre where genre_id = 2 limit 1);

insert into genre (genre_id, genre_name)
select 3, 'Мультфильм'
where not exists(select 1 from genre where genre_id = 3 limit 1);

insert into genre (genre_id, genre_name)
select 4, 'Триллер'
where not exists(select 1 from genre where genre_id = 4 limit 1);

insert into genre (genre_id, genre_name)
select 5, 'Документальный'
where not exists(select 1 from genre where genre_id = 5 limit 1);

insert into genre (genre_id, genre_name)
select 6, 'Боевик'
where not exists(select 1 from genre where genre_id = 6 limit 1);
----

-- rating
-- insert into rating (rating_id, rating_name, rating_description)
-- values (1, 'G', 'Нет возрастных ограничений'),
--        (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
--        (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
--        (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
--        (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');
insert into rating (rating_id, rating_name, rating_description)
select 1, 'G', 'Нет возрастных ограничений'
where not exists(select 1 from rating where rating_id = 1);

insert into rating (rating_id, rating_name, rating_description)
select 2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'
where not exists(select 1 from rating where rating_id = 2);

insert into rating (rating_id, rating_name, rating_description)
select 3, 'PG-13', 'Детям до 13 лет просмотр не желателен'
where not exists(select 1 from rating where rating_id = 3);

insert into rating (rating_id, rating_name, rating_description)
select 4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'
where not exists(select 1 from rating where rating_id = 4);

insert into rating (rating_id, rating_name, rating_description)
select 5, 'NC-17', 'Лицам до 18 лет просмотр запрещён'
where not exists(select 1 from rating where rating_id = 5);




