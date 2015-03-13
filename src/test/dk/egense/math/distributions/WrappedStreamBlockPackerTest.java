package dk.egense.math.distributions;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class WrappedStreamBlockPackerTest {


	@Test
	public void testSimpleCompressDecompress() {
	
		int[] dist = LongTailIntGenerator.GenerateLongtailDistribution(351000000,50000, (long) (Math.random()*100000000d));		
		WrappedStreamedBlockPacker wrapper = new WrappedStreamedBlockPacker(dist);
		
		//Test single access (slow as hell)
		assertEquals(dist[0],wrapper.getValue(0));
		assertEquals(dist[1],wrapper.getValue(1));
		assertEquals(dist[111111111],wrapper.getValue(111111111));
		assertEquals(dist[300000000],wrapper.getValue(300000000));
		assertEquals(dist[351000000],wrapper.getValue(351000000));
	}


	@Test
	public void testSimpleIncreaseValue() {	
		int[] dist = LongTailIntGenerator.GenerateLongtailDistribution(351000000,50000, (long) (Math.random()*100000000d));		
		WrappedStreamedBlockPacker wrapper = new WrappedStreamedBlockPacker(dist);
		int index= (int) (Math.random()*351000000d); 
		
		int value = wrapper.getValue(index);
		int testVal=value;
		for (int i=0;i<10;i++){
			wrapper.increaseValue(index);
			testVal++;
			assertEquals(testVal, wrapper.getValue(index));
		}				
	}

	
	@Test
	public void testSingleCache() {	
		int[] dist = LongTailIntGenerator.GenerateLongtailDistribution(351000000,50000, (long) (Math.random()*100000000d));		
		WrappedStreamedBlockPacker wrapper = new WrappedStreamedBlockPacker(dist);
	    int flush_cache_size = wrapper.getBLOCK_FLUSH_SIZE();
	    int index= (int) (Math.random()*351000000d);
	    int valBefore = wrapper.getValue(index);
	    int trueValue=valBefore;
	    for (int i=0;i<flush_cache_size-1;i++){
	    	wrapper.increaseCachedValue(index);	         	        
	    	trueValue++;
	    }
	    
	    assertEquals(valBefore, wrapper.getValue(index)); //It has not been flushed yet
	    
	    wrapper.increaseCachedValue(index); //this will flush
	    trueValue++;
	    assertEquals(trueValue, wrapper.getValue(index)); 
	    valBefore = wrapper.getValue(index);
	    
		//Fill up and manually flush
	    for (int i=0;i<100;i++){
	    	wrapper.increaseCachedValue(index);
	        trueValue++;
	    }
	    
	    assertEquals(valBefore, wrapper.getValue(index)); 
	    wrapper.flush();
	    assertEquals(trueValue, wrapper.getValue(index));
	    

	}
	
	@Test
	public void testFullDecompress() {
		
		int[] distOrg = LongTailIntGenerator.GenerateLongtailDistribution(351000000,50000, (long) (Math.random()*100000000d));				
		WrappedStreamedBlockPacker wrapper = new WrappedStreamedBlockPacker(distOrg);
    	wrapper.flush();
		int[] distDecompressed = wrapper.getUncompressed();
		
		assertEquals(distOrg.length,distDecompressed.length);
		for (int i=0;i<distOrg.length;i++){		
			assertEquals("error index: "+i,distOrg[i], distDecompressed[i]);
		}		
			
	}
	
	

}

