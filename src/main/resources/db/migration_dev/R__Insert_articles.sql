DELETE FROM articles;

ALTER TABLE articles AUTO_INCREMENT = 1;

INSERT INTO articles (title, body)
VALUES ('タイトルです1', '1本文です。')
     , ('タイトルです2', '2本文です。')
     , ('タイトルです3', '3本文です。')
;
DELETE FROM users;
-- password => "password" for all users
INSERT INTO users (username, password, enabled)
VALUES ('user1', '$2a$10$MFrEraT1YbkAl0R5yJMD3eyCUgmabebBrv9WvFclFxZCaQvFqyZma', true)
     , ('user2', '$2a$10$p6pSfRrjvLVUDrkRLMn8hOMQtypsU0lDWFilyNgH1CraOFXS6QCF6', true)
     , ('user3', '$2a$10$zv.tFNKWA.4NKZQ02uJfqupJzqX5x.KH0oMSCtp58Xi/qwNRCMOH.', true)
;