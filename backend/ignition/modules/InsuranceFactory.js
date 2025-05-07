const { buildModule } = require("@nomicfoundation/hardhat-ignition/modules");

module.exports = buildModule("InsuranceFactoryModule", (m) => {
  // This line will console log the ABI during deployment so it can be copied to the React app
  console.log(
    JSON.stringify(
      require("../../artifacts/contracts/InsuranceFactory.sol/InsuranceFactory.json")
        .abi
    )
  );

  // parametri per il costruttore del contratto
  const tokenAddress = "0xF9f3AE879C612D35a8D1CAa67e178f190a4a215f"; // indirizzo del token su sepolia

  const InsuranceFactory = m.contract("InsuranceFactory", [tokenAddress]);
  return { InsuranceFactory };
});
