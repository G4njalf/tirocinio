// SPDX-License-Identifier: MIT
pragma solidity ^0.8.9;

import "./InsuranceContract.sol";

contract InsuranceFactory {
    address public assicuratore;
    address public tokenAddress;
    address[] public allInsuranceContracts;
    event NewInsuranceContractCreated(
        address indexed contractAddress,
        address indexed assicurato,
        uint premioAssicurativo
    );
    mapping(address => address[]) public insuranceContractsByInsured;

    constructor(address _token) {
        assicuratore = msg.sender;
        tokenAddress = _token;
    }

    function createInsurance(
        address assicurato,
        uint premioAssicurativo
    ) external onlyInsurer returns (address) {
        InsuranceContract newInsurance = (new InsuranceContract)(
            assicuratore,
            assicurato,
            tokenAddress,
            premioAssicurativo
        );
        allInsuranceContracts.push(address(newInsurance));
        insuranceContractsByInsured[assicurato].push(address(newInsurance));
        emit NewInsuranceContractCreated(
            address(newInsurance),
            assicurato,
            premioAssicurativo
        );
        return address(newInsurance);
    }

    function getAllInsuranceContracts()
        external
        view
        onlyInsurer
        returns (address[] memory)
    {
        return allInsuranceContracts;
    }

    function getInsuranceContractsByInsured(
        address assicurato
    ) external view returns (address[] memory) {
        return insuranceContractsByInsured[assicurato];
    }

    modifier onlyInsurer() {
        require(msg.sender == assicuratore, "Only insurer can call this");
        _;
    }
}
