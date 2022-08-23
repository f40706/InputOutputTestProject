package InputOutput;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class RandomAccessFileWriteThread extends Thread {
    private final int skip;
    private final byte[] bytes;

    public RandomAccessFileWriteThread(int skip, byte[] bytes) {
        this.skip = skip;
        this.bytes = bytes;
    }

    @Override
    public void run() {
        try {
            writeFile(skip, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(int skip, byte[] bytes) throws IOException {
        try(RandomAccessFile randomAccessFile =
                    new RandomAccessFile("locations.txt", "rw")) {
            randomAccessFile.seek(skip);
            randomAccessFile.write(bytes);
        }
    }
}
