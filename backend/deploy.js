const HDWalletProvider = require("@truffle/hdwallet-provider");
const { Web3 } = require("web3");
//updated web3 and hdwallet-provider imports added for convenience

// deploy code will go here
const { interface, bytecode } = require("./solidity/compile");

const provider = new HDWalletProvider(
  "catch mix cute accident this nation myself liberty review embark dinner damp",
  "https://sepolia.infura.io/v3/3e885576a998490992a7cdaa69e2ed2f"
);

const web3 = new Web3(provider);

const deploy = async () => {
  const accounts = await web3.eth.getAccounts();
  console.log("Attempting to deploy from account", accounts[0]);

  const result = await new web3.eth.Contract(JSON.parse(interface))
    .deploy({ data: bytecode })
    .send({ gas: "1000000", from: accounts[0] });
  console.log(interface);
  console.log("Contract deployed to", result.options.address);
  provider.engine.stop(); // Stop the provider engine after deployment
  // This is important to avoid memory leaks and ensure proper cleanup
};

deploy();
