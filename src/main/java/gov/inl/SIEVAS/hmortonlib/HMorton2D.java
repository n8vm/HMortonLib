package gov.inl.SIEVAS.hmortonlib;

/**
 * Encode/decode 2D coordinates to Hierarchical Morton codes (HZ-order curve)
 *
 * @author Nate Morrical
 */
public class HMorton2D extends Morton2D {
	private int numLevels;
	private int last_bit_mask;

	public HMorton2D(int numLevels) {
		this.numLevels = numLevels;
		this.last_bit_mask = 1 << (2 * numLevels);
	}

    /**
     * Hierarchical Morton (hz-ordering) encoding with Lookup Table method and
     * some final bit manipulation
     *
     * @param x range is from 0 to 16777215.
     * @param y range is from 0 to 16777215.
     * @return	return HMorton Code as long .
     */
    public long encode(int x, int y) {
        long c = super.encode(x, y);

		c |= last_bit_mask; // set leftmost one
		c /= c&-c;          // remove trailing zeros
		c >>= 1;            // remove rightmost one

        return c;
    }

    /**
     * Decode HMorton (hz-ordering)
     *
     * @param c hierarchical morton code up to 48 bits
     * @return	array [x,y] .
     */
    public int[] decode(long c) {
		// Special case
    	if (c == 0) return new int[] {0, 0};

		// Add back rightmost one.
		c = (c << 1) | 1;

		// Determine highest bit
		long i = c;
		i |= (i >>  1);
		i |= (i >>  2);
		i |= (i >>  4);
		i |= (i >>  8);
		i |= (i >> 16);
		i |= (i >> 32);
		i = i - (i >>> 1);

		// Shift left by max bits - highest bit index.
		c = c * (last_bit_mask / i);

		// Mask the number to remove added 1.
		c &= ~last_bit_mask;

		return super.decode(c);
    }
}
