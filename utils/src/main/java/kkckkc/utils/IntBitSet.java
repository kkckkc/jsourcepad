package kkckkc.utils;

public class IntBitSet {

    private int bits;

    public IntBitSet(int bits) {
        this.bits = bits;
    }

    public boolean getBit(int idx) {
        checkIndex(idx);
        return (bits & (1 << idx)) != 0;
    }

    public void setBit(int idx) {
        checkIndex(idx);
        bits |= 1 << idx;
    }

    public void clearBit(int idx) {
        checkIndex(idx);
        bits &= ~(1 << idx);
    }

    protected void checkIndex(int idx) {
        if (idx < 0 || idx > 31)
            throw new IllegalArgumentException("index: " + idx);
    }

}
