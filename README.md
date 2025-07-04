# 🛡️ Smart Insurance DApp – Tirocinio

## 📌 Obiettivo del Progetto

Sviluppare un'applicazione client che interagisca con smart contract su blockchain, integrando dati IoT _trusted_, per abilitare assicurazioni automatiche e trasparenti tramite tecnologia **smart insurance**.

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

## 📝 Note dopo riunione del 28

- la creazione dei contratti partendo dal factory e carta bianca, o si parte
  da un contratto diverso per ogni voce , oppure un contratto con piu voci come
  quelli normali l unica cosa e che non deve essere una cosa hardcoded ma una cosa
  piu generale possibile.

- l assicurato mette una % di soldi per attivare il contratto, questo fara anche
  da firma e dopo il contratto sara attivo

- non vanno usati gli eth come moneta ma ne va creata una tramite un contratto
  erc-20 usando il wizard https://wizard.openzeppelin.com/#erc20

- per l erogazione dei soldi invece verra gestita piu o meno cosi :
  - quando premo verifica manuale su un contratto, il contratto chiamera una
    funzione di un contratto gia deployato che mi rispondera con un valore (callback?)
    se il valore e maggiore di una threshold allora il contratto eroghera i soldi
    (questo andra a simulare il lavoro di un oracolo che va a verificare i dati?)

## Workflow contratti sepolia

- IL TOKEN E IL FACTORY SONO GIA STATI DEPLOYATI SU SEPOLIA



## Aggiungere emit nelle funzioni che hanno bisogno di una transizione :
    - createInsurance
    - fundContract
    - liquidazione
    - activateContract
# quindi va ricompilato e ridepolyato il factory poi va su kotlin modificato,
# una volta che abbiamo la txhash :
    modifica in solidity -> emit InsuranceCreated(address(newContract), _assicurato); (esempio)
    modifica in kotlin ->

    val receipt = waitForReceipt(txHash)

    val event = Event(
        "InsuranceCreated",
        listOf(
            TypeReference.create(Address::class.java, true), // indexed contractAddress
            TypeReference.create(Address::class.java, true)  // indexed assicurato
        )
    )

    for (log in receipt.logs) {
        val topics = log.topics
        if (topics[0] == EventEncoder.encode(event)) {
            // Decode the address of the contract from the first indexed topic
            val contractAddress = "0x" + topics[1].substring(26)
            return contractAddress
        }
    }

    throw RuntimeException("InsuranceCreated event not found in transaction receipt.")
}
