package org.bcos.browser.entity.rsp;

import java.io.Serializable;

public class RspTbWarrantInfoVO implements Serializable {

    private static final long serialVersionUID = 7785790370982919451L;
    
    private Integer warrantId;
    
    private String warrantName;
    
    private Integer warrantQty;
    
    private String warrantOwner;
    
    private String transactionHash;
    
    private Integer blockNumber;
    
    private String dateTimeStr;

    public Integer getWarrantId() {
        return warrantId;
    }

    public void setWarrantId(Integer warrantId) {
        this.warrantId = warrantId;
    }

    public String getWarrantName() {
        return warrantName;
    }

    public void setWarrantName(String warrantName) {
        this.warrantName = warrantName;
    }

    public Integer getWarrantQty() {
        return warrantQty;
    }

    public void setWarrantQty(Integer warrantQty) {
        this.warrantQty = warrantQty;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Integer getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Integer blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getDateTimeStr() {
        return dateTimeStr;
    }

    public void setDateTimeStr(String dateTimeStr) {
        this.dateTimeStr = dateTimeStr;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getWarrantOwner() {
        return warrantOwner;
    }

    public void setWarrantOwner(String warrantOwner) {
        this.warrantOwner = warrantOwner;
    }

}
