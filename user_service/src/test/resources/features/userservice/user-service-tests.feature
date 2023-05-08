Feature: Servis za upravljanje sa korisnicima

  Ovaj servis sluzi da bi se korisnici dodali u sistem, da upravljamo sa korisnicima
  i da proverimo da li korisnik ima privilegije.

  Scenario: Dodavanje novog korisnika
    When kada se napravi novi korisnik
    Then taj korisnik je sacuvan u bazi podataka

  Scenario: Dodavanje novog korisnika sa parametrima
    When napravimo novog korisnika koji se zove "Janko Jankovic", ima username "janko" i password "test"
    Then "janko" je sacuvan u bazi podataka
