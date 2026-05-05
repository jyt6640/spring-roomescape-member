INSERT INTO theme (id, name, description, thumbnail) VALUES
    (1, '추리', '단서를 모아 사건을 해결하는 테마', '/images/theme/mystery.jpg'),
    (2, '공포', '어둠 속에서 탈출하는 테마', '/images/theme/horror.jpg'),
    (3, '모험', '숨겨진 유적을 탐험하는 테마', '/images/theme/adventure.jpg'),
    (4, '감성', '잔잔한 이야기를 따라가는 테마', '/images/theme/story.jpg');

INSERT INTO reservation_time (id, start_at) VALUES
    (1, '10:00'),
    (2, '11:00'),
    (3, '12:00'),
    (4, '13:00'),
    (5, '14:00'),
    (6, '15:00'),
    (7, '16:00'),
    (8, '17:00');

INSERT INTO reservation (name, date, time_id, theme_id) VALUES
    ('흑곰', '2026-05-02', 3, 1),
    ('재키', '2026-05-03', 4, 1),
    ('로치', '2026-05-04', 5, 1),
    ('라티', '2026-05-02', 6, 2),
    ('피온', '2026-05-03', 7, 2),
    ('워넬', '2026-05-04', 8, 3),
    ('카키', '2026-04-30', 1, 4),
    ('포비', '2026-05-08', 2, 4);
