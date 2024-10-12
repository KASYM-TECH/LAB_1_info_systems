CREATE OR REPLACE FUNCTION zero_oscar_count_by_genre(genre_input VARCHAR) RETURNS VOID AS $$
BEGIN
UPDATE movie
SET oscarscount = 0
WHERE director_id IN (
    SELECT m2.director_id
    FROM movie m2
    WHERE m2.genre = genre_input
);
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_all_movies_with_no_oscars()
RETURNS TABLE(id BIGINT, name VARCHAR, oscars_count INT) AS $$
BEGIN
RETURN QUERY
SELECT id, name, oscarscount
FROM movie
WHERE oscarscount = 0;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION group_by_total_box_office()
RETURNS TABLE(total_box_office real, count BIGINT) AS $$
BEGIN
RETURN QUERY
SELECT m.totalboxoffice, COUNT(*)
FROM movie m
GROUP BY m.totalboxoffice;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_min_total_box_office_movie()
RETURNS BIGINT AS $$
DECLARE
movie_id BIGINT;
BEGIN
SELECT id INTO movie_id
FROM movie
WHERE totalboxoffice = (SELECT MIN(totalboxoffice) FROM movie)
    LIMIT 1;

RETURN movie_id;
END;
$$ LANGUAGE plpgsql;

