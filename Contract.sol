pragma solidity ^0.4.8;

                
    /// @title Trade contract
    contract Trade {
        struct Member {
            address add;
            string item;
            bool signed;
        }
        
        /* Contract Variables and events */
        string public clauseA;
        string public clauseB;
        Member[] public members;
                
        // Check ifMember
        modifier onlyMember {
        if (msg.sender != members[0].add && msg.sender != members[1].add)
            throw;
        _;
        }
             
        function init(address memberU1, address memberU2, string itemU1, string itemU2, string clause1, string clause2){
            if(msg.sender!= memberU1)
                throw;
            if(members[0].add == memberU1)
                throw;
            members[0] = Member({add:memberU1, item:itemU1, signed:false});
            members[1] = Member({add:memberU2, item:itemU2, signed:false});
            clauseA = clause1;
            clauseB = clause2;
        }
                
        function signature(string items) onlyMember {
            
            for(uint i = 0; i < members.length; i++)
                if(msg.sender == members[i].add)
                    members[i].signed = true;
        }
                    
        function launchTrade() onlyMember returns (bool launch){
            for(uint i = 0; i < members.length; i++)
                if(members[i].signed != true)
                    return false;
                        
            return true;
        }
                    
        function getAdds() onlyMember returns (address[] adds){
            for(uint i = 0; i < members.length; i++)
                adds[i] = members[i].add;
            return adds;
        }
                    
        function getItem() onlyMember returns (string item){
            for(uint i = 0; i < members.length; i++)
                if(msg.sender == members[i].add)
                    item = members[i].item;
            return item;
        }
                    
        function getSignatures() onlyMember returns (bool[] signatures) {
            for(uint i = 0; i < members.length; i++)
                signatures[i] == members[i].signed;
            return signatures;
        }
            
        function getClauseA() onlyMember constant returns (string clause) {
            return clauseA;
        }
            
        function getClauseB() onlyMember constant returns (string clause) {
            return clauseB;
        }
    }