package dk.egense.math.distributions;

import java.math.BigInteger;
import java.util.Random;

public class LongTailIntGenerator {	
	private static long BIJECTIONPRIME1 = 0;      
	private static long BIJECTIONPRIME2 = 0;     


	public static void main(String[] args) {
    int[] testBitDistribution= new int[]{150000570,75000002,37500001,18750000,9375000,4687500,2343750,1171875,585938,292969,146484,73242,36621,18311,9155,4578,2289,1144,575};

    
         int[] dist = generateFromBitHistogram(testBitDistribution,1000);
         int[] histogram = getHistogram(dist);
         printHistorgram(histogram);                    
		
      /*
		int[] dist = generateLongtailDistribution(300000000 , 500000,1000);		
		int[] bitsRequiredHistogram = getHistogram(dist);
		printHistorgram(bitsRequiredHistogram);
		*/		
	}    

	// The totalSize of the array will be rounded up to nearest prime
	public static synchronized int[] generateLongtailDistribution(int totalSize, int maxValue, long seed){
		Random random = new Random(seed); 

		//jump distance
		int prime1=totalSize/3+random.nextInt(totalSize/4);
		totalSize = nextPrime(totalSize);
		BIJECTIONPRIME1 = new BigInteger(""+prime1).nextProbablePrime().longValue();		
		BIJECTIONPRIME2 = new BigInteger(""+totalSize/2).nextProbablePrime().longValue();		
		System.out.println("array size:"+totalSize +" prime1:"+BIJECTIONPRIME1 +" prime2:"+BIJECTIONPRIME2);

		//Generate the full dataset directly in the array-object
		//First generate the head.

		//generate histogram
		int maxBit =  bitsRequired(maxValue);
		System.out.println("maxbits:"+maxBit);
		int[] histogram = new int[maxBit];
		int remaining = totalSize;


		for (int i=0;i<histogram.length;i++){
			int count = remaining/2; //TODO configure rate of bit loss
			remaining = remaining-count;		    		    		    
			histogram[i]=count;
		}

		//Just add the remaining to first the bit 0 array.
		histogram[0]=histogram[0]+remaining;
		
		
		
		//printHistorgram(histogram);

		int[] distribution = generateFromBitHistogram(histogram, seed);		 		
		return  distribution;
	}


	public static synchronized int[] generateFromBitHistogram(int[] histogram,long seed){		
		int totalSize=countNumberEntries(histogram);
		Random random = new Random(seed); 
		//jump distance
		int prime1=totalSize/3+random.nextInt(totalSize/4);
		totalSize = nextPrime(totalSize);
		BIJECTIONPRIME1 = new BigInteger(""+prime1).nextProbablePrime().longValue();		
		BIJECTIONPRIME2 = new BigInteger(""+totalSize/2).nextProbablePrime().longValue();		

        					
		int totalSizePrime = nextPrime(totalSize);
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


	//Just a fast 'randomlike' bijective mapping from [0,N] -> [0,N]. N must be a prime
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

	private static int[] getHistogram(int[] maxima) {
		final int[] histogram = new int[32];
		for (int maxValue : maxima) {
			int bitsRequired = bitsRequired(maxValue);
			histogram[bitsRequired == 0 ? 0 : bitsRequired - 1]++;
		}
		return histogram;
	}

	private static int bitsRequired(int maxValue){	     
		return Math.max(1, 32 - Integer.numberOfLeadingZeros(maxValue));	     
	}

	private static int nextPrime(int totalSize){
		BigInteger suggestedSize = new BigInteger(""+(totalSize-1));    	
		int totalSizePrime = suggestedSize.nextProbablePrime().intValue();
		return totalSizePrime; 
	}

	private static void printHistorgram(int[] bitsRequiredHistogram){
		for (int i =0;i<bitsRequiredHistogram.length;i++){
			System.out.println((i+1) +" bits:"+bitsRequiredHistogram[i]);	       
		}
	}

	private static int countNumberEntries(int[] bitsRequiredHistogram){
		int count=0;
		for (int i =0;i<bitsRequiredHistogram.length;i++){
			count+=bitsRequiredHistogram[i];	       
		}
	    return count;
	}

}
