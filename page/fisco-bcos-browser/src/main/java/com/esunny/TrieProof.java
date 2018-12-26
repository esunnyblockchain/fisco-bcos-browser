package com.esunny;


import java.util.Arrays;
import java.util.List;

import org.bcos.web3j.crypto.Hash;
import org.bcos.web3j.crypto.sm2.util.encoders.Hex;
import org.bcos.web3j.rlp.RlpDecoder;
import org.bcos.web3j.rlp.RlpEncoder;
import org.bcos.web3j.rlp.RlpList;
import org.bcos.web3j.rlp.RlpString;
import org.bcos.web3j.rlp.RlpType;
import org.bcos.web3j.utils.Numeric;
 

public class TrieProof {  
    
    /*
     * 交易收据SPV
     */
    public static boolean verify(String root, List<String> proofs, String index, String target) {
        try {
            if (index.startsWith("0x"))
                index = index.substring(2);
            byte[] key = RlpEncoder.encode(RlpString.create(Integer.parseInt(index, 16))); 
            Node node = verifyProof(root, key, proofs);  
            if (node.status ==  0)
                return false;
            if (!target.equals(new String(Hex.encode(Hash.sha3(node.hash)))))
                return false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } 
    }
    
    private static Node verifyProof(String root, byte[] key, List<String> proofs) throws Exception{
        root = Numeric.cleanHexPrefix(root);
        byte[] wantHash = Hex.decode(root.getBytes()); // 预期的哈希编码
        Nibbles keyNibbles = new Nibbles(key); // 将key转为Nibble的形式
        for (String proof : proofs) { 
            // 验证当前证据的哈希 是否与 预期的哈希 一致
            if (!Arrays.equals(Hash.sha3(Hex.decode(proof.getBytes())), wantHash)) {
                return Node.emptyNode();
            }
            // 根据当前节点的RLP和key，寻找下一节点
            Node node = getNode(keyNibbles, Hex.decode(proof));
            if (node.status == 0 || node.status == 1) // 失败 或者 成功找到叶子节点，返回结果
                return node;
            else
                wantHash = node.hash; // 得到下一个节点的哈希，需要验证是否与下一条证据的哈希一致
        }
        return Node.emptyNode();
    }

    private static Node getNode(Nibbles keyNibbles, byte[] proof) throws Exception {  
        if (isEmpty(proof))
            return Node.emptyNode();
        if (!isList(proof)) {
            return Node.emptyNode();
        }
        RlpList rlpList = (RlpList) RlpDecoder.decode(proof).getValues().get(0);
        return getNode(keyNibbles, rlpList);
    }
    
    private static Node getNode(Nibbles keyNibbles, RlpList rlpList) throws Exception {
        List<RlpType> items = rlpList.getValues(); 
        if (items.size() == 2) { // 扩展节点或叶子节点
            RlpString firstItem = (RlpString) items.get(0); 
            byte[] kBytes = firstItem.getBytes();
            Nibbles k = keyOf(kBytes); 
            int nibblePreLength = keyNibbles.getSharedPrefixLen(k); // 获取key和当前节点的key 的公共前缀长度
            if (nibblePreLength != k.size())   // 如果公共前缀长度 和 当前节点的key长度 不同，说明证据有问题
                return Node.emptyNode();
            RlpType rlp = items.get(1); // 获取value rlp
            if (nibblePreLength == keyNibbles.size() && isLeaf(kBytes)) {  // 如果是叶子节点，并且 公共前缀长度和key长度相同，说明key的路径已经全部匹配
                return Node.create(1, ((RlpString)rlp).getBytes()); // 找到叶子节点，返回叶子节点的value
            }
            else if (nibblePreLength < keyNibbles.size() && !isLeaf(kBytes))  { // 如果是扩展节点，并且公共前缀长度小于key长度，还要继续往下走
                keyNibbles.move(nibblePreLength);   // 移动key已经匹配的部分
                if (rlp instanceof RlpString) {  // 如果是String说明当前value已经是下一个节点了
                    return Node.create(2, ((RlpString)rlp).getBytes());
                } else { //如果是List,继续往下找
                    return getNode(keyNibbles, (RlpList) rlp);
                }
            }
            else // 如果是叶子节点但是key没有匹配完，或者是扩展节点而key已经匹配完了，或者都不是，说明证据有问题
                return Node.emptyNode();
        } else if (items.size() == 17) { // 分支节点
            if (keyNibbles.size() == 0) { // key已经匹配完了，分支节点的value就是要找的
                return Node.create(1, ((RlpString)items.get(16)).getBytes());
            } 
            RlpType rlp = items.get(keyNibbles.get(0)); // 从key的第一位中取一个nibble，找到对应槽中的值
            keyNibbles.move(1);
            if (rlp instanceof RlpString) { // 如果是String说明当前value已经是下一个节点了
                return Node.create(2, ((RlpString)rlp).getBytes());
            } else { //如果是List,继续往下找
                return getNode(keyNibbles, (RlpList) rlp);
            }   
        }
        return Node.emptyNode(); 
    }
    
    private static boolean isNull(byte[] rlpData) {
        return rlpData.length == 0;
    }
    
    private static boolean isEmpty(byte[] rlpData) {
        if (isNull(rlpData))
            return false;
        int firstByte = rlpData[0] & 0xff;
        return (firstByte == RlpDecoder.OFFSET_SHORT_STRING || firstByte == RlpDecoder.OFFSET_SHORT_LIST);
    }
    
    private static boolean isList(byte[] rlpData) {
        if (isNull(rlpData))
            return false;
        int firstByte = rlpData[0] & 0xff;
        return firstByte >= RlpDecoder.OFFSET_SHORT_LIST;
    }
    
    private static boolean isData(byte[] rlpData) {
        if (isNull(rlpData))
            return false;
        int firstByte = rlpData[0] & 0xff;
        return firstByte < RlpDecoder.OFFSET_SHORT_LIST;
    }
    
    private static boolean isLeaf(byte[] key) {
        if (key.length == 0)
            return false;
        // extension node even 0x0 odd 0x1
        // leaf node even 0x2 odd 0x3
        return (key[0] & 0x20) > 0;
    }
    
    private static Nibbles keyOf(byte[] rlpData) {
        int offset = 0;
        if (rlpData.length == 0) {
            offset = 0;
        } else if ((rlpData[0] & 0x10) > 0) {
            offset = 1;
        } else {
            offset = 2;
        }
        return new Nibbles(rlpData, offset);
    }
    
    private static class Node {
        Node(int status, byte[] hash) {
            this.status = status;
            this.hash = hash;
        }
        
        public static Node emptyNode() {
            return create(0, null);
        }
        
        public static Node create(int status, byte[] hash) {
            return new Node(status, hash);
        }
        
        public int status; // 0, 1, 2
        public byte[] hash; 
    } 
}
