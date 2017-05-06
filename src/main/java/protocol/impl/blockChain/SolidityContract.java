package protocol.impl.blockChain;

/**
 * Created by alex on 24/04/17.
 */
public class SolidityContract {

    public static String soliditySrc =

        "pragma solidity ^0.4.8;\n" +
                "/* Manage Contract Ownership*/\n" +
                "\n" +
                "/// @title Trade contract\n" +
                "contract Trade {\n" +
                "    \n" +
                "    struct Member {\n" +
                "        bool isOwner;\n" +
                "        address add;\n" +
                "        string item;\n" +
                "        bool signed;\n" +
                "    }\n" +
                "\n" +
                "    /* Contract Variables and events */\n" +
                "    Member[] public members;\n" +
                "    address public owner;\n" +
                "    string public clauseA;\n" +
                "    string public clauseB;\n" +
                "    \n" +
                "    event checkStruct(bool isOwner, address add, string item, bool signed);\n" +
                "    \n" +
                "    // Defines Owner\n" +
                "    function owned() {\n" +
                "        owner = msg.sender;\n" +
                "    }\n" +
                "\n" +
                "    // Check ifOwner\n" +
                "    modifier onlyOwner {\n" +
                "        if (msg.sender != owner) throw;\n" +
                "        _;\n" +
                "    }\n" +
                "\n" +
                "    function transferOwnership(address newOwner) onlyOwner {\n" +
                "        owner = newOwner;\n" +
                "    }\n" +
                "    \n" +
                "    // Check ifMember\n" +
                "    modifier onlyMember {\n" +
                "        if (msg.sender != members[0].add && msg.sender != members[1].add)\n" +
                "            throw;\n" +
                "        _;\n" +
                "    }\n" +
                "    \n" +
                "    function getisOwnerU1() returns (bool isOwner){\n" +
                "        return members[0].isOwner;\n" +
                "    }\n" +
                "    \n" +
                "    function getisOwnerU2() returns (bool isOwner){\n" +
                "        return members[1].isOwner;\n" +
                "    }\n" +
                "    \n" +
                "    function getAddU1() returns (address addr){\n" +
                "        return members[0].add;\n" +
                "    }\n" +
                "    \n" +
                "    function getAddU2() returns (address addr){\n" +
                "        return members[1].add;\n" +
                "    }\n" +
                "    \n" +
                "    function getItemU1() returns (string item){\n" +
                "        return members[0].item;\n" +
                "    }\n" +
                "    \n" +
                "    function getItemU2() returns (string item){\n" +
                "        return members[1].item;\n" +
                "    }\n" +
                "    \n" +
                "    function getSignatureU1() returns (bool signed) {\n" +
                "        return members[0].signed;\n" +
                "    }\n" +
                "    \n" +
                "    function getSignatureU2() returns (bool signed) {\n" +
                "        return members[1].signed;\n" +
                "    }\n" +
                "    \n" +
                "    function getClauseA() returns (string clause) {\n" +
                "        return clauseA;\n" +
                "    }\n" +
                "    \n" +
                "    function getClauseB() returns (string clause) {\n" +
                "        return clauseB;\n" +
                "    }\n" +
                "    \n" +
                "    function Trade(address user1, address user2, string itemU1, string itemU2, string clause1, string clause2){\n" +
                "        initMember(user1, itemU1);\n" +
                "        initMember(user2, itemU2);\n" +
                "        clauseA = clause1;\n" +
                "        clauseB = clause2;\n" +
                "    }\n" +
                "    \n" +
                "    function initMember (address addr, string items) {\n" +
                "        if (msg.sender == owner) {\n" +
                "            members.push(Member({isOwner : true, add : addr, item : items, signed : false}));\n" +
                "            if(addr != msg.sender)\n" +
                "                throw;\n" +
                "        }\n" +
                "        else {\n" +
                "            members.push(Member({isOwner : false, add : addr, item : items, signed : false}));\n" +
                "            if(addr != msg.sender)\n" +
                "                throw;\n" +
                "        }\n" +
                "        \n" +
                "        for(uint i = 0; i < members.length; i++)\n" +
                "            checkStruct(members[i].isOwner, members[i].add, members[i].item, members[i].signed);\n" +
                "    }\n" +
                "    \n" +
                "    function signature() onlyMember {\n" +
                "        for(uint i = 0; i < members.length; i++){\n" +
                "            if (members[i].add == msg.sender){\n" +
                "                members[i].signed = true;\n" +
                "            }\n" +
                "        }\n" +
                "        \n" +
                "        for(uint j = 0; j < members.length; j++)\n" +
                "            checkStruct(members[j].isOwner, members[j].add, members[j].item, members[j].signed);\n" +
                "    }\n" +
                "    \n" +
                "    function launchTrade() onlyOwner returns (bool launch){\n" +
                "        uint valid = 0;\n" +
                "        for(uint i = 0; i < members.length; i++){\n" +
                "            if(members[i].signed == true)\n" +
                "                valid++;\n" +
                "        }\n" +
                "        \n" +
                "        if(valid == members.length)\n" +
                "            return true;\n" +
                "        else\n" +
                "            return false;\n" +
                "    }\n" +
                "}";



            /*"contract Signature {" +

                    "   function Signature(){ }" +
                    "   bool public signedUser1 = false ; " +

                    "   function getU1() constant returns (bool) { " +
                    "       return signedUser1 ; " +
                    "   } " +

                    "   function signatureUser1() { " +
                    "       signedUser1 = true ; " +
                    "   } " +

                    "} " ;*/



            /*"contract Signature {" +

            "  struct Contract {" +
            "    string itemU1;" +
            "    string itemU2;" +
            "    string user1;" +
            "    string user2;" +
            "  }" +

            "  bool public signedUser1;" +
            "  bool public signedUser2;" +

            "  Contract public contractSXP;" +

            "  function createContract(string user1, string user2, string itemU1, string itemU2){" +
            "    contractSXP.itemU1 = itemU1;" +
            "    contractSXP.itemU2 = itemU2;" +
            "    contractSXP.user1 = user1;" +
            "    contractSXP.user2 = user2;" +
            "  }" +

            "  function Signature(string user1addr, string user2addr, string itemU1, string itemU2){" +
            //"    if(msg.sender != user1addr)" +
            //"      throw;" +
            "    createContract(user1addr, user2addr, itemU1, itemU2);" +
            "      signedUser1 = false;" +
            "      signedUser2 = false;" +
            "  }" +

            "  function getU1() constant returns (bool) {" +
            "    return signedUser1;" +
            "  }" +

            "  function getU2() constant returns (bool) {" +
            "    return signedUser2;" +
            "  }" +

            "  function signatureUser1(){" +
            //"    if(msg.sender != contractSXP.user1)" +
            //"      throw;" +
            "    signedUser1 = true;" +
            "  }" +

            "  function signatureUser2(){" +
            //"    if(msg.sender != contractSXP.user2)" +
            //"      throw;" +
            "    signedUser2 = true;" +
            "  }" +

            "}" ;*/

}
