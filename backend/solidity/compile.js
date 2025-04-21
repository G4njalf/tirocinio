// compile code will go here
// serve perche prima di testare e deployare il codice, bisogna compilarlo
// dobbiamo usare il compilatore di solidity , gli passiamo il codice sorgente del contratto
// e ci restituisce ABI (layer di interpretazione di cos e il contratto) e bytecode del contratto (che sara deployato
// sulla blockchain)

const path = require("path");
const fs = require("fs");
const solc = require("solc");

const LotteryPath = path.resolve(__dirname, "contracts", "Lottery.sol");
//dirname costante di node che ci dice la current working directory
// gli altri 2 argomenti puntano alla cartella contracts e al file Lottery.sol

const source = fs.readFileSync(LotteryPath, "utf8");

module.exports = solc.compile(source, 1).contracts[':Lottery']; // compila il source code del contratto 1 perche ce solo un contratto
// bytecode e ABI(interface nell output) del contratto
// .contracts[':Lottery'] perche invece di ritornare tutto l output torno solo la parte che mi interessa
