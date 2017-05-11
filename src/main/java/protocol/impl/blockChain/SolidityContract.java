package protocol.impl.blockChain;

import model.entity.ContractEntity;

/**
 * Created by alex on 24/04/17.
 */
public class SolidityContract {

    public static String soliditySrc =

       /*"pragma solidity ^0.4.8;\n" +
               "\n" +
               "contract Calcul {\n" +
               "    struct Stock {\n" +
               "        int a;\n" +
               "        int b;\n" +
               "        int res;\n" +
               "    }\n" +
               "    \n" +
               "    Stock public st;\n" +
               "    \n" +
               "    function Calcul(int first, int second, int result) {\n" +
               "        initStruct(first, second, result);\n" +
               "    }\n" +
               "    \n" +
               "    function getMsgSender() returns(address add) {\n" +
               "        return msg.sender;\n" +
               "    }\n" +
               "    \n" +
               "    function initStruct(int first, int second, int result) internal {\n" +
               "        st = Stock({a:first, b:second, res:result});\n" +
               "    }\n" +
               "    \n" +
               "    function checkRes() returns(bool isTrue) {\n" +
               "        if(st.res == (st.a + st.b))\n" +
               "            return true;\n" +
               "        \n" +
               "        return false;\n" +
               "    }\n" +
               "}";*/

       "pragma solidity ^0.4.10;\n" +
               "contract Trade {\n" +
               "    struct Member {\n" +
               "        address add;\n" +
               "        string item;\n" +
               "        bool signed;\n" +
               "    }\n" +
               "        Member public member1;\n" +
               "        Member public member2;\n" +
               "        string public clauseA;\n" +
               "        string public clauseB;\n" +
               "        address public sender;\n" +
               "    \n" +
               "        function init(address add1, address add2, string item1, string item2, string clause1, string clause2) {\n" +
               "            sender = msg.sender;\n" +
               "            if(msg.sender != sender || member1.add == add1)\n" +
               "                throw;\n" +
               "            member1.add = add1;\n" +
               "            member1.item = item1;\n" +
               "            member2.add = add2;\n" +
               "            member2.item = item2;\n" +
               "            clauseA = clause1;  \n" +
               "            clauseB = clause2;\n" +
               "            \n" +
               "        }\n" +
               "        \n" +
               "        function getSender() returns(address add) {\n" +
               "            return sender;\n" +
               "        }\n" +
               "        \n" +
               "        function signature() {\n" +
               "            sender = msg.sender;\n" +
               "            if(member1.add == sender)\n" +
               "                member1.signed = true;\n" +
               "            else if(member2.add == sender)\n" +
               "                member2.signed = true;\n" +
               "            else throw;\n" +
               "        }\n" +
               "        \n" +
               "        function getAdd1() constant returns(address add) {\n" +
               "                return member1.add;\n" +
               "        }\n" +
               "        \n" +
               "        function getAdd2() constant returns(address add) {\n" +
               "            return member2.add;\n" +
               "        }\n" +
               "        \n" +
               "        function getItem1() constant returns(string item) {\n" +
               "            return member1.item;\n" +
               "        }\n" +
               "    \n" +
               "        function getItem2() constant returns(string item) {\n" +
               "            return member2.item;\n" +
               "        }\n" +
               "        \n" +
               "        function getSignature() constant returns(bool signed) {\n" +
               "              if(sender == member1.add)\n" +
               "                return member1.signed;\n" +
               "            else if(sender == member2.add)\n" +
               "                return member2.signed;\n" +
               "            else throw;\n" +
               "        }\n" +
               "        \n" +
               "        function getClauseA() constant returns(string clause) {\n" +
               "            return clauseA;\n" +
               "        }\n" +
               "        \n" +
               "        function getClauseB() constant returns(string clause) {\n" +
               "            return clauseB;\n" +
               "        }\n" +
               "}";

       
      /* "pragma solidity ^0.4.8;\n" +
               "contract Trade {\n" +
               "    struct Member {\n" +
               "        address add;\n" +
               "        string item;\n" +
               "        bool signed;\n" +
               "        string clauseA;\n" +
               "        string clauseB;\n" +
               "    }\n" +
               "        Member public member1;\n" +
               "        Member public member2;\n" +
               "        string public clauseA;\n" +
               "        string public clauseB;\n" +
               "        address public member;\n" +
               "        \n" +
               "        modifier onlyMember {\n" +
               "            if(msg.sender != member)\n" +
               "                throw;\n" +
               "            _;\n" +
               "        }\n" +
               "    \n" +
               "        function init(address add1, address add2, string item1, string item2, string clause1, string clause2) {\n" +
               "            // member = msg.sender;\n" +
               "            member1.add = add1;\n" +
               "            member1.item = item1;\n" +
               "            member2.add = add2;\n" +
               "            member2.item = item2;\n" +
               "            clauseA = clause1;  \n" +
               "            clauseB = clause2;\n" +
               "        }\n" +
               "        \n" +
               "        function signature(address addr) {\n" +
               "            if(member1.add == addr)\n" +
               "                member1.signed = true;\n" +
               "            if(member2.add == addr)\n" +
               "                member2.signed = true;\n" +
               "        }\n" +
               "        \n" +
               "        function getTxSender() returns(address add) {\n" +
               "            return tx.origin;\n" +
               "        }\n" +
               "        \n" +
               "        function getMsgSender() returns(address add) {\n" +
               "            return msg.sender;\n" +
               "        }\n" +
               "        \n" +
               "        function getAdd1() constant returns(address add) {\n" +
               "            return member1.add;\n" +
               "        }\n" +
               "        \n" +
               "        function getAdd2() constant returns(address add) {\n" +
               "            return member2.add;\n" +
               "        }\n" +
               "        \n" +
               "        function getItem1() constant returns(string item) {\n" +
               "            return member1.item;\n" +
               "        }\n" +
               "    \n" +
               "        function getItem2() constant returns(string item) {\n" +
               "            return member2.item;\n" +
               "        }\n" +
               "        \n" +
               "        function getSignature1() constant returns(bool signed) {\n" +
               "            return member1.signed;\n" +
               "        }\n" +
               "        \n" +
               "        function getSignature2() constant returns(bool signed) {\n" +
               "            return member2.signed;\n" +
               "        }\n" +
               "        \n" +
               "        function getClauseA() constant returns(string clause) {\n" +
               "            return clauseA;\n" +
               "        }\n" +
               "        \n" +
               "        function getClauseB() constant returns(string clause) {\n" +
               "            return clauseB;\n" +
               "        }\n" +
               "}";*/



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
