package InputOutput;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileMain {
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_RESET = "\u001B[0m";
    /**
     * writeFile: 實現檔案寫入及try縮寫方法
     * readFile: 實現檔案讀取及try縮寫方法
     * bufferedReaderFile: 利用buffered實現批次讀取效率較高，減少磁碟寫入動作
     * bufferedWriterFile: 利用buffered實現批次寫入效率較高，減少磁碟寫入動作
     * byteStreamUTFFile: 實現UTF寫入與讀取
     * getValueToBinary: 實現將數值轉為二進制String並顯示，以及顯示的String換色
     * serializableTest: 實現將class序列化，將資料寫入txt與txt讀取的方法
     * serialVersionUID是版本號，如果讀取版本號不同，會拋異常InvalidClassException
     * randomAccessFileWriteTest: 實現利用RandomAccessFile，多執行續寫入資料
     * randomAccessFileReadTest: 實現利用RandomAccessFile，多執行續讀取資料
     */
    public static void main(String[] args) {
//        try {
//            writeFile();
//            readFile();
//            bufferedReaderFile();
//            bufferedWriterFile();
//            byteStreamUTFFile();
//            randomAccessFileWriteTest();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        randomAccessFileReadTest();
//        try {
//            serializableTest();
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        getValueToBinary(0xff);
    }

    private static void randomAccessFileReadTest() {
        new RandomAccessFileReadThread(0).start();
        new RandomAccessFileReadThread(1024).start();
        new RandomAccessFileReadThread(2*1024).start();
    }

    private static void randomAccessFileWriteTest() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("locations.txt", "rw");
        randomAccessFile.setLength(1024*1024);
        randomAccessFile.close();
        new RandomAccessFileWriteThread(0, "Test1".getBytes(StandardCharsets.UTF_8)).start();
        new RandomAccessFileWriteThread(1024, "Test2".getBytes(StandardCharsets.UTF_8)).start();
        new RandomAccessFileWriteThread(2*1024, "Test3".getBytes(StandardCharsets.UTF_8)).start();
    }

    private static void serializableTest() throws IOException, ClassNotFoundException {
        //測試serialVersionUID時，把寫入註解
        SerializableItem serializableItem = new SerializableItem(23,"123");
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream("locations.txt"))
        )) {
            objectOutputStream.writeObject(serializableItem);
        }

        SerializableItem serializableItem2;
        try(ObjectInputStream objectInputStream = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream("locations.txt"))
        )) {
            serializableItem2 = (SerializableItem) objectInputStream.readObject();
            System.out.println(serializableItem2.getId() + " : " + serializableItem2.getTag());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getValueToBinary(int value) {
        String binaryString = String.format("%32s", Integer.toBinaryString(value))
                .replace(" ", "0");
        binaryString = binaryString.substring(0, 24) + ANSI_PURPLE +
                binaryString.substring(24) + ANSI_RESET;
        System.out.println(binaryString);
    }

    private static void byteStreamUTFFile() throws IOException {
        try(DataOutputStream dataOutputStream = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream("locations.txt"))
        )) {
            dataOutputStream.writeUTF("123445");
        }
        try(DataInputStream dataInputStream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("locations.txt"))
        )) {
            System.out.println(dataInputStream.readUTF());
        }
    }

    private static void bufferedWriterFile() throws IOException {
        try (BufferedWriter fileWriter = new BufferedWriter(
                new FileWriter("locations.txt"))
        ) {
            fileWriter.write("%234576A");
        }
    }

    private static void bufferedReaderFile() throws IOException {
        try (BufferedReader fileReader = new BufferedReader(
                new FileReader("locations.txt"))
        ) {
            String read;
            do {
                read = fileReader.readLine();
                if (read != null)
                    System.out.print(read);
            } while (read != null);
        }
    }

    private static void readFile() throws IOException {
        try (FileReader fileReader = new FileReader("locations.txt")) {
            int read;
            do {
                read = fileReader.read();
                if (read != -1)
                    System.out.print((char) read);
            } while (read != -1);
        }
    }

    private static void writeFile() throws IOException {
        //Java 7以後提供，縮寫
        try (FileWriter fileWriter = new FileWriter("locations.txt")) {
            fileWriter.write("A12345sA");
        }
        //以下等效以上
//        FileWriter fileWriter = null;
//        try {
//            fileWriter = new FileWriter("locations.txt");
//            fileWriter.write("123445");
//        } finally {
//            if(fileWriter != null)
//                fileWriter.close();
//        }
    }
}
