const Web3 = require("web3").Web3;
const web3 = new Web3(
  "https://sepolia.infura.io/v3/3e885576a998490992a7cdaa69e2ed2f"
);

// 🟡 Replace con la tua chiave privata (ATTENZIONE: non condividerla mai!)
const PRIVATE_KEY =
  "0xREMOVED";

// 👤 Ricava l'account dal private key
const account = web3.eth.accounts.privateKeyToAccount(PRIVATE_KEY);
web3.eth.accounts.wallet.add(account);
web3.eth.defaultAccount = account.address;

// 📍 Indirizzo del contratto Gate su Sepolia
const contractAddress = "0xbb6849DC5D97Bd55DE9A23B58CD5bBF3Bfdda0FA";

// 🧠 ABI minimale per `submitRequest(InputRequest)`
const abi = [
  {
    inputs: [
      {
        components: [
          { internalType: "string", name: "query", type: "string" },
          {
            components: [
              { internalType: "uint256", name: "w1", type: "uint256" },
              { internalType: "uint256", name: "w2", type: "uint256" },
              { internalType: "uint256", name: "w3", type: "uint256" },
              { internalType: "uint256", name: "w4", type: "uint256" },
            ],
            internalType: "struct ChainParams",
            name: "chainParams",
            type: "tuple",
          },
          { internalType: "uint256", name: "ko", type: "uint256" },
          { internalType: "uint256", name: "ki", type: "uint256" },
          { internalType: "uint256", name: "fee", type: "uint256" },
        ],
        internalType: "struct InputRequest",
        name: "inputRequest",
        type: "tuple",
      },
    ],
    name: "submitRequest",
    outputs: [{ internalType: "bytes32", name: "", type: "bytes32" }],
    stateMutability: "nonpayable",
    type: "function",
  },
];

// 🔧 Istanzia il contratto
const contract = new web3.eth.Contract(abi, contractAddress);

const stringinput =
  '{"topic":"saref:Temperature","geo":{"type":"Feature","geometry":{"type":"Point","coordinates":[11.582,48.1351]},"properties":{"radius":1000}}}';
const stringinput2 = "test";
// 📨 Prepara il payload
const inputRequest = {
  query: stringinput,
  chainParams: {
    w1: 1,
    w2: 1,
    w3: 1,
    w4: 1,
  },
  ko: 0,
  ki: 0,
  fee: 1,
};

// 🚀 Chiama la funzione
async function callSubmitRequest() {
  try {
    // Verifica il bilancio
    const balance = await web3.eth.getBalance(account.address);
    console.log(
      "💰 Account balance:",
      web3.utils.fromWei(balance, "ether"),
      "ETH"
    );
    console.log("🏠 Account address:", account.address);

    // Verifica che il contratto esista
    const contractCode = await web3.eth.getCode(contractAddress);
    console.log("📄 Contract exists:", contractCode !== "0x");

    // ❌ RIMUOVI LA SIMULAZIONE - invia direttamente
    console.log("🚀 Sending transaction directly...");

    const tx = contract.methods.submitRequest(inputRequest);
    const gas = 500000; // Stima del gas (puoi usare tx.estimateGas() per una stima più precisa)
    const gasPrice = await web3.eth.getGasPrice();

    console.log("⛽ Estimated gas:", gas);
    console.log("💸 Gas price:", gasPrice);

    const receipt = await tx.send({
      from: account.address,
      gas: Math.floor(gas * 1.2),
      gasPrice,
    });

    console.log("✅ Transaction submitted!");
    console.log("📦 Transaction hash:", receipt.transactionHash);
    console.log(
      "🔗 Etherscan:",
      `https://sepolia.etherscan.io/tx/${receipt.transactionHash}`
    );
    console.log("📊 Status:", receipt.status); // 1 = success, 0 = failed
  } catch (err) {
    console.error("❌ Transaction failed:");
    console.error("Error message:", err);

    // 🎯 ANCHE SE FALLISCE, POTRESTI AVERE L'HASH
    if (err.receipt) {
      console.log("📦 Transaction hash (failed):", err.receipt.transactionHash);
      console.log(
        "🔗 Etherscan:",
        `https://sepolia.etherscan.io/tx/${err.receipt.transactionHash}`
      );
      console.log("📊 Status:", err.receipt.status); // Sarà 0 per failed
    }

    // Oppure cerca nell'errore stesso
    if (err.transactionHash) {
      console.log("📦 Transaction hash from error:", err.transactionHash);
    }
  }
}

callSubmitRequest();
