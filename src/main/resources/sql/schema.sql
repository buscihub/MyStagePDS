CREATE TABLE ARTISTA (
    codiceFiscale CHAR(16) PRIMARY KEY NOT NULL,
    nome VARCHAR(255) NOT NULL,
    cognome VARCHAR(255) NOT NULL,
    dataDiNascita DATETIME NOT NULL,
    sesso VARCHAR(255) NOT NULL,
    nomeDarte VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    urlImmagineProfilo VARCHAR(2048) NOT NULL,
    codiceVerifica VARCHAR(10)
);

CREATE TABLE CARRIERA (
    idCarriera INT PRIMARY KEY AUTO_INCREMENT,
    codiceFiscaleArtist CHAR(16) NOT NULL,
    tipologia VARCHAR(255) NOT NULL,
    anni INT NOT NULL,
    FOREIGN KEY (codiceFiscaleArtist) REFERENCES ARTISTA(codiceFiscale) ON DELETE CASCADE
);

CREATE TABLE UTENTE (
    idUtente INT PRIMARY KEY AUTO_INCREMENT
);

CREATE TABLE STANZA (
    idStanza INT PRIMARY KEY AUTO_INCREMENT,
    codiceFiscaleArtist CHAR(16) NOT NULL,
    nomeStanza VARCHAR(255) NOT NULL,
    link VARCHAR(255) NOT NULL,
    FOREIGN KEY (codiceFiscaleArtist) REFERENCES ARTISTA(codiceFiscale) ON DELETE CASCADE
);

CREATE TABLE DOCUMENTO (
    idDocumento INT PRIMARY KEY AUTO_INCREMENT,
    codiceFiscaleArtist CHAR(16) NOT NULL,
    visibile BOOLEAN NOT NULL,
    percorso VARCHAR(2048) NOT NULL,
    FOREIGN KEY (codiceFiscaleArtist) REFERENCES ARTISTA(codiceFiscale) ON DELETE CASCADE
);

CREATE TABLE CONTIENE (
    idStanza INT NOT NULL,
    idDocumento INT NOT NULL,
    scaricabile BOOLEAN NOT NULL,
    PRIMARY KEY (idStanza, idDocumento),
    FOREIGN KEY (idStanza) REFERENCES STANZA(idStanza) ON DELETE CASCADE,
    FOREIGN KEY (idDocumento) REFERENCES DOCUMENTO(idDocumento) ON DELETE CASCADE
);

CREATE TABLE VISUALIZZATORE (
    idVisualizzatore INT PRIMARY KEY AUTO_INCREMENT,
    nomeVisualizzatore VARCHAR(255) NOT NULL,
    cognomeVisualizzatore VARCHAR(255) NOT NULL,
    emailVisualizzatore VARCHAR(255) NOT NULL
);

CREATE TABLE VISUALIZZAZIONE (
    idVisualizzazione INT PRIMARY KEY AUTO_INCREMENT,
    idVisualizzatore INT NOT NULL,
    idStanza INT NOT NULL,
    dataVisualizzazione DATETIME NOT NULL,
    FOREIGN KEY (idVisualizzatore) REFERENCES VISUALIZZATORE(idVisualizzatore) ON DELETE CASCADE,
    FOREIGN KEY (idStanza) REFERENCES STANZA(idStanza) ON DELETE CASCADE
);
