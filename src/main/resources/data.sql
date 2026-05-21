INSERT INTO reservation_time(id, start_at) VALUES (1, '10:00');
INSERT INTO reservation_time(id, start_at) VALUES (2, '11:00');
INSERT INTO reservation_time(id, start_at) VALUES (3, '12:00');

INSERT INTO theme(id, name, description, thumbnail)
VALUES (1, '달빛 저택', '사라진 편지를 찾는 추리 테마', 'https://example.com/moonlight.png');
INSERT INTO theme(id, name, description, thumbnail)
VALUES (2, '우주 탈출', '고장난 우주선에서 탈출하는 SF 테마', 'https://example.com/space.png');
INSERT INTO theme(id, name, description, thumbnail)
VALUES (3, '해저 미션', '침몰한 연구소의 비밀을 푸는 어드벤처 테마', 'https://example.com/ocean.png');

INSERT INTO reservation(name, date, time, time_id, theme_id)
VALUES ('브라운', CAST(DATEADD('DAY', -1, CURRENT_DATE) AS VARCHAR), '10:00', 1, 1);
INSERT INTO reservation(name, date, time, time_id, theme_id)
VALUES ('포비', CAST(DATEADD('DAY', -2, CURRENT_DATE) AS VARCHAR), '11:00', 2, 1);
INSERT INTO reservation(name, date, time, time_id, theme_id)
VALUES ('춘식', CAST(DATEADD('DAY', -3, CURRENT_DATE) AS VARCHAR), '12:00', 3, 2);
