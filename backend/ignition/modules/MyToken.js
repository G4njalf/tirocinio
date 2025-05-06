const { buildModule } = require("@nomicfoundation/hardhat-ignition/modules");

module.exports = buildModule("MyTokenModule", (m) => {
  // This line will console log the ABI during deployment so it can be copied to the React app
  console.log(
    JSON.stringify(
      require("../../artifacts/contracts/MyToken.sol/MyToken.json").abi
    )
  );

  // parametri per il costruttore del contratto
  const recipient = "0x8C6b618aC0b1E69FA7FF02Ec2a8EB6caDC29bc86";
  const initialOwner = "0x8C6b618aC0b1E69FA7FF02Ec2a8EB6caDC29bc86";

  const MyToken = m.contract("MyToken", [recipient, initialOwner]);
  return { MyToken };
});
