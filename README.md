# TMS - Triage Management System

**TMS** è un sistema software gestionale progettato per ottimizzare il flusso di lavoro all'interno di un reparto di pronto soccorso. Il sistema gestisce l'intero ciclo di vita del paziente, dall'accettazione (Triage) alla visita medica, fino alla refertazione, utilizzando un'architettura **MVC (Model-View-Controller)**.

Progetto universitario realizzato per il corso di Ingegneria del Software da
* **Riccardo Accatino 20054233**
* **Francesco Dappiano 20055270** 
* **Angie Albitres 20054397** 

## 🚀 Caratteristiche Principali

* **Gestione Utenti:** Ruoli distinti per Segretari, Dottori e Pazienti.
* **Triage Digitale:** Assegnazione di codici colore e priorità ai ticket.
* **Gestione Code e Reparti:** Smistamento automatico verso i reparti competenti (Cardiologia, Ortopedia, ecc.).
* **Calendario Visite:** Pianificazione delle visite mediche.
* **Refertazione:** Creazione e archiviazione dei referti post-visita.
* **Persistenza Dati:** Database SQLite integrato e portabile.

## 🛠 Tecnologia e Requisiti

Il progetto è basato su **Java 21** e utilizza **Maven** per la gestione delle dipendenze.

* **Linguaggio:** Java JDK 21
* **Interfaccia Grafica (GUI):** JavaFX 21
* **Database:** SQLite (con driver JDBC)
* **Build Tool:** Maven
* **Librerie di Test:** JUnit 5

## ⚙️ Installazione e Configurazione

1.  **Clonare il repository** o estrarre l'archivio del progetto.
2.  Aprire il progetto con un IDE (IntelliJ IDEA raccomandato o Eclipse).
3.  Assicurarsi che l'SDK del progetto sia impostato su **Java 21**.
4.  Ricaricare le dipendenze Maven (Reload Project) per scaricare le librerie necessarie (JavaFX, SQLite, JUnit).

## ▶️ Come Avviare l'Applicazione

L'applicazione dispone di un punto di ingresso principale creato appositamente per evitare problemi di caricamento dei moduli JavaFX. Per avviarla correttamente:

1.  Navigare nel package `src/main`.
2.  Eseguire la classe **`Main.java`** (non `GuiMain`).

```java
// Percorso: TMS/src/main/Main.java
public class Main {
    public static void main(String[] args) {
        GuiMain.main(args);
    }
}
```

> **Nota:** Al primo avvio, l'applicazione inizializzerà automaticamente il database locale `tms.db` creando tutte le tabelle necessarie.

## 📦 Build ed Esportazione (.jar)

Poiché il progetto utilizza Maven, è possibile generare il file eseguibile `.jar`.

### Metodo 1: Tramite IDE (IntelliJ IDEA)
1.  Aprire il pannello laterale "Maven".
2.  Espandere il progetto > `Lifecycle`.
3.  Fare doppio click su `clean` e successivamente su `package`.
4.  Il file `.jar` verrà generato nella cartella `target/` all'interno del progetto.

### Metodo 2: Tramite Terminale
Aprire il terminale nella cartella radice del progetto (dove si trova il file `pom.xml`) ed eseguire:

```bash
mvn clean package
```

*Nota per l'esecuzione del JAR:* Se si esegue il jar fuori dall'IDE, assicurarsi di avere le librerie JavaFX nel path o di configurare il plugin Maven Shade/Assembly per creare un "Fat Jar" che includa tutte le dipendenze.

## 🔐 Credenziali di Accesso (Dati di Test)

Il database viene popolato automaticamente con i seguenti dati di test.

### 1. Personale di Segreteria (Accettazione)
Utilizzare questo account per gestire i ticket, l'accettazione e lo smistamento iniziale.

| Ruolo | ID Utente (Login) | Nome |
| :--- | :--- | :--- |
| **Segretario** | `100` | segretario |

### 2. Personale Medico (Dottori)
Ogni dottore accede con il proprio ID ed è vincolato al proprio reparto e turno.

| Reparto | ID Utente (Login) | Nome Dottore | Matricola | Turno |
| :--- | :--- | :--- | :--- | :--- |
| **Cardiologia** | `1` | Dr. Mario Rossi | DOC001 | Mattina |
| **Ortopedia** | `2` | Dr. Luca Bianchi | DOC002 | Pomeriggio |
| **Chirurgia Gen.**| `3` | Dr. Elena Verdi | DOC003 | Notte |
| **Medicina Int.** | `4` | Dr. Gregory House | DOC004 | H24 |
| **Neurologia** | `5` | Dr. Stephen Strange| DOC005 | Reperibile |

### 3. Pazienti
Utenti registrati per testare le funzionalità lato paziente (es. storico visite).

| ID Utente | Nome | Codice Fiscale |
| :--- | :--- | :--- |
| `10` | Luigi Paziente | LGUPAZ95B15F205X |
| `11` | Anna Neri | NNANRI50M70L219K |

## 📂 Struttura del Progetto

Il codice sorgente segue rigorosamente il pattern MVC:

* `src/main`: Entry point (Main wrapper).
* `src/gui.vista`: File FXML e Controller grafici.
* `src/controller`: Logica di coordinamento tra View e Model.
* `src/model`: Logica di business e classi POJO.
* `src/model/dao`: Accesso ai dati (Data Access Objects).
* `src/dbManager`: Configurazione SQLite e script di popolamento (`dbPopolate.java`).

## 👥 Autori

* **Riccardo Accatino**
* **Francesco Dappiano**
* **Angie Albitres**

----
© 2026 TMS Project. All rights reserved.

