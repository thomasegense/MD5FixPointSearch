package dk.egense.math.distributions;

import java.util.Arrays;

import me.lemire.integercompression.Composition;
import me.lemire.integercompression.FastPFOR;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.VariableByte;

public class WrappedStreamedBlockPacker {

    public static int BLOCK_SIZE=3000000;    
    int[][] blocks =null; //First is index, second is the compressed array
    
    
    
    
    
    public WrappedStreamedBlockPacker(int[] dataOrg){
        int numberOfBlocks=dataOrg.length/BLOCK_SIZE;
        System.out.println("blocks:"+numberOfBlocks);
        for (int i=0;i<numberOfBlocks;i++){            
            int[] compressed = new int [BLOCK_SIZE+64];// could need more
            IntegerCODEC codec =  new  Composition(new FastPFOR(),new VariableByte());
                 // compressing
                 IntWrapper inputoffset = new IntWrapper(0);
                 IntWrapper outputoffset = new IntWrapper(0);                 
                 long start=System.currentTimeMillis();
                 int[] copyOfRange = Arrays.copyOfRange(dataOrg, i*BLOCK_SIZE,  i*BLOCK_SIZE+BLOCK_SIZE );
                 
                 codec.compress(copyOfRange,inputoffset,copyOfRange.length,compressed,outputoffset);
        }
        
        
    }
    
    
    
}
