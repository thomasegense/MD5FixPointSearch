package dk.egense.math.distributions;

import java.math.BigInteger;
import java.util.Random;

public class LongTailIntGenerator {
	//The power parameter should be >= 1
	private static double HEAD_CURVEFACTOR=1d; 
	private static double TAIL_CURVEFACTOR=2d;
	private static long BIJECTIONPRIME1 = 0;      
	private static long BIJECTIONPRIME2 = 0;     


	public static void main(String[] args) {

	    int[] dist =GenerateLongtailDistribution(100,100000,500, 300000000 ,1000);

		for (int i = 0;i <1000;i++){
			System.out.println(dist[i]);
		}		
	}    

	// The totalSize of the array will be rounded up to nearest prime
	public static int[] GenerateLongtailDistribution(int headSize,int headMax,int headMin,int totalSize,long seed){
		int[] distribution;
		BigInteger suggestedSize = new BigInteger(""+(totalSize-1));    	
		totalSize = suggestedSize.nextProbablePrime().intValue();
		BIJECTIONPRIME1 = new BigInteger(""+totalSize/3).nextProbablePrime().longValue();
		BIJECTIONPRIME2 = new BigInteger(""+totalSize/2).nextProbablePrime().longValue();
		
		System.out.println("array size:"+totalSize +" prime1:"+BIJECTIONPRIME1 +" prime1:"+BIJECTIONPRIME2);
		Random random = new Random(seed); 
		distribution =  new int[totalSize];

		//Generate the full dataset directly in the array-object
		//First generate the head.
    	
		for (int i=0;i<headSize;i++){
			distribution[getBijectionMapping(i,distribution.length)]=new Double(random.nextInt(headMax)*Math.pow(random.nextDouble(),HEAD_CURVEFACTOR )).intValue();           
		}

		for (int i=headSize;i<totalSize;i++){
			distribution[getBijectionMapping(i,distribution.length)]=new Double(headMin*Math.pow(random.nextDouble(),TAIL_CURVEFACTOR)+1).intValue(); 
		}

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

}
