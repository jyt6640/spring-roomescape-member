INSERT INTO reservation_time (id, start_at) VALUES (1, '10:00');
INSERT INTO reservation_time (id, start_at) VALUES (2, '11:00');
INSERT INTO reservation_time (id, start_at) VALUES (3, '12:00');

INSERT INTO theme (id, name, description, thumbnail_url)
VALUES (1, '공포의 방', '어두운 저택에서 단서를 찾는 테마', 'https://example.com/horror.png');
INSERT INTO theme (id, name, description, thumbnail_url)
VALUES (2, '우주 탈출', '고장 난 우주선에서 탈출하는 테마', 'https://example.com/space.png');
INSERT INTO theme (id, name, description, thumbnail_url)
VALUES (3, '고대 유적', '잃어버린 유적의 문을 여는 테마', 'https://example.com/ruins.png');

INSERT INTO reservation (name, date, time_id, theme_id) VALUES ('브라운', '2026-05-14', 1, 1);
INSERT INTO reservation (name, date, time_id, theme_id) VALUES ('코니', '2026-05-15', 2, 1);
INSERT INTO reservation (name, date, time_id, theme_id) VALUES ('샐리', '2026-05-16', 3, 1);
INSERT INTO reservation (name, date, time_id, theme_id) VALUES ('문', '2026-05-17', 1, 2);
INSERT INTO reservation (name, date, time_id, theme_id) VALUES ('제임스', '2026-05-18', 2, 2);
INSERT INTO reservation (name, date, time_id, theme_id) VALUES ('레너드', '2026-05-19', 1, 3);
