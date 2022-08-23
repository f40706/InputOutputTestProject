package InputOutput;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FileNioMain {
    /**
     * io使用阻塞式、nio是非阻塞式
     * 速度: 阻塞io > 非阻塞io
     * 非阻塞式好處，可以在處理中做其他事情，提升整體效率
     * NIO能在緩衝區讀取和快速尋訪
     * NIO是一塊一塊處理，不是一個字符或字節
     * 大多情況下大型應用，還是適合IO，字符字節讀取
     * NIO除非是大型應用程式，且多執行續同時讀取寫入，會很合適
     * 因為使用IO會把全部堵塞，否則IO就足以應用大部分應用了
     * ------------
     * fileNioToIo: 實現NIO轉IO的用法，根據需求，若使用NIO時，可以中途轉
     * fileNIO: 實現純NIO用法，批次讀取，不太適合大數據，內存會不夠
     * byteBufferTest: 實現IO轉NIO用法，設定緩衝區大小，再將資料讀/寫緩衝區
     */

    public static void main(String[] args) throws IOException {
//        fileNioToIo();
//        fileNIO();
        byteBufferTest();
    }

    private static void byteBufferTest() throws IOException {
        //由IO經由Channel轉為NIO
        //與fileNioToIo相反
        try(FileOutputStream fileOutputStream = new FileOutputStream("bytebuffer.txt");
            FileChannel fileChannel = fileOutputStream.getChannel()) {
            String output = "data123456789";
            //wrap不需要將位置重置
            ByteBuffer byteBuffer = ByteBuffer.wrap(output.getBytes());
            int numberBytes = fileChannel.write(byteBuffer);
            System.out.println("read number: " + numberBytes);

            //設定ByteBuffer最大寫入大小
            //寫入大小超過的話，會拋出BufferOverflowException
            ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
            intBuffer.putInt(2542);
//            intBuffer.putInt(2542);//此處打開會拋出BufferOverflowException，因為緩衝區寫滿
            //put需要將位置重置，
            intBuffer.flip();
            numberBytes = fileChannel.write(intBuffer);
            System.out.println("read number: " + numberBytes);

            //若需要要在寫入時，因為Write所以也要重置位置
            intBuffer.flip();
            intBuffer.putInt(-98765);
            //put也要重置位置
            intBuffer.flip();
            numberBytes = fileChannel.write(intBuffer);
            System.out.println("read number: " + numberBytes);


            System.out.println("------");
            nioReadMethod();
//            ioReadMethod(output);
        }
    }

    private static void nioReadMethod() throws IOException {
        //此方法try不需要close，否則記得close
        try(RandomAccessFile randomAccessFile = new RandomAccessFile("bytebuffer.txt", "rw");
            FileChannel fileChannel = randomAccessFile.getChannel()) {
            ByteBuffer bytes = ByteBuffer.allocate(13);
            //如果有重複只用ByteBuffer，記得flip，此處不需要
            fileChannel.read(bytes);
            //字串讀取方式
            if(bytes.hasArray()) {
                System.out.println(new String(bytes.array()));
            }

            //Int讀取方式
            //以下測試時，使用絕對位置時，不可開啟相對位置，反之一樣
            //可以測看看差異，讀到的結果是一樣的

            //絕對讀取，
            //getInt時，直接指定緩衝區index，所以不需要flip重置位置
            //絕對讀取 example 1
            /*ByteBuffer intByteBuffer = ByteBuffer.allocate(Integer.BYTES*2);
            fileChannel.read(intByteBuffer);
            System.out.println(intByteBuffer.getInt(Integer.BYTES));
            System.out.println(intByteBuffer.getInt(0));*/

            //絕對讀取 example 2
            //read需要flip!
            ByteBuffer intByteBuffer = ByteBuffer.allocate(Integer.BYTES);
            fileChannel.read(intByteBuffer);
            System.out.println(intByteBuffer.getInt(0));
            intByteBuffer.flip();
            fileChannel.read(intByteBuffer);
            System.out.println(intByteBuffer.getInt(0));

            //相對讀取，需要將緩衝區重置，才能用相對讀取到
            /*ByteBuffer intByteBuffer2 = ByteBuffer.allocate(Integer.BYTES);
            fileChannel.read(intByteBuffer2);
            intByteBuffer2.flip();
            System.out.println(intByteBuffer2.getInt());
            intByteBuffer2.flip();
            fileChannel.read(intByteBuffer2);
            intByteBuffer2.flip();
            System.out.println(intByteBuffer2.getInt());*/
        }
    }

    private static void ioReadMethod(String output) throws IOException {
        try(RandomAccessFile randomAccessFile = new RandomAccessFile("bytebuffer.txt", "rw")) {
            byte[] bytes = new byte[output.length()];
            randomAccessFile.read(bytes);
            System.out.println(new String(bytes));

            int int1 = randomAccessFile.readInt();
            int int2 = randomAccessFile.readInt();
            System.out.println(int1);
            System.out.println(int2);
        }

    }

    private static void fileNIO() throws IOException {
        Path path = FileSystems.getDefault().getPath("data_nio.txt");
        if(!Files.exists(path))
            Files.createFile(path);
        //Java8
//            Files.write(path, "line1".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        //Java11
        Files.writeString(path, "\nline1", StandardOpenOption.APPEND);
        List<String> list = Files.readAllLines(path);
        for(String content: list) {
            System.out.println(content);
        }
    }

    private static void fileNioToIo() throws IOException {
        //使用NIO需要用Path，Java7以後提供，是NIO包裝IO
        //以高效的方式寫入/讀取資料
        Path path = FileSystems.getDefault().getPath("locations_nio.txt");
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            bufferedWriter.write("OoO_|OoO");
        }
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            readTxt(bufferedReader);
        }
    }

    private static void readTxt(BufferedReader bufferedReader) throws IOException {
        boolean eof = false;
        while (!eof) {
            try {
                String buffer;
                if ((buffer = bufferedReader.readLine()) != null) {
                    System.out.println(buffer);
                } else break;
            } catch (EOFException e) {
                eof = true;
            }
        }
    }
}
