// SPDX-License-Identifier: MIT
pragma solidity ^0.8.9;

contract InsuranceContract {
    address public assicuratore;
    address payable public assicurato;
    uint public premio;
    bool public liquidato;

    constructor(address _assicuratore, address payable _assicurato) payable {
        assicuratore = _assicuratore;
        assicurato = _assicurato;
        premio = msg.value;
        liquidato = false;
    }

    function liquidazione() external payable restricted {
        require(!liquidato, "Already liquidated");
        assicurato.transfer(address(this).balance);
        liquidato = true;
    }

    modifier restricted() {
        require(msg.sender == assicuratore);
        _;
    }
}
