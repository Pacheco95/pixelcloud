package br.ufop.decom.dataStructures;

@SuppressWarnings("ALL")
public class Pixel {
    public short x;
    public short y;
    public short r;
    public byte g;
    public byte b;

    public Pixel(short x, short y, short r, byte g, byte b) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
