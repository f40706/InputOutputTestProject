package InputOutput;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public final class RandomAccessFileReadThread extends Thread {
    private final int skip;

    public RandomAccessFileReadThread(int skip) {
        this.skip = skip;
    }

    @Override
    public void run() {
        try {
            readFile(skip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFile(int skip) throws IOException {
        byte bytes[] = new byte[1024];
        try(RandomAccessFile randomAccessFile =
                    new RandomAccessFile("locations.txt", "r")) {
            randomAccessFile.seek(skip);
            randomAccessFile.read(bytes);
            System.out.println(new String(bytes, StandardCharsets.UTF_8));
        }
    }
}
