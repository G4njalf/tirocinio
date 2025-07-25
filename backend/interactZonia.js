const Web3 = require("web3").Web3;
const web3 = new Web3(
  "https://sepolia.infura.io/v3/3e885576a998490992a7cdaa69e2ed2f"
);

const PRIVATE_KEY =
  "0xREMOVED";

const account = web3.eth.accounts.privateKeyToAccount(PRIVATE_KEY);
web3.eth.accounts.wallet.add(account);
web3.eth.defaultAccount = account.address;

const contractAddress = "0xbb6849DC5D97Bd55DE9A23B58CD5bBF3Bfdda0FA";

const abi = [
  {
    inputs: [
      {
        components: [
          {
            internalType: "string",
            name: "query",
            type: "string",
          },
          {
            components: [
              {
                internalType: "uint256",
                name: "w1",
                type: "uint256",
              },
              {
                internalType: "uint256",
                name: "w2",
                type: "uint256",
              },
              {
                internalType: "uint256",
                name: "w3",
                type: "uint256",
              },
              {
                internalType: "uint256",
                name: "w4",
                type: "uint256",
              },
            ],
            internalType: "struct DataTypes.ChainParams",
            name: "chainParams",
            type: "tuple",
          },
          {
            internalType: "uint256",
            name: "ko",
            type: "uint256",
          },
          {
            internalType: "uint256",
            name: "ki",
            type: "uint256",
          },
          {
            internalType: "uint256",
            name: "fee",
            type: "uint256",
          },
        ],
        internalType: "struct DataTypes.InputRequest",
        name: "inputRequest",
        type: "tuple",
      },
    ],
    name: "submitRequest",
    outputs: [
      {
        internalType: "bytes32",
        name: "",
        type: "bytes32",
      },
    ],
    stateMutability: "nonpayable",
    type: "function",
  },
];

const contract = new web3.eth.Contract(abi, contractAddress);

const stringinput =
  '{"topic":"saref:Temperature","geo":{"type":"Feature","geometry":{"type":"Point","coordinates":[11.582,48.1351]},"properties":{"radius":1000}}}';
const stringinput2 = "test";

const inputRequest = {
  query: stringinput,
  chainParams: {
    w1: 25,
    w2: 25,
    w3: 25,
    w4: 25,
  },
  ko: 0,
  ki: 0,
  fee: 1,
};

function showTopics(receipt) {
  if (receipt.logs && receipt.logs[0] && receipt.logs[0].topics) {
    console.log("\n🆔 Request ID candidates:");
    receipt.logs[0].topics.forEach((topic, index) => {
      console.log(`Topic ${index}:`, topic);
    });
  }
}

async function callSubmitRequest() {
  try {
    console.log("Account address:", account.address);

    const tx = contract.methods.submitRequest(inputRequest);
    const gas = 500000;
    const gasPrice = await web3.eth.getGasPrice();

    const receipt = await tx.send({
      from: account.address,
      gas: Math.floor(gas * 1.2),
      gasPrice,
    });

    console.log("Transaction submitted!");
    console.log("Transaction hash:", receipt.transactionHash);
    console.log("recipt:", receipt);
    showTopics(receipt);
  } catch (err) {
    console.error("Transaction failed:");
    console.error("Error message:", err);

    if (err.receipt) {
      console.log("Transaction hash (failed):", err.receipt.transactionHash);
      console.log("Status:", err.receipt.status);
    }
    if (err.transactionHash) {
      console.log("Transaction hash from error:", err.transactionHash);
    }
  }
}

callSubmitRequest();
