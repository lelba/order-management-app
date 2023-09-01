
INSERT INTO "T_USER" (id, dob, email, name, password, role, surname, username, address_id)
SELECT 1, null, 'lejla.barac@gmail.com', null, '123456', 'ADMIN', null, 'lela_', null
    WHERE NOT EXISTS (SELECT 1 FROM "T_USER" WHERE username = 'lela_');

