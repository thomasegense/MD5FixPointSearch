package dk.egense.math.md5;

import java.security.MessageDigest;

import java.util.Random;


public class MD5FixPointSearch {

    final long startTime = System.currentTimeMillis();
    long iterations = 0;
    static int longestPrefixMatch=5; //to avoid all the spam in the start
    static int longestSuffixMatch=5;

    static MessageDigest md=null;

    public static void main(String[] args) throws Exception {

        int numberOfThreads=7;  
        if (args != null && args.length==1){
            numberOfThreads = Integer.parseInt(args[0]);           
        }
        System.out.println("Starting MD5 fixpoint finder with #threads:"+numberOfThreads);
        for (int i=1;i<=numberOfThreads;i++){           
            //Generate random start values for the threads;
            byte[] start= generateRandomHexString(32);

            MD5Thread md5Thread= new MD5Thread(start);
            Thread t = new Thread (md5Thread);
            t.start();
        }


    }
    private static class MD5Thread  implements Runnable {
        byte[] start;

        public MD5Thread(byte[] startValue){
            System.out.println("Thread started with start value:"+new String(startValue));
            start=startValue;       
        }

        public void run() {
            try{
                final MessageDigest md = MessageDigest.getInstance("MD5");

                byte[] oldMD5 = start;
                byte[] newMD5 = new byte[32];

                while (true){
                    md.update(oldMD5); 
                    byte[] newDigest = md.digest();
                    fastHex(newDigest, newMD5);
                    int matchPrefix = prefixMatch(oldMD5, newMD5)/2;
                    int matchSuffix = suffixMatch(oldMD5, newMD5)/2;

                    if (matchPrefix >= longestPrefixMatch) {
                        System.out.println("prefix:"+matchPrefix + ": " + new String(oldMD5) + " -> " + new String(newMD5));               
                        longestPrefixMatch = matchPrefix;               
                    }
                    if (matchSuffix >= longestSuffixMatch) {
                        System.out.println("suffix: "+matchSuffix  + ": " + new String(oldMD5) + " -> " + new String(newMD5));
                        longestSuffixMatch = matchSuffix;
                    }
                    System.arraycopy(newMD5, 0, oldMD5, 0, newMD5.length);
                }
            }    catch(Exception e){
                e.printStackTrace();
            }
        }
    }



    private final static byte[] HEX = "0123456789abcdef".getBytes();
    private static void fastHex(byte[] digest, byte[] hex) {
        for (int i = 0 ; i < digest.length ; i++) {
            hex[i*2] = HEX[(digest[i] & 0xf0) >> 4];
            hex[i*2+1] = HEX[digest[i] & 0x0f];
        }
    }

    private static int prefixMatch(byte[] oldDigest, byte[] newDigest) {
        for (int i = 0 ; i < oldDigest.length ; i++) {
            if (oldDigest[i] != newDigest[i]) {
                return ((oldDigest[i] & 0xf0) == (newDigest[i] & 0xf0)) ? i*2+1 : i*2;  // Every byte -> 2 hex digits
            }
        }
        return oldDigest.length * 2;
    }

    public static byte[] generateRandomHexString(int length){
        Random randomService = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<length;i++){
            sb.append(Integer.toHexString(randomService.nextInt(16))); //0,1,....a,b,c,d,e,f
        }
        return sb.toString().getBytes();
    }




    //Todo ask Toke
    private static int suffixMatch(byte[] oldDigest, byte[] newDigest) {
        byte[] b1= oldDigest.clone();
        byte[] b2= newDigest.clone();
        reverse(b1);
        reverse(b2);           
        return prefixMatch(b1,b2);
    }

    public static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

}