package dk.egense.math.distributions;

import java.util.Arrays;

import me.lemire.integercompression.Composition;
import me.lemire.integercompression.FastPFOR;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.VariableByte;

public class PackingTest {


    public static void main(String[] args){
        unsortedExample();       
    }
 
    public static void unsortedExample() {
        int[] data = LongTailIntGenerator.GenerateLongtailDistribution(300000000,500000,100);
      
        System.out.println("data size:"+data.length);
        int N=data.length;
        
        int[] compressed = new int [N+1024];// could need more
        IntegerCODEC codec =  new 
           Composition(
                    new FastPFOR(),
                    new VariableByte());
        // compressing
        IntWrapper inputoffset = new IntWrapper(0);
        IntWrapper outputoffset = new IntWrapper(0);
        
        long start=System.currentTimeMillis();
        codec.compress(data,inputoffset,data.length,compressed,outputoffset);
        System.out.println("compressed unsorted integers from "+data.length*4/1024+"KB to "+outputoffset.intValue()*4/1024+"KB");
        System.out.println("compress time:"+(System.currentTimeMillis()-start));
        
        // we can repack the data: (optional)
        compressed = Arrays.copyOf(compressed,outputoffset.intValue());

        

        start=System.currentTimeMillis();
        int[] recovered = new int[N];
        IntWrapper recoffset = new IntWrapper(0);
        codec.uncompress(compressed,new IntWrapper(0),compressed.length,recovered,recoffset);
        System.out.println("decompress time:"+(System.currentTimeMillis()-start));
        
        if(Arrays.equals(data,recovered)) 
          System.out.println("data is recovered without loss");
        else
          throw new RuntimeException("bug"); // could use assert
        System.out.println();

    }
}
