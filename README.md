# 🛡️ Smart Insurance DApp – Tirocinio

## 📌 Obiettivo del Progetto

Sviluppare un'applicazione client che interagisca con smart contract su blockchain, integrando dati IoT *trusted*, per abilitare assicurazioni automatiche e trasparenti tramite tecnologia **smart insurance**.

---

## 🧠 Use Case Principale

Creazione di una piattaforma per **assicurazioni intelligenti**, dove gli **smart contract** agiscono come vere e proprie polizze assicurative. Il flusso tipico è il seguente:

- La compagnia assicurativa deposita un **capitale** nel contratto smart.
- Il contratto gestisce **diverse clausole** (es. danni ai cristalli dell’auto).
- Al verificarsi di un evento assicurabile, il contratto **rilascia automaticamente** i fondi all’assicurato, **senza bisogno di intermediari**.

---

## 🧱 Architettura Generale

### 🔨 Smart Contracts
- **Contract Factory**: consente di generare smart contract assicurativi con campi predefiniti (es. tipo di polizza, soglie di attivazione, massimali).
- **Assurance Contracts**: ogni contratto rappresenta una singola polizza personalizzata tra assicuratore e assicurato.

> Da definire lo **standard** per i contratti assicurativi smart (struttura, eventi, funzioni principali).

---

## 👥 Attori e Funzionalità

### 👔 Compagnia Assicurativa
- Visualizza tutti i contratti che ha generato.
- Monitora i fondi depositati nei contratti attivi.
- Crea nuovi contratti assicurativi tramite la **Factory**.
- Vede lo storico dei fondi erogati tramite i contratti.

### 🙋‍♂️ Assicurato
- Consulta i contratti assicurativi sottoscritti.
- Visualizza il riepilogo dei fondi ricevuti.
- Può **richiedere una verifica manuale** in caso di mancata erogazione automatica (es. per eventi non coperti da IoT o ambigui).

---

## 🛠️ Prossimi Step (entro venerdì 25)

1. **Schema generale dell'applicazione**
   - Componenti principali
   - Comunicazione tra frontend, smart contract e eventuali fonti dati IoT

2. **Progettazione degli Smart Contract**
   - Che tipo di contratti servono
   - Cosa fanno e come sono strutturati

3. **Design delle interfacce**
   - Schermate principali per:
     - Compagnia assicurativa
     - Assicurato
     - Dettaglio contratto
     - Stato dei fondi
     - Richiesta verifica manuale

---

## 📦 Tecnologie

> (Da confermare/espandere durante lo sviluppo)

- **Frontend**: Android
- **Smart Contract**: Solidity, Hardhat
- **Blockchain**: Ethereum / testnet (es. Sepolia)
- **Backend/Oracoli IoT**: boh
