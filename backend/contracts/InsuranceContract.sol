// SPDX-License-Identifier: MIT
pragma solidity ^0.8.9;
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

contract InsuranceContract {
    address public assicuratore;
    address public assicurato;
    uint public premio;
    bool public liquidato;
    bool public attivato;
    bool public funded;
    IERC20 public token; // ERC20 token

    constructor(
        address _assicuratore,
        address _assicurato,
        address _token,
        uint _premio
    ) {
        assicuratore = _assicuratore;
        assicurato = _assicurato;
        liquidato = false;
        premio = _premio;
        token = IERC20(_token);
        attivato = false;
        funded = false;
    }

    function fundContract() public onlyInsurer {
        require(!funded, "Already funded");
        token.transferFrom(
            assicuratore,
            address(this),
            premio // l assicuratore manda il premio all assicurato
        );
        funded = true;
    }

    function liquidazione() external onlyInsurer {
        /* premo verifica manuale dall app -> parte sta funzione -> interroga un contratto che mi devono dare l abi
            l abi mi da un threshold -> controllo, se ok liquido
            quando cho l abi poi vedo come farlo 
        */
        require(!liquidato, "Already liquidated");
        require(attivato, "Contract not activated yet");
        require(funded, "Contract has to be funded");
        token.transfer(assicurato, premio); // trasferisce il premio all assicurato
        liquidato = true;
    }

    function activateContract() external onlyInsured {
        // l assicurato per attivare il contratto manda una piccola percentuale (si puo parametrizzare dalla factory)
        // del premio assicurativo, che viene poi restituita all assicurato in caso di liquidazione
        require(!liquidato, "Already liquidated");
        require(!attivato, "Already activated");
        require(funded, "Contract has to be funded");
        token.transferFrom(
            assicurato,
            address(this),
            (premio * 5) / 100 // 5% del premio assicurativo
        );
        attivato = true;
    }

    modifier onlyInsurer() {
        require(
            msg.sender == assicuratore,
            "Only the insurer can call this function"
        );
        _;
    }

    modifier onlyInsured() {
        require(
            msg.sender == assicurato,
            "Only the insured can call this function"
        );
        _;
    }
}
