const hre = require("hardhat");

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

  /*console.log("Creo un nuovo contratto assicurativo...");
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
  }*/
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
