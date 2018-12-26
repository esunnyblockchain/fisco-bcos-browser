package org.bcos.browser.entity.rsp;

import java.util.ArrayList;
import java.util.List;

import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.protocol.core.methods.response.AbiDefinition.NamedType;

import com.esunny.EventResult;

public class RspVerifyResult {
    
    public Boolean valid;
    
    public Integer blockNumber;
    public String blockHash;
    public String timeStr;
    
    public Integer transactionIndex;
    
    public String transactionHash;
    
    public String transactionFrom;
    
    public String transactionTo;
     
    public List<RspEvent> events = new ArrayList<RspEvent>();    
    
    public static class RspEvent {
        public String contractAddress;
        public String contractName;
        public String eventName;
        public List<String> eventParams = new ArrayList<String>();
        public List<String> eventValues = new ArrayList<String>();
    }
}
