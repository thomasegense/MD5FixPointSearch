package dk.egense.math.distributions;

import java.math.BigInteger;
import java.util.Random;

public class LongTailIntGenerator {
	//The power parameter should be >= 1
	private static long BIJECTIONPRIME1 = 0;      
	private static long BIJECTIONPRIME2 = 0;     


	public static void main(String[] args) {

	    int[] dist =GenerateLongtailDistribution(300000000 , 500000,1000);

		
	    long[] bitsRequiredHistogram = getHistogram(dist);
	   int count=0;
	    for (int i =0;i<bitsRequiredHistogram.length;i++){
	        System.out.println((i+1) +" bits:"+bitsRequiredHistogram[i]);	       
	    count += (int) bitsRequiredHistogram[i];
	    }
	    System.out.println(count);
	    //test count is correct;
	    
	    
	}    

	// The totalSize of the array will be rounded up to nearest prime
	public static int[] GenerateLongtailDistribution(int totalSize, int maxValue, long seed){
	    Random random = new Random(seed); 
	    int[] distribution = new int[totalSize];
  	    
		BigInteger suggestedSize = new BigInteger(""+(totalSize-1));    	
		totalSize = suggestedSize.nextProbablePrime().intValue();
	    
		//jump distance
		int prime1=totalSize/3+random.nextInt(totalSize/4);
		
		BIJECTIONPRIME1 = new BigInteger(""+prime1).nextProbablePrime().longValue();		
		BIJECTIONPRIME2 = new BigInteger(""+totalSize/2).nextProbablePrime().longValue();		
		System.out.println("array size:"+totalSize +" prime1:"+BIJECTIONPRIME1 +" prime2:"+BIJECTIONPRIME2);
		
		//Generate the full dataset directly in the array-object
		//First generate the head.
  
		//generate histogram
		int maxBit = (int) bitsRequired(maxValue);
		System.out.println("maxbits:"+maxBit);
		int[] histogram = new int[maxBit];
		int remaining = totalSize;
		        
		
		for (int i=0;i<histogram.length;i++){
		    int count = remaining/2; //TODO configure rate of bit loss
		    remaining = remaining-count;		    		    		    
		    histogram[i]=count;
		}
		
		  for (int i =0;i<histogram.length;i++){
	            System.out.println((i+1) +" bits:"+histogram[i]);          
	        }
		
		  
				 
		  int index =0;
		  for (int i=0;i<histogram.length;i++){
		      int localMax= (int) Math.pow(2, i);
		      for (int j=0;j<histogram[i];j++){
		          distribution[getBijectionMapping(index++,distribution.length)]=random.nextInt(localMax);
		      }		      		      
		  }
		
		/*
		for (int i=0;i<headSize;i++){
			distribution[getBijectionMapping(i,distribution.length)]=new Double(random.nextInt(headMax)*Math.pow(random.nextDouble(),HEAD_CURVEFACTOR )).intValue();           
		}

		for (int i=headSize;i<totalSize;i++){
			distribution[getBijectionMapping(i,distribution.length)]=new Double(headMin*Math.pow(random.nextDouble(),TAIL_CURVEFACTOR)+1).intValue(); 
		}
*/
		return  distribution;
	}

	
	//Just a fast 'randomlike' bijective mapping from [0,n] -> [0,n]. N must be a prime
	private static int getBijectionMapping(int index, int length){		
		long  tmp=1l*index*BIJECTIONPRIME1+ BIJECTIONPRIME2;								
		long tmp1 = tmp  % length;
		int mapped = (int) tmp1;

		/* sanity check
        if (mapped <0 || mapped >= totalCount.length ){
            System.out.println("not injective value:"+mapped +" for index:"+index);        
        }
        if (totalCount[mapped] != 0){
            System.out.println("not surjective: index="+index);        
        }
		 */        
		return mapped;
	}

	 public static long[] getHistogram(int[] maxima) {
	     final long[] histogram = new long[64];
	     for (int maxValue : maxima) {
	       int bitsRequired = bitsRequired(maxValue);
	       histogram[bitsRequired == 0 ? 0 : bitsRequired - 1]++;
	     }
	     return histogram;
	   }

	 public static int bitsRequired(int maxValue){	     
	    return Math.max(1, 64 - Long.numberOfLeadingZeros(maxValue));	     
	 }
	 
}
