
INSERT INTO "T_USER" (id, dob, email, name, password, role, surname, username, address_id)
SELECT 1, null, 'lejla.barac@gmail.com', null, '$2a$10$HSNPTX/1vMXJ/rcYr3U.A.z5U5CEWg1cLBVkRH04N1ouPxL9Wn8RO', 'ADMIN', null, 'lela_', null
    WHERE NOT EXISTS (SELECT 1 FROM "T_USER" WHERE username = 'lela_');

