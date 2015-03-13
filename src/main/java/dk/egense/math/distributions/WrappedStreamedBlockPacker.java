package dk.egense.math.distributions;

import java.util.Arrays;

public class WrappedStreamedBlockPacker {

	public static int BLOCK_SIZE=3000000;    
	public static int BLOCK_FLUSH_SIZE=100000;

	private int[][] blocks =null; //First is index, second is the compressed array
	private int[][] blockCache = null;
	private int[] blockCacheCount = null;

	private int orgSize;
	private int numberOfBlocks;

	public WrappedStreamedBlockPacker(int[] dataOrg){
		orgSize=dataOrg.length;
		numberOfBlocks = (int) Math.ceil(1d*orgSize/BLOCK_SIZE);
		System.out.println("number of blocks:"+numberOfBlocks);
		blocks=new int[numberOfBlocks][];
		blockCache=new int[numberOfBlocks][];
		blockCacheCount = new int[numberOfBlocks];

		//Split data in blocks
		for (int i=0;i<numberOfBlocks;i++){
			blockCache[i]=new int[BLOCK_FLUSH_SIZE];    
			int[] tmp = new int[ BLOCK_SIZE];            
			tmp= Arrays.copyOfRange(dataOrg, i* BLOCK_SIZE, Math.min((i+1)* BLOCK_SIZE,dataOrg.length));                          
			int[] compressed = FastPFORCompresserUtil.compress(tmp); 
			blocks[i]= compressed;           
			System.out.println("size of compressed block"+ i +" ="+blocks[i].length);

		}
	}    

	public int totalCompressedArraySize(){
		int size=0;
		for (int i =0;i<numberOfBlocks;i++){
			size+=blocks[i].length;                     
		}
		return size;
	}

	public int getValue(int entry){
		int blocknumber = entry / BLOCK_SIZE;
		int[] orgBlock = FastPFORCompresserUtil.unCompress(blocks[blocknumber], BLOCK_SIZE); //TODO use correct block size for last block
		return orgBlock[entry % BLOCK_SIZE];       
	}

	public void increaseValue(int entry){
		int blocknumber = entry / BLOCK_SIZE;
		int[] orgBlock = FastPFORCompresserUtil.unCompress(blocks[blocknumber], BLOCK_SIZE);//TODO use correct block size for last block
		int before= orgBlock[entry % BLOCK_SIZE];       
		orgBlock[entry % BLOCK_SIZE]=++before;
		int[] compressed = FastPFORCompresserUtil.compress(orgBlock); 
		blocks[blocknumber]= compressed;              
	}

	public int[] getUncompressedBlock(int blockNumber){
		int[] orgBlock = FastPFORCompresserUtil.unCompress(blocks[ blockNumber], BLOCK_SIZE); //TODO use correct block size for last block
		return orgBlock;
	}

	public void increaseCachedValue(int entry){
		int blocknumber = entry / BLOCK_SIZE;
		int nextCacheEntry =blockCacheCount[blocknumber];
		blockCache[blocknumber][nextCacheEntry]=entry;
		blockCacheCount[blocknumber]= ++nextCacheEntry;
		    System.out.println("entry:"+entry +" for block:"+blocknumber +" block cache entry"+nextCacheEntry);
		//Flush it if cache is full
		if (nextCacheEntry==BLOCK_FLUSH_SIZE){
			flushCacheBlock(blocknumber);
		}      
	}

	private void flushCacheBlock(int blocknumber){
		System.out.println("flushing cache for block:"+blocknumber);
		int[] orgBlock = FastPFORCompresserUtil.unCompress(blocks[blocknumber], BLOCK_SIZE); //TODO use correct block size for last block    

		for (int i=0;i<blockCacheCount[blocknumber];i++){
			int entryToIncrease=blockCache[blocknumber][i] % BLOCK_SIZE;
			orgBlock[ entryToIncrease]=++orgBlock[ entryToIncrease];
		}
		int[] compressed = FastPFORCompresserUtil.compress(orgBlock); 
		blocks[blocknumber]= compressed;

		//reset cache   
		blockCache[blocknumber] = new int[BLOCK_FLUSH_SIZE];   
		blockCacheCount[blocknumber]=0;
	}

	public void flush(){
		System.out.println("flushing cache");
		for (int i=0;i<numberOfBlocks;i++){
			flushCacheBlock(i);                      
		}       
	}


	public int logCompressionData(){
		System.out.println("Size of orginal array:"+orgSize);
		int total =0;
		int compressedSize= totalCompressedArraySize();
		System.out.println("Size of compressed array(added):"+compressedSize + " " + (100d*compressedSize)/orgSize +"%");
		total += compressedSize;

		int cacheSize=BLOCK_FLUSH_SIZE*numberOfBlocks;
		System.out.println("Size of cache array(added):"+cacheSize+ " " + (100d*cacheSize)/orgSize +"%");       
		total += cacheSize;        
		int uncompressedBlockSize = (int) (1d*orgSize)/numberOfBlocks;
		System.out.println("Size of uncompressed block:"+uncompressedBlockSize+ " " + (100d*uncompressedBlockSize)/orgSize +"%");

		total += uncompressedBlockSize;
		System.out.println("Size of cache index holders:"+numberOfBlocks+ " " + (100d*numberOfBlocks)/orgSize +"%");;
		total += numberOfBlocks; //The small cache with entries 

		System.out.println("Total compression rate:"+ 1d*total/orgSize);

		return total;
	}


	
	//Uncompress all blocks and add them sequential to the int[] 
	public int[] getUncompressed(){				
		int[] data = new int[orgSize];
		for (int i=0;i<numberOfBlocks;i++){			
			int[] uncompressedBlock = getUncompressedBlock(i);		
			for (int j=0;j<uncompressedBlock.length;j++){
			      data[i*BLOCK_SIZE+j]=uncompressedBlock[j];			
		}
			//Use faster copy, some error in below code
			//TODO use System.arraycopy(uncompressedBlock,0,data,i*numberOfBlocks  ,(i+1)*numberOfBlocks-1 );
		}
		return data;
	}

	public int getTotalArraySizes(){
		int total = 0;
		total += totalCompressedArraySize(); //Sum of the compressed arrays
		total += BLOCK_FLUSH_SIZE*numberOfBlocks; // The cache 
		total += (int) 1d*orgSize/BLOCK_SIZE; // When flushing, the block is uncompressed.
		total += numberOfBlocks; //The small cache with entries 
		return total;
	}

	public static int getBLOCK_FLUSH_SIZE() {
		return BLOCK_FLUSH_SIZE;
	}

}
