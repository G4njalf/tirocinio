const { buildModule } = require("@nomicfoundation/hardhat-ignition/modules");

module.exports = buildModule("InsuranceContractModule", (m) => {
  // This line will console log the ABI during deployment so it can be copied to the React app
  console.log(
    JSON.stringify(
      require("../../artifacts/contracts/InsuranceContract.sol/InsuranceContract.json")
        .abi
    )
  );

  const InsuranceContract = m.contract("InsuranceContract", []);
  return { InsuranceContract };
});
