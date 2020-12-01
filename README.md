# CryptGuard-Android
### Funkcionalnosti
**_CryptGuard_** je password manager program za Android čije su osnovne funkcionalnosti sledeće:
* Enkripcija fajlova
* Dekripcija fajlova
* Uvoz baze podataka
* Izvoz baze podataka
* Kreiranje baze podataka na osnovu CSV fajla
* Kreiranje jakih korisničkih lozinki
* Čuvanje login podataka za korisnički definisane sajtove (username, password, email, ime
sajta, proizvoljni opis)

### Opis rada aplikacije
**_CryptGuard_** funkcioniše tako što se prvobitno korisnik registruje u aplikaciji. Pri registraciji
podaci koji se čuvaju o korisniku su ime, prezime, email, datum rođenja i lozinka. Nakon
registracije, na serveru se generiše server master key koji će biti neophodan korisniku u
kasnijim etapama.
Nakon registracije , korisnik se loguje i time dobija master ključ od servera i pri kreiranju nove baze definiše svoju master šifru pomoću fraze (_passphrase_). 
Pomoću serverskog ključa i fraze će se baza podataka otključavati. 
_Poželjno je da korisnik za svaku bazu koristi novu frazu._
U bazi podataka će korisnik moći da čuva sve neophodne podatke vezane za definisane sajtove kao što su korisničko ime, lozinka, email ili bilo koji opis određene dužine. Svaka baza podataka će moći da se izvede u format koji je prenosiv između telefona. Baze podataka je moguće kreirati pomoću CSV fajla prateći definisani način rasporeda podataka. 
Korisnik može da enkriptuje fajlove sa nekom frazom pomoću koje će se isti dekriptovati. 
Dostupna je i opcija generisanja jakih lozinki sa visokim nivoom entropije.
