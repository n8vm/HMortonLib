package gov.inl.SIEVAS.hmortonlib;

/**
 * Encode/decode 2D coordinates to Hierarchical Morton codes (HZ-order curve)
 *
 * @author Nate Morrical
 */
public class HMorton3D extends Morton3D {
	private int numLevels;
	private int last_bit_mask;

	public HMorton3D(int numLevels) {
		this.numLevels = numLevels;
		this.last_bit_mask = 1 << (3 * numLevels);
	}

	/**
	 * Hierarchical Morton (hz-ordering) encoding with Lookup Table method and
	 * some final bit manipulation
	 *
	 * @param x range is from 0 to 2097151.
	 * @param y range is from 0 to 2097151.
	 * @param z range is from 0 to 2097151.
	 *
	 * @return	return HMorton Code as long .
	 */
    public long encode(int x, int y, int z) {
        long c = super.encode(x, y, z);

		c |= last_bit_mask; // set leftmost one
		c /= c&-c;          // remove trailing zeros
		c >>= 1;            // remove rightmost one

        return c;
    }

	/**
	 * Decode Hierarchical Morton (hz-ordering)
	 *
	 * @param c hierarchical morton code up to 64 bits
	 * @return	array [x,y,z] .
	 */
    public int[] decode(long c) {
		// Special case
    	if (c == 0) return new int[] {0, 0, 0};

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
