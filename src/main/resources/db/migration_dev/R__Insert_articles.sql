DELETE FROM articles;

ALTER TABLE articles AUTO_INCREMENT = 1;

INSERT INTO articles (title, body)
VALUES ('タイトルです1', '1本文です。')
     , ('タイトルです2', '2本文です。')
     , ('タイトルです3', '3本文です。')
;

DELETE FROM users;

ALTER TABLE articles AUTO_INCREMENT = 1;

-- password => "password00" for all users
INSERT INTO users (id, username, password, enabled)
VALUES (1, 'user1', '$2a$10$nyJuFN8ukeszAPUBELCxauSxU2fHygybh3.keJnq3mwEgai3AulbK', true)
     , (2, 'user2', '$2a$10$8yF984DRLex7UDeCOG230ehnSVPm4OShzVVOYujPYcJsXPcy39czy', true)
     , (3, 'user3', '$2a$10$l8gXDAKbsGtUQhdrwE06N.ip6iNW4tauQW40Q6nhU8DMYF1sLmFvy', true)
;