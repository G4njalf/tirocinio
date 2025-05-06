// SPDX-License-Identifier: MIT
pragma solidity ^0.8.9;

import "./InsuranceContract.sol";

contract InsuranceFactory {
    address public assicuratore;
    address public tokenAddress;
    address[] public allInsuranceContracts;

    constructor(address _token) {
        assicuratore = msg.sender;
        tokenAddress = _token;
    }

    function createInsurance(
        address assicurato,
        uint premioAssicurativo
    ) external returns (address) {
        InsuranceContract newInsurance = (new InsuranceContract)(
            assicuratore,
            assicurato,
            tokenAddress,
            premioAssicurativo
        );
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
