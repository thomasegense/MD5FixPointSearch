package dk.egense.math.distributions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FullBuildTest {

	@Test
	public void fullBuildTest() {
	
        WrappedStreamedBlockPacker wrapper = null;
        
        int length;
    
        int[] data = LongTailIntGenerator.GenerateLongtailDistribution(640000000,500000,101);

        length=data.length; 
        wrapper = new WrappedStreamedBlockPacker(data);
     
               
        wrapper.logCompressionData();
        
    //    System.out.println(data[12345677] +" : "+wrapper.getValue(12345677));
    
        long start= System.currentTimeMillis();
        for (int i =0;i<100000000;i++){
         int index = (int) (1d*Math.random()*length);
            wrapper.increaseCachedValue(index);
     
            //Also update org data for comparison:
            data[index]=data[index]+1;
            
            if (i%1000000==0){
                long time = System.currentTimeMillis()-start;
                System.out.println("total updates:"+i +" total time:"+time +" updates/ms:"+1d*i/time);     
            }        
        }
         
        wrapper.flush(); 
    
        
           int[] dataUncompressed= wrapper.getUncompressed();

           assertEquals(data.length, dataUncompressed.length);
           for (int i=0;i<data.length;i++){
        	   assertEquals("Data error for index:"+i,data[i], dataUncompressed[i]);
           }
           
        
	}

}
