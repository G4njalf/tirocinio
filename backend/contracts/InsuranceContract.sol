// SPDX-License-Identifier: MIT
pragma solidity ^0.8.9;
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";
import "@openzeppelin/contracts/utils/Strings.sol";

using Strings for uint256;

interface IGate {
    enum RequestStatus {
        Void,
        Created,
        Ready,
        Completed,
        Failed
    }

    struct ChainParams {
        uint256 w1;
        uint256 w2;
        uint256 w3;
        uint256 w4;
    }

    struct InputRequest {
        string query;
        ChainParams chainParams;
        uint256 ko;
        uint256 ki;
        uint256 fee;
    }

    struct Node {
        string did;
        address addr;
    }

    struct Request {
        string query;
        ChainParams chainParams;
        address consumer;
        uint256 fee;
        uint256 timestamp;
        uint256 ko;
        uint256 ki;
        RequestStatus status;
        string result;
        Node[] indexers;
        Node[] committee;
    }

    function submitRequest(
        InputRequest calldata inputRequest
    ) external returns (bytes32);

    function getRequest(
        bytes32 requestId
    ) external view returns (Request memory);

    function getResult(bytes32 requestId) external view returns (string memory);
}

contract InsuranceContract {
    address public assicuratore;
    address public assicurato;
    uint public premio;
    bool public liquidato;
    bool public attivato;
    bool public funded;
    IERC20 public token; // ERC20 token
    string public version = "0.4";
    IGate public gate;
    bytes32 public requestId;
    IERC20 public zoniaToken;
    string public topic; // Humidity or Temperature
    uint256 public lat; // * 10000
    uint256 public lng; // * 10000
    uint public chp1;
    uint public chp2;
    uint public chp3;
    uint public chp4;
    uint public ko;
    uint public ki;
    uint public fee;

    event Liquidation(
        address contractAddress,
        address indexed assicurato,
        address indexed assicuratore,
        uint premio,
        bool liquidato,
        uint8 reqStatus,
        string reqResult
    );

    event Funded(
        address contractAddress,
        address indexed assicurato,
        address indexed assicuratore,
        uint premio,
        bool funded
    );

    event Activated(
        address contractAddress,
        address indexed assicurato,
        address indexed assicuratore,
        uint premio,
        bool attivato
    );

    constructor(
        address _assicuratore,
        address _assicurato,
        address _token,
        uint _premio,
        address _gate,
        address _zoniaToken,
        string memory _topic,
        uint256 _lat,
        uint256 _lng
    ) {
        assicuratore = _assicuratore;
        assicurato = _assicurato;
        liquidato = false;
        premio = _premio;
        token = IERC20(_token);
        attivato = false;
        funded = false;
        gate = IGate(_gate);
        zoniaToken = IERC20(_zoniaToken);
        topic = _topic;
        lat = _lat;
        lng = _lng;
    }

    function helper(uint256 value) internal pure returns (string memory) {
        uint256 integerPart = value / 10000; // parte intera
        uint256 decimalPart = value % 10000; // parte decimale

        // padding a sinistra della parte decimale
        string memory decStr = decimalPart.toString();
        while (bytes(decStr).length < 4) {
            decStr = string(abi.encodePacked("0", decStr));
        }

        return string(abi.encodePacked(integerPart.toString(), ".", decStr));
    }

    // per adesso gli passo qui chp ko ki ecc perche se li passo dal costruttore mi dice che la stack e troppo profonda
    function fundContract(
        uint _chp1,
        uint _chp2,
        uint _chp3,
        uint _chp4,
        uint _ko,
        uint _ki,
        uint _fee
    ) public onlyInsurer {
        require(!funded, "Already funded");
        require(
            _chp1 + _chp2 + _chp3 + _chp4 == 100 &&
                _ko > 0 &&
                _ki > 0 &&
                _fee > 0,
            "error in parameters"
        );
        chp1 = _chp1;
        chp2 = _chp2;
        chp3 = _chp3;
        chp4 = _chp4;
        ko = _ko;
        ki = _ki;
        fee = _fee;
        token.transferFrom(
            assicuratore,
            address(this),
            premio // l assicuratore manda il premio all assicurato
        );
        funded = true;
        emit Funded(address(this), assicurato, assicuratore, premio, true);
    }

    function liquidazione() external onlyInsured {
        /* premo verifica manuale dall app -> parte sta funzione -> interroga un contratto che mi devono dare l abi
            l abi mi da un threshold -> controllo, se ok liquido
            quando cho l abi poi vedo come farlo 
        */
        require(!liquidato, "Already liquidated");
        require(attivato, "Contract not activated yet");
        require(funded, "Contract has to be funded");
        require(requestId != bytes32(0), "No request submitted yet");

        IGate.Request memory r;

        try gate.getRequest(requestId) returns (IGate.Request memory result) {
            r = result;
        } catch {
            revert("getRequest failed");
        }

        require(
            r.status == IGate.RequestStatus.Created,
            "Request not created yet"
        ); // dovrei in realta contrrolarre se e completed (3)

        token.transfer(assicurato, premio); // trasferisce il premio all assicurato
        liquidato = true;
        emit Liquidation(
            address(this),
            assicurato,
            assicuratore,
            premio,
            true,
            uint8(r.status),
            r.result
        );
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
        emit Activated(address(this), assicurato, assicuratore, premio, true);
    }

    function requestZoniaData() external onlyInsured {
        require(!liquidato, "Already liquidated");
        require(attivato, "Contract not activated yet");
        require(funded, "Contract has to be funded");

        require(
            zoniaToken.allowance(assicurato, address(this)) >= 1,
            "Insufficient ZoniaToken allowance"
        );

        require(
            zoniaToken.balanceOf(assicurato) >= 1,
            "Insufficient ZoniaToken balance"
        );

        IGate.InputRequest memory req = IGate.InputRequest({
            query: string(
                abi.encodePacked(
                    '{"topic":"saref:',
                    topic,
                    '","geo":{"type":"Feature","geometry":{"type":"Point","coordinates":[',
                    helper(lat),
                    ",",
                    helper(lng),
                    ']},"properties":{"radius":1000}}}'
                )
            ),
            chainParams: IGate.ChainParams(chp1, chp2, chp3, chp4),
            ko: ko,
            ki: ki,
            fee: fee
        });

        require(
            zoniaToken.transferFrom(assicurato, address(this), fee),
            "TransferFrom failed"
        );

        require(
            zoniaToken.approve(address(gate), fee),
            "Approve to Gate failed"
        );

        requestId = gate.submitRequest(req);
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
