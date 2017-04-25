package utility;

public class PerlinNoise {
	
	private int[] p;
	
	private int repeat = 0;
	private SRandom random;
	
	public PerlinNoise() {
		
		random = new SRandom();
		
		p = new int[512];
		for(int x = 0; x < 256; x++) {
			p[x] = (int)Math.floor(random.nextDouble() * 256);
		}
		for(int x = 0; x < 256; x++) {
			p[x+256] = p[x]; 
		}
		
	}
	
	public PerlinNoise(long seed) {
		
		random = new SRandom(seed);
		
		p = new int[512];
		for(int x = 0; x < 256; x++) {
			p[x] = (int)Math.floor(random.nextDouble() * 256);
		}
		for(int x = 0; x < 256; x++) {
			p[x+256] = p[x]; 
		}
		
	}
	
	public double perlin(double x, double y, double z) {
		
		x = format(x);
		y = format(y);
		z = format(z);
		
		if(x > 255 || y>255 || z>255) {
			new Exception("Warning noise is repeated!").printStackTrace();
			/*
			 * 
			 * adjust 256, 255 and 512 to 65536, 65535 and 131072
			 * 
			 * 
			 */
		}
		
		if(repeat>0) {
			x = x%repeat;
			y = y%repeat;
			z = z%repeat;
		}
		
		int xi = (int)x & 255;
		int yi = (int)y & 255;
		int zi = (int)z & 255;
		double xf = x - (int)x;
		double yf = y - (int)y;
		double zf = z - (int)z;
		
		double u = fade(xf);
		double v = fade(yf);
		double w = fade(zf);
		
		int aaa, aba, aab, abb, baa, bba, bab, bbb;
	    aaa = p[p[p[    xi ]+    yi ]+    zi ];
	    aba = p[p[p[    xi ]+inc(yi)]+    zi ];
	    aab = p[p[p[    xi ]+    yi ]+inc(zi)];
	    abb = p[p[p[    xi ]+inc(yi)]+inc(zi)];
	    baa = p[p[p[inc(xi)]+    yi ]+    zi ];
	    bba = p[p[p[inc(xi)]+inc(yi)]+    zi ];
	    bab = p[p[p[inc(xi)]+    yi ]+inc(zi)];
	    bbb = p[p[p[inc(xi)]+inc(yi)]+inc(zi)];
	    
	    double x1, x2, y1, y2;
	    
	    x1 = lerp(    grad (aaa, xf  , yf  , zf),        
                grad (baa, xf-1, yf  , zf),            
                u);                                     
	    x2 = lerp(    grad (aba, xf  , yf-1, zf),           
	                grad (bba, xf-1, yf-1, zf),             
	                  u);
	    y1 = lerp(x1, x2, v);
	
	    x1 = lerp(    grad (aab, xf  , yf  , zf-1),
	                grad (bab, xf-1, yf  , zf-1),
	                u);
	    x2 = lerp(    grad (abb, xf  , yf-1, zf-1),
	                  grad (bbb, xf-1, yf-1, zf-1),
	                  u);
	    y2 = lerp (x1, x2, v);
	    
	    return (lerp (y1, y2, w)+1)/2;
		
	}
	
	public double perlin(double x, double y) {
		
		x = format(x);
		y = format(y);
		
		if(repeat>0) {
			x = x%repeat;
			y = y%repeat;
		}
		
		int xi = (int)x & 255;
		int yi = (int)y & 255;
		double xf = x - (int)x;
		double yf = y - (int)y;
		
		double u = fade(xf);
		double v = fade(yf);
		
		int aaa, aba, baa, bba;
	    aaa = p[p[p[    xi ]+    yi ]];
	    aba = p[p[p[    xi ]+inc(yi)]];
	    baa = p[p[p[inc(xi)]+    yi ]];
	    bba = p[p[p[inc(xi)]+inc(yi)]];
	    
	    double x1, x2, y1;
	    
	    x1 = lerp(    grad (aaa, xf  , yf),        
                grad (baa, xf-1, yf),            
                u);                                     
	    x2 = lerp(    grad (aba, xf  , yf-1),           
	                grad (bba, xf-1, yf-1),             
	                  u);
	    y1 = lerp(x1, x2, v);
	    
	    return (y1+1)/2;
		
	}
	
	public double fade(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}
	
	public int inc(int num) {
	    num++;
	    if (repeat > 0) num %= repeat;
	    
	    return num;
	}
	
	public double grad(int hash, double x, double y, double z) {
		int h = hash & 15;
		double u = h < 8 ? x : y;
		
		double v;
		
		if(h < 4) {
			v = y;
		} else if(h == 12 || h == 14) {
			v = x;
		} else {
			v = z;
		}
		
		return ((h&1) == 0 ? u : -u)+((h&2) == 0 ? v : -v);
	}
	
	public double grad(int hash, double x, double y) {
		int h = hash & 15;
		double u = h < 8 ? x : y;
		
		double v;
		
		if(h < 4) {
			v = y;
		} else {
			v = x;
		}
		
		return ((h&1) == 0 ? u : -u)+((h&2) == 0 ? v : -v);
	}
	
	public double fastGrad(int hash, double x, double y, double z) {
		switch(hash & 0xF)
	    {
	        case 0x0: return  x + y;
	        case 0x1: return -x + y;
	        case 0x2: return  x - y;
	        case 0x3: return -x - y;
	        case 0x4: return  x + z;
	        case 0x5: return -x + z;
	        case 0x6: return  x - z;
	        case 0x7: return -x - z;
	        case 0x8: return  y + z;
	        case 0x9: return -y + z;
	        case 0xA: return  y - z;
	        case 0xB: return -y - z;
	        case 0xC: return  y + x;
	        case 0xD: return -y + z;
	        case 0xE: return  y - x;
	        case 0xF: return -y - z;
	        default: return 0; // never happens
	    }
	}
	
	public double lerp(double a, double b, double x) {
		return a + x * (b-a);
	}
	
	public double OctavePerlin(double x, double y, double z, int octaves, double persistence) {
	    double total = 0;
	    double frequency = 1;
	    double amplitude = 1;
	    double maxValue = 0;  // Used for normalizing result to 0.0 - 1.0
	    for(int i=0;i<octaves;i++) {
	        total += perlin(x * frequency, y * frequency, z * frequency) * amplitude;
	        
	        maxValue += amplitude;
	        
	        amplitude *= persistence;
	        frequency *= 2;
	    }
	    
	    return total/maxValue;
	}
	
	public double OctavePerlin(double x, double y, int octaves, double persistence) {
	    double total = 0;
	    double frequency = 1;
	    double amplitude = 1;
	    double maxValue = 0;  // Used for normalizing result to 0.0 - 1.0
	    for(int i=0;i<octaves;i++) {
	        total += perlin(x * frequency, y * frequency) * amplitude;
	        
	        maxValue += amplitude;
	        
	        amplitude *= persistence;
	        frequency *= 2;
	    }
	    
	    return total/maxValue;
	}
	
	private double format(double d) {
		
		if(d < 0) {
			
			int a[] = ByteUtility.doubleToInts(d);
			a[0] = a[0] & Integer.MAX_VALUE;
			d = ByteUtility.intsToDouble(a);
			
		}
		
		return d;
		
	}
	
	
	public long getSeed() {
		return random.getSeed();
	}
	
}