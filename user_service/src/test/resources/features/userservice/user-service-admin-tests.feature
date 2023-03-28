Feature: Administratorske funkcije korisnickog servisa

  Scenario: Provera da li je korisnik administrator
    When imamo korisnika "zarko" koji je administrator
    Then metoda isAdmin treba da vrati da je "zarko" administrator
