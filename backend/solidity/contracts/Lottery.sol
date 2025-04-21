pragma solidity ^0.4.17; // specifica la versione di solidity che usiamo in questo file

contract Lottery {
    address public manager;
    address[] public players;

    constructor() public {
        manager = msg.sender; // chi ha invocato la funzione
    }

    function enter() public payable {
        // payable : riceve i dindini
        require(msg.value > .001 ether); // .001 ether viene convertito in wei
        players.push(msg.sender);
    }

    function random() private view returns (uint) {
        return uint(keccak256(block.difficulty, now, players)); // non molto randomica
    }

    function pickWinner() public restricted {
        uint index = random() % players.length;
        players[index].transfer(this.balance); // manda il cachet all indirizzo del vincitore this si riferisce al contratto
        //e balance ai soldi che cha il contratto
        players = new address[](0); // crea un nuovo array dinamico vuoto nelle parentesi tonde lo 0 sta a significare che
        //l array viene inizializzato con dentro 0 elementi
        // ovvero viene inizializzato vuoto
    }

    modifier restricted() {
        require(msg.sender == manager); // solo il manager puo invocare questa funzione
        _; // il codice della funzione viene eseguito
    }

    function getPlayers() public view returns (address[]) {
        return players; // restituisce l array dei giocatori
    }
}
