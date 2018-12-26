package com.esunny;


public class Nibbles {
    
    private byte[] data = null;
    private int offset = 0;
    private int length = 0;
    
    public Nibbles(byte[] data) {
        this(data, 0, 2 * data.length);
    }
    
    public Nibbles(byte[] data, int offset, int length) {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }
    
    public Nibbles(byte[] data, int offset) {
        this(data, offset, 2 * data.length);
    }
    
    public int size() {
        return length - offset;
    }
    
    public boolean move(int size) {
        if (size <= size()) {
            offset += size;
            return true;
        }
        return false;
    }
    
    public int getSharedPrefixLen(Nibbles nibble) {
         int length1 = size();
         int length2 = nibble.size();
         int preLength = 0; 
         while (preLength < length1 && preLength < length2 && get(preLength) == nibble.get(preLength))
             ++preLength;
         return preLength;
    }
    
    public int get(int index) {
        index += offset;
        if ((index & 1) > 0) { // odd
            // first part of bytes
            return (data[index / 2] & 0xff) & 0xf;
        } else { // even
            // second part of bytes
            return (data[index / 2] & 0xff) >> 4;
        }
    }
}
