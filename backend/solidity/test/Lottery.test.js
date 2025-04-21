const assert = require("assert");
const ganache = require("ganache"); // testnet
const { Web3 } = require("web3");
const web3 = new Web3(ganache.provider({ logging: { quiet: true } })); // testnet provider
const { interface, bytecode } = require("../compile"); // ABI and bytecode from compile.js

let lottery;
let accounts;

beforeEach(async () => {
  accounts = await web3.eth.getAccounts(); // get all accounts from ganache
  lottery = await new web3.eth.Contract(JSON.parse(interface))
    .deploy({ data: bytecode })
    .send({ from: accounts[0], gas: "1000000" }); // deploy contract
});

describe("Lottery contract", () => {
  it("deploys a contract", () => {
    assert.ok(lottery.options.address);
  });
  it("allows one account to enter", async () => {
    await lottery.methods.enter().send({
      from: accounts[0],
      value: web3.utils.toWei("0.02", "ether"),
    });

    const players = await lottery.methods.getPlayers().call({
      from: accounts[0],
    });
    assert.equal(accounts[0], players[0]);
    assert.equal(1, players.length);
  });

  it("allows multiple accounts to enter", async () => {
    for (let i = 0; i < 3; i++) {
      await lottery.methods.enter().send({
        from: accounts[i],
        value: web3.utils.toWei("0.02", "ether"),
      });
    }
    const players = await lottery.methods.getPlayers().call({
      from: accounts[0],
    });
    for (let i = 0; i < 3; i++) {
      assert.equal(accounts[i], players[i]);
    }
    assert.equal(3, players.length);
  });

  it("requires a minimum amount of ether to enter", async () => {
    // pattern comune per checcare se qualcosa deve andare male
    try {
      await lottery.methods.enter().send({
        from: accounts[0],
        value: "10",
      });
      assert(false); // fallisce sempre il test
    } catch (error) {
      assert(error); // assert e basta guarda se quello che gli viene passato e vero,
      // assert.ok guarda se gli viene passato qualcosa non il suo valore di verita
    }
  });

  it("only manager can call pickWinner", async () => {
    try {
      await lottery.methods.pickWinner().send({
        from: accounts[1], // not the manager
      });
      assert(false);
    } catch (error) {
      assert(error);
    }
  });

  it("sends money to winner and resets players array", async () => {
    await lottery.methods.enter().send({
      from: accounts[0],
      value: web3.utils.toWei("2", "ether"), // si vede meglio il cambio di soldi
    });

    const initialBalance = await web3.eth.getBalance(accounts[0]); // ritorna i soldi in wei di un account
    await lottery.methods.pickWinner().send({
      from: accounts[0],
    });
    const finalBalance = await web3.eth.getBalance(accounts[0]); // non sara initial + 2 perche abbiamo speso per il gas

    const difference = finalBalance - initialBalance;

    assert(difference > web3.utils.toWei("1.8", "ether"));

    const players = await lottery.methods.getPlayers().send({
      from: accounts[0],
    });
    assert.equal(undefined, players.length); // boh secondo me deve essere 0 non undefined
  });
});

//Se utilizzi Mocha per i test e vuoi mantenere ESM, assicurati di eseguire i test con il flag --loader: mocha --loader esm
