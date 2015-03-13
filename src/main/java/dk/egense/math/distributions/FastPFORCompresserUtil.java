package dk.egense.math.distributions;

import java.util.Arrays;

import me.lemire.integercompression.Composition;
import me.lemire.integercompression.FastPFOR;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.VariableByte;

public class FastPFORCompresserUtil {

	public static int[] compress(int[] data){                

		IntegerCODEC codec =  new 
				Composition(
						new FastPFOR(),
						new VariableByte());
		// compressing

		int[] outBuf = new int[data.length+1024];
		IntWrapper inPos = new IntWrapper();
		IntWrapper outPos = new IntWrapper();
		codec.compress(data, inPos, data.length, outBuf, outPos);
		return Arrays.copyOf(outBuf, outPos.get());               
	}

	public static int[] unCompress(int[] data, int len){                        
		IntegerCODEC codec =  new 
				Composition(
						new FastPFOR(),
						new VariableByte());                        

		int[] outBuf = new int[len + 1024];
		IntWrapper inPos = new IntWrapper();
		IntWrapper outPos = new IntWrapper();
		codec.uncompress(data, inPos, data.length, outBuf, outPos);
		return Arrays.copyOf(outBuf, outPos.get());
	}
}
