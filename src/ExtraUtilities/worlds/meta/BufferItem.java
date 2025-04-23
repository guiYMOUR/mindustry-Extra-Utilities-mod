package ExtraUtilities.worlds.meta;


public final class BufferItem {
    public static final long bitMaskItem = 255L;
    public static final long bitMaskTime = 1099511627520L;

    public BufferItem() {
    }

    public static byte item(long bufferitem) {
        return (byte)((int)(bufferitem & 255L));
    }

    public static long item(long bufferitem, byte value) {
        return bufferitem & -256L | (long) value;
    }

    public static float time(long bufferitem) {
        return Float.intBitsToFloat((int)(bufferitem >>> 8 & 4294967295L));
    }

    public static long time(long bufferitem, float value) {
        return bufferitem & -1099511627521L | (long)Float.floatToIntBits(value) << 8;
    }

    public static long get(byte item, float time) {
        return (long) item & 255L | (long)Float.floatToIntBits(time) << 8;
    }
}
