package protocol.impl.blockChain;

/**
 * Created by alex on 24/04/17.
 */
public class SolidityContract {

    public static String soliditySrc =

        "pragma solidity ^0.4.2;\n" +
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
                "    int MAXMEMBERS = 5;\n" +
                "    uint public indice = 0;\n" +
                "    \n" +
                "    Member public m;\n" +
                "    address public owner;\n" +
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
                "        uint cmp = 0;\n" +
                "        \n" +
                "        if (msg.sender != members[0].add && msg.sender != members[1].add)\n" +
                "            throw;\n" +
                "        _;\n" +
                "    }\n" +
                "    \n" +
                "    function initMember1 () {\n" +
                "        if (msg.sender == owner)\n" +
                "            m.isOwner = true;\n" +
                "        else\n" +
                "            m.isOwner = false;\n" +
                "        m.add = msg.sender;\n" +
                "        m.item = \"item\";\n" +
                "        m.signed = false;\n" +
                "        members[0] = m;\n" +
                "        indice++;\n" +
                "    }\n" +
                "    \n" +
                "    function initMember2 () {\n" +
                "        \n" +
                "        if (msg.sender == owner)\n" +
                "            m.isOwner = true;\n" +
                "        else\n" +
                "            m.isOwner = false;\n" +
                "        m.add = msg.sender;\n" +
                "        m.item = \"item\";\n" +
                "        m.signed = false;\n" +
                "        members[1] = m;\n" +
                "        indice++;\n" +
                "    }\n" +
                "    \n" +
                "    function signature1() onlyMember {\n" +
                "        if(members[0].add == msg.sender)\n" +
                "            members[0].signed = true;\n" +
                "    }\n" +
                "    \n" +
                "    function signature2() onlyMember {\n" +
                "        if(members[1].add == msg.sender)\n" +
                "            members[1].signed = true;\n" +
                "    }\n" +
                "    \n" +
                "    function launchTrade() onlyOwner returns (bool launch){\n" +
                "        uint valid = 0;\n" +
                "        \n" +
                "        if(members[0].signed == true && members[1].signed == true)\n" +
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
