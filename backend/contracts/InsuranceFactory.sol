// SPDX-License-Identifier: MIT
pragma solidity ^0.8.9;

import "./InsuranceContract.sol";

contract InsuranceFactory {
    address public assicuratore;
    address public tokenAddress;
    address public gateAddress;
    address public zoniaTokenAddress;
    address[] public allInsuranceContracts;
    event NewInsuranceContractCreated(
        address indexed contractAddress,
        address indexed assicurato,
        uint premioAssicurativo
    );
    mapping(address => address[]) public insuranceContractsByInsured;

    constructor(address _token, address _gate, address _zoniaToken) {
        require(_token != address(0), "Invalid token address");
        require(_gate != address(0), "Invalid gate address");
        assicuratore = msg.sender;
        tokenAddress = _token;
        gateAddress = _gate;
        zoniaTokenAddress = _zoniaToken;
    }

    function createInsurance(
        address assicurato,
        uint premioAssicurativo
    ) external onlyInsurer returns (address) {
        require(premioAssicurativo > 0, "Premio must be greater than 0");
        InsuranceContract newInsurance = (new InsuranceContract)(
            assicuratore,
            assicurato,
            tokenAddress,
            premioAssicurativo,
            gateAddress,
            zoniaTokenAddress
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
