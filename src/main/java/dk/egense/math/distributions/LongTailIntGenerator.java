package dk.egense.math.distributions;

import java.math.BigInteger;
import java.util.Random;

public class LongTailIntGenerator {

	private static long BIJECTIONPRIME1 = 0;      
	private static long BIJECTIONPRIME2 = 0;     


	public static void main(String[] args) {

		int[] dist = GenerateLongtailDistribution(300000000 , 500000,1000);


		int[] bitsRequiredHistogram = getHistogram(dist);
		int count=0;
		for (int i =0;i<bitsRequiredHistogram.length;i++){
			System.out.println((i+1) +" bits:"+bitsRequiredHistogram[i]);          
			count += (int) bitsRequiredHistogram[i];
		}
		System.out.println(count);



	}    

	// The totalSize of the array will be rounded up to nearest prime
	public static int[] GenerateLongtailDistribution(int totalSize, int maxValue, long seed){

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
        histogram[0]=histogram[0]+remaining;
		
		int[] distribution = generateFromBitHistogram(histogram, seed);		 		
		
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

	public static synchronized int[] generateFromBitHistogram(int[] histogram,long seed){
		int totalSize=0;
		for (int i=0;i<histogram.length;i++){
			totalSize+=histogram[i];
		}				
		int totalSizePrime = nextPrime(totalSize);
		System.out.println("totalSize:"+totalSize);

        Random random = new Random(seed);

        BigInteger suggestedSize = new BigInteger(""+(totalSizePrime-1));
        totalSizePrime = suggestedSize.nextProbablePrime().intValue();

        //jump distance
        int prime1=totalSizePrime/3+random.nextInt(totalSizePrime/4);

        BIJECTIONPRIME1 = new BigInteger(""+prime1).nextProbablePrime().longValue();
        BIJECTIONPRIME2 = new BigInteger(""+totalSizePrime/2).nextProbablePrime().longValue();
        System.out.println("array size:"+totalSizePrime +" prime1:"+BIJECTIONPRIME1 +" prime2:"+BIJECTIONPRIME2);

		return generateFromBitHistogramPrimeSize(histogram, totalSizePrime, seed);
	}

	//The totalsize must be prime.
	private static int[] generateFromBitHistogramPrimeSize(int[] histogram,int totalSizePrime,long seed){

		int[] distribution = new int[totalSizePrime];
		Random random = new Random(seed);
		int index =0;
		for (int i=0;i<histogram.length;i++){
			int localMax= ((int) Math.pow(2, i+1))/2;
			for (int j=0;j<histogram[i];j++){
				distribution[getBijectionMapping(index++,distribution.length)]=localMax +random.nextInt(Math.max(1, localMax));
			}		      		      
		}
		return distribution;	
	}

	private static int nextPrime(int totalSize){
		BigInteger suggestedSize = new BigInteger(""+(totalSize-1));    	
		int totalSizePrime = suggestedSize.nextProbablePrime().intValue();
		return totalSizePrime; 
	}

	private static void printHistogram(int[] bitsRequiredHistogram){
		int count=0;
		for (int i =0;i<bitsRequiredHistogram.length;i++){
			System.out.println((i+1) +" bits:"+bitsRequiredHistogram[i]);	       
			count += (int) bitsRequiredHistogram[i];
		}
		System.out.println(count);
	}

	
	public static int[] getHistogram(int[] maxima) {
		final int[] histogram = new int[32];
		for (int maxValue : maxima) {
			int bitsRequired = bitsRequired(maxValue);
			histogram[bitsRequired == 0 ? 0 : bitsRequired - 1]++;
		}
		return histogram;
	}

	public static int bitsRequired(int maxValue){       
		return Math.max(1, 32 - Integer.numberOfLeadingZeros(maxValue));        
	}

}
