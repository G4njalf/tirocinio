// SPDX-License-Identifier: MIT
// Compatible with OpenZeppelin Contracts ^5.0.0
pragma solidity ^0.8.27;

import {ERC20} from "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import {ERC20Permit} from "@openzeppelin/contracts/token/ERC20/extensions/ERC20Permit.sol";
import {Ownable} from "@openzeppelin/contracts/access/Ownable.sol";

contract MyToken is ERC20, Ownable, ERC20Permit {
    constructor(
        address recipient, // address recipient is the address that will receive the initial supply of tokens
        address initialOwner // address initialOwner is the address that will be the owner of the contract
    ) ERC20("MyToken", "MTK") Ownable(initialOwner) ERC20Permit("MyToken") {
        _mint(recipient, 1000000 * 10 ** decimals());
    }

    function mint(address to, uint256 amount) public onlyOwner {
        _mint(to, amount);
    }
}
