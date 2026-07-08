INSERT INTO ARTISTA (codiceFiscale, nome, cognome, dataDiNascita, sesso, nomeDarte, email, password, urlImmagineProfilo, codiceVerifica) VALUES
('RSSMRA80A01H501U', 'Mario', 'Rossi', '1980-01-01 00:00:00', 'M', 'SuperMario', 'mario.rossi@example.com', 'password123', 'default.png', NULL),
('BNCLCU90B02H501V', 'Lucia', 'Bianchi', '1990-02-02 00:00:00', 'F', 'LuceArt', 'lucia.bianchi@example.com', 'password123', 'default.png', NULL),
('VRDLGI85C03H501W', 'Luigi', 'Verdi', '1985-03-03 00:00:00', 'M', 'GreenGigi', 'luigi.verdi@example.com', 'password123', 'default.png', NULL),
('TESTCF1234567890', 'Utente', 'Test', '2000-01-01 00:00:00', 'ND', 'TestArtist', 'test@test.com', 'test', 'default.png', NULL);

INSERT INTO DOCUMENTO (codiceFiscaleArtist, visibile, percorso) VALUES
('RSSMRA80A01H501U', 1, 'src/main/resources/images/paesaggio1.png'),
('RSSMRA80A01H501U', 1, 'src/main/resources/images/paesaggio2.png'),
('BNCLCU90B02H501V', 1, 'src/main/resources/images/paesaggio3.png');

INSERT INTO STANZA (codiceFiscaleArtist, nomeStanza, link) VALUES
('RSSMRA80A01H501U', 'Galleria Mario', 'http://localhost:8080/stanze/1'),
('BNCLCU90B02H501V', 'LuceArt Esposizione', 'http://localhost:8080/stanze/2');

INSERT INTO CONTIENE (idStanza, idDocumento, scaricabile) VALUES
(1, 1, 1),
(1, 2, 0),
(2, 3, 1);
