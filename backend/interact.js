/*

workflow remix :
1. deploy token con recipient = initalOwner = assicuratore
  - balanceOf assicuratore = 1000 token
2. deploy factory con tokenAddress
3. createInsurance con assicurato  = address assicurato , premio = premio
  - getAllinsuranceContracts mi da l indirizzo del contratto assicurativo creato
5. fund e liquidazione e activate hanno bisogno che ci siano gli approve
  - per la fund vengono trasferiti da ASSICURATORE(owner del token) -> CONTRATTO 
    (lo fa il contratto quindi assicuratore deve approvare) QUANTI SOLDI? IL PREMIO
  - per l activate vengono trasferiti da ASSICURATO -> CONTRATTO (come do i soldi all assicurato? mint?) 
    (lo fa il contratto quindi...) QUANTI SOLDI? UNA PERCENTUALE DEL PREMIO PER ORA l approve lo da l assicurato qua
  - per la liquidazione vengono trasferiti da CONTRATTO - > ASSICURATO (lo fa il contratto per il contratto , non serve approve)
  2 APPROVE IN TUTTO
*/

const tokenAddress = "0xF9f3AE879C612D35a8D1CAa67e178f190a4a215f";
const factoryAddress = "0xa133327Baa93455433bC8Ea547B77704f84042a7";
const path = require("path");
const Web3 = require("web3").Web3;
const web3 = new Web3(
  "https://sepolia.infura.io/v3/3e885576a998490992a7cdaa69e2ed2f"
);

const artifacttokenPath = path.join(
  __dirname,
  "ignition/deployments/chain-11155111/artifacts/MyTokenModule#MyToken.json"
);
const artifacttoken = require(artifacttokenPath);
const tokenabi = artifacttoken.abi;
const tokenContract = new web3.eth.Contract(tokenabi, tokenAddress);

const artifactfactoryPath = path.join(
  __dirname,
  "ignition/deployments/chain-11155111/artifacts/InsuranceFactoryModule#InsuranceFactory.json"
);
const artifactfactory = require(artifactfactoryPath);
const factoryabi = artifactfactory.abi;
const factoryContract = new web3.eth.Contract(factoryabi, factoryAddress);

async function getBalance(address) {
  const balance = await tokenContract.methods.balanceOf(address).call();
  console.log(`Balance of ${address}:`, balance);
  console.log();
}

async function getassicuratore() {
  const assicuratore = await factoryContract.methods.assicuratore().call();
  const tokenAddress = await factoryContract.methods.tokenAddress().call();
  console.log("Assicuratore:", assicuratore);
  console.log("Token Address:", tokenAddress);
  console.log();
}

getBalance("0x8C6b618aC0b1E69FA7FF02Ec2a8EB6caDC29bc86");
getassicuratore();

/*const hre = require("hardhat");

async function main() {
  const [assicuratore] = await hre.ethers.getSigners();

  const tokenAddress = "INSERISCI_INDIRIZZO_TOKEN";
  const factoryAddress = "INSERISCI_INDIRIZZO_FACTORY";

  const token = await hre.ethers.getContractAt("MyToken", tokenAddress);
  const factory = await hre.ethers.getContractAt(
    "InsuranceFactory",
    factoryAddress
  );

  const premio = hre.ethers.utils.parseUnits("200", 18); // 200 token

  console.log("Approvo il factory per spendere i token...");
  const approveTx = await token
    .connect(assicuratore)
    .approve(factoryAddress, premio);
  await approveTx.wait();

  console.log("Creo un nuovo contratto assicurativo...");
  const createTx = await factory
    .connect(assicuratore)
    .createInsurance(assicuratore.address, premio);
  const receipt = await createTx.wait();

  const logs = receipt.logs.filter((log) => log.address !== tokenAddress);
  console.log("Contratto assicurativo creato! TX hash:", createTx.hash);

  if (logs.length > 0) {
    console.log("Possibile nuovo contratto:", logs[0].address);
  } else {
    console.log(
      "Controlla gli eventi del contratto per ottenere l'indirizzo preciso."
    );
  }
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});*/
