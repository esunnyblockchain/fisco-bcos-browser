package org.bcos.browser.dto;

public class TbCnsContractDto {
    public String contractName;
    public String contractAddress;
    public String startBlock;
    public String endBlock;
    public String abi;
    public String code;
    public String bin;
    public String version;
    
   
    public String getContractName() {
        return contractName;
    }
    public void setContractName(String contractName) {
        this.contractName = contractName;
    }
    public String getContractAddress() {
        return contractAddress;
    }
    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
    public String getStartBlock() {
        return startBlock;
    }
    public void setStartBlock(String startBlock) {
        this.startBlock = startBlock;
    }
    public String getEndBlock() {
        return endBlock;
    }
    public void setEndBlock(String endBlock) {
        this.endBlock = endBlock;
    }
    public String getAbi() {
        return abi;
    }
    public void setAbi(String abi) {
        this.abi = abi;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getBin() {
        return bin;
    }
    public void setBin(String bin) {
        this.bin = bin;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"contractName\":\"")
                .append(contractName).append('\"');;
        sb.append(",\"contractAddress\":\"")
                .append(contractAddress).append('\"');
        sb.append(",\"startBlock\":\"")
                .append(startBlock).append('\"');
        sb.append(",\"endBlock\":\"")
                .append(endBlock).append('\"');
        sb.append(",\"abi\":")
                .append(abi);
        sb.append(",\"code\":\"")
                .append(code).append('\"');
        sb.append(",\"bin\":\"")
                .append(bin).append('\"');
        sb.append(",\"version\":\"")
                .append(version).append('\"');
        sb.append('}');
        return sb.toString();
    }

}
