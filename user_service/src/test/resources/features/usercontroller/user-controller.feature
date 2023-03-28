Feature: Kontroler za korisnicki servis

  Scenario: Pravimo novog korisnika preko API-a
    Given logovali smo se na aplikaciju kao administrator
    When pravimo novog korisnika koji se zove "stanko"
    Then proveravmo da li je "stanko" upisan u bazu podataka

  Scenario: testiramo nesto drugo
    Then proveram da li je JWT token setovan
