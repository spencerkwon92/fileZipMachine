package career.projects.fileZipMachine;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Final Project-decompressing the zip301 file.
 * By.Sungjin Kwon, Sungjoon Ha
 *
 * Methods Explaination....
 * buildTree(method): building Huffman Tree.
 * deencoding(method): decoding the text file. It recieve the binarycode and read by character by character. and when it find
 * the value in the HASHMAP, it change it to char(key) value.
 * getKeysByValue(method): getting key value in the HASHMAP using STREAM.
 */

public class DecodeFile {
    private static Map<Character, String> prefixCodeTale = new HashMap<>();
    private static List<String> datasList = new ArrayList<>();
    public static long TOTALTIME  = 0;
    public static long START;
    public static long tmp;

    DecodeFile(String path) throws IOException{

        START = System.currentTimeMillis();

        String OutPutFileName = path.substring(0,path.length()-7);
        FileInputStream FIS = new FileInputStream(new File(path));
        FileOutputStream FOS = new FileOutputStream(OutPutFileName+"2.txt");

        int read=0;
        String codes = "";
        while((read=FIS.read()) !=-1){
            codes += (char)read;

            if(codes.contains("*****")){
                break;
            }
        }

        String[] datas = codes.split("\n");
        int num = datas.length;

        for(int i=0; i<num-1; i++){
            String str;
            String nl;
            if(datas[i].contains("space")&&datas[i].contains(" ")){
                str = datas[i].replaceAll(" ", "|");
                nl = str.replaceAll("space", " ");
                datasList.add(nl);
            }else if(datas[i].contains("newline")&&datas[i].contains(" ")){
                str = datas[i].replaceAll(" ", "|");
                nl = str.replaceAll("newline", "\n");
                datasList.add(nl);
            }else if(datas[i].contains("return")&&datas[i].contains(" ")){
                str = datas[i].replaceAll(" ", "|");
                nl = str.replaceAll("return", "\r");
                datasList.add(nl);
            }else if(datas[i].contains("tab")&&datas[i].contains(" ")){
                str = datas[i].replaceAll(" ", "|");
                nl = str.replaceAll("tab", "\t");
                datasList.add(nl);
            }else{
                str = datas[i].replaceAll(" ", "|");
                datasList.add(str);
            }
        }

        int size = datasList.size();
        for(int i=0; i<size; i++){
            String[] codeSet = datasList.get(i).split("\\|");
            String value = codeSet[0];
            char key = codeSet[1].charAt(0);
            prefixCodeTale.put(key, value);
        }

        List<Integer> rawCodeList = new ArrayList<>();
        List<Integer> codeList = new ArrayList<>();

        String s="";
        while((read = FIS.read()) !=-1){
            int b=read;
            rawCodeList.add(b);
        }

        int RC_size = rawCodeList.size();

        for(int i=1;i<RC_size; i++){
            if(rawCodeList.get(i)==10){
                int startIndex = i+1;
                for(int j=startIndex; j<RC_size;j++){
                    codeList.add(rawCodeList.get(j));
                }
                break;
            }
        }

        String codeBuffer = "";
        for(int i=0; i<codeList.size(); i++){
            String bin = Integer.toBinaryString(codeList.get(i));
            int numZero = 8-bin.length();
            String padding = "0".repeat(numZero);
            bin = padding+bin;
            codeBuffer += bin;
        }

        String sentences =decoding(codeBuffer);

        FOS.write(sentences.getBytes());

        FIS.close();

        tmp = System.currentTimeMillis() - START;
        TOTALTIME  += tmp;

        System.out.println("*** UNZIP301 - OUR TEAM IMPLEMENTATION ***");
        System.out.println("Total Time :" +TOTALTIME);
        System.out.println("Output File Name: "+OutPutFileName.substring(7, OutPutFileName.length())+"2.txt");
        System.out.println("*** All processes are DONE ***");

    }

    public static String decoding(String data){

        START = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        String buffer = "";
        for(char c : data.toCharArray()){
            buffer += c;

            if(prefixCodeTale.containsValue(buffer)){
                Stream<Character> keyStream = getKeysByValue(prefixCodeTale, buffer);
                char key = keyStream.findFirst().get();
                sb.append(key);
                buffer="";
            }
        }

        tmp = System.currentTimeMillis() - START;
        TOTALTIME  += tmp;

        return sb.toString();
    }

    public static <K, V> Stream<K> getKeysByValue(Map<K, V> map, V value) {
        return map.
                entrySet().
                stream().
                filter(entry -> value.equals(entry.getValue())).
                map(Map.Entry::getKey);
    }

    public static void main(String[] argv) throws IOException {
        String path = argv[0];
        new DecodeFile("sample/"+path);
    }
}
