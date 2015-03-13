package dk.egense.math.distributions;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class FastPFORCompresserUtilTest {

	
	@Test
	public void testSimpleCompressDecompress() {
	
		int[] dist = LongTailIntGenerator.GenerateLongtailDistribution(3120000,50000, (long) (Math.random()*100000000d));
	    int[] compressed = FastPFORCompresserUtil.compress(dist);
	    int[] unCompressed = FastPFORCompresserUtil.unCompress(compressed, dist.length);
	    System.out.println("compress rate:"+(100d*compressed.length/dist.length)+"%");
	    
	    
	    
	    assertTrue(Arrays.equals(dist, unCompressed));
		
	    for (int i = 0;i<unCompressed.length;i++){
            if (dist[i] != unCompressed[i]){
                System.out.println("error index:"+i+ " values "+ dist[i]+":"+unCompressed[i] );
            }
        }
	    
	}

}
