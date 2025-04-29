// SPDX-License-Identifier: MIT
pragma solidity ^0.8.9;

import "./InsuranceContract.sol";

contract InsuranceFactory {
    address public assicuratore;
    address[] public allInsuranceContracts;

    constructor() {
        assicuratore = msg.sender;
    }

    function createInsurance(
        address payable assicurato,
        uint premioAssicurativo
    ) external payable returns (address) {
        require(msg.value == premioAssicurativo, "Deposit mismatch");
        InsuranceContract newInsurance = (new InsuranceContract){
            value: msg.value
        }(assicuratore, assicurato);
        allInsuranceContracts.push(address(newInsurance));
        return address(newInsurance);
    }

    function getAllInsuranceContracts()
        external
        view
        returns (address[] memory)
    {
        return allInsuranceContracts;
    }
}
