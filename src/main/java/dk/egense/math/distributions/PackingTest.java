package dk.egense.math.distributions;



import java.util.Arrays;


public class PackingTest {


    public static void main(String[] args){

        testBlockStreamCompressionWrapper();
           //unsortedExample();       
    }
   
    public static void testBlockStreamCompressionWrapper(){
        WrappedStreamedBlockPacker wrapper = null;
        
        int length;
    
        int[] data = LongTailIntGenerator.GenerateLongtailDistribution(51200000,500000,101);
     data[0]=5;
        length=data.length; 
        wrapper = new WrappedStreamedBlockPacker(data);
     
        

     int[]  blocktest = wrapper.getUncompressedBlock(0);
        
        for (int i = 0;i<100;i++){
            if (data[i] != blocktest[i]){
                System.out.println("error index:"+i+ " values "+ data[i]+":"+blocktest[i] );
            }
            else{
                System.out.println("OK index:"+i+ " values "+ data[i]+":"+blocktest[i] );
            }
        }
    
        wrapper.logCompressionData();
        
    //    System.out.println(data[12345677] +" : "+wrapper.getValue(12345677));
    
        long start= System.currentTimeMillis();
        for (int i =0;i<10000000;i++){
         int index = (int) (1d*Math.random()*length);
            wrapper.increaseCachedValue(index);
     
            //Also update org data for comparison:
            data[index]=data[index]+1;
            
            if (i%1000000==0){
                long time = System.currentTimeMillis()-start;
                System.out.println("total updates:"+i +" total time:"+time +" updates/ms:"+1d*i/time);     
            }        
        }
        System.out.println("flushing cache before compare count is correct"); 
        wrapper.flush();
        
        
        int[]  block0 = wrapper.getUncompressedBlock(0);
        
        for (int i = 0;i<3000000;i++){
            if (data[i] != block0[i]){
                System.out.println("error index:"+i+ " values "+ data[i]+":"+block0[i] );
            }
        }
         System.out.println("time:"+(System.currentTimeMillis()-start));
        
    }
    
    
    public static void unsortedExample() {
       int[] data = LongTailIntGenerator.GenerateLongtailDistribution(300000000,500000,100);
       long start=System.currentTimeMillis();               
       int[] compressed = FastPFORCompresserUtil.compress(data);
        System.out.println("compress ratio:"+1d*compressed.length/data.length+" compress time:"+(System.currentTimeMillis()-start));

        start=System.currentTimeMillis();
        int[] data1 =FastPFORCompresserUtil.unCompress(compressed,data.length);
        System.out.println("decompress time:"+(System.currentTimeMillis()-start));
        
        if(Arrays.equals(data,data1)) 
          System.out.println("data is recovered without loss");
        else
          throw new RuntimeException("bug"); // could use assert
        System.out.println();

    }
}
