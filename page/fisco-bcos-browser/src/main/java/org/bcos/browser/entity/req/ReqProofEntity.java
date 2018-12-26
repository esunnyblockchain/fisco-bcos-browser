package org.bcos.browser.entity.req;

import java.util.List;

import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.protocol.core.methods.response.EthBlock.Block;

public class ReqProofEntity {
    
    public Block block;
    public List<String> proofs;
    public TransactionReceipt receipt;
}
