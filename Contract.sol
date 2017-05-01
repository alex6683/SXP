pragma solidity ^0.4.2;
/* Manage Contract Ownership*/
contract owned {
    address public owner;

    // Defines Owner
    function owned() {
        owner = msg.sender;
    }

    // Check ifOwner
    modifier onlyOwner {
        if (msg.sender != owner) throw;
        _;
    }

    function transferOwnership(address newOwner) onlyOwner {
        owner = newOwner;
    }
}

contract Trade is owned {

    /* Contract Variables and events */
    // This declares a state variable that
    // stores a `Voter` struct for each possible address.
    mapping(address => Member) public membersAdd;
    Member[] public members;
    
    int MAXMEMBERS = 5;

    event Voted(uint proposalID, bool position, address voter, string justification);
    event MembershipChanged(address member, bool isMember);
    int indice = 0;

    struct Member {
        bool isOwner;
        address add;
        string item;
        bool signed;
    }
    
    // Check ifMember
    modifier onlyMember {
        int cmp = 0;
        
        for(int i = 0; i < members.length; i++){
            if (msg.sender != members[i].add)
                cmp++;
        }
        
        if(cmp == members.length) throw;
        _;
    }
    
    function initMember (string item) returns (Member member){
        
        Member m;
        
        if (msg.sender == owner)
            m.isOwner = true;
        else
            m.isOwner = false;
        m.add = msg.sender;
        m.item = item;
        m.signed = false;
    }
    
    function addMember(string item) onlyOwner {
        members[indice] = initMember(item);
        indice++;
    }
    
    function signature() onlyMember {
        for(int i = 0; i < members.length; i++){
            if(members[i].add == msg.sender)
                m.signed = true;
        }
    }
    
    function launchTrade() onlyOwner returns (bool launch){
        int valid = 0;
        for(int i = 0; i < members.length; i++){
            if(members[i].signed == true)
                valid++;
        }
        
        if(valid == members.length)
            return true;
        else
            return false;
    }
}