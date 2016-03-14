import java.io.*;
import java.net.*;

public class DatagramInputStream extends InputStream {
    public static final int BUFFER_SIZE = 120000;
    byte[] buffer;
    int index, count;
    DatagramSocket socket;
    DatagramPacket packet;

    public DatagramInputStream(DatagramSocket socket) {
        this.socket = socket;
        buffer = new byte[BUFFER_SIZE];
        packet = new DatagramPacket(buffer, 0);
    }

    public synchronized int read() throws IOException {
        while (index >= count) {
            receive();
        }
        return (int) buffer[index++];
    }

    public synchronized int read(byte[] data, int offset, int length)
            throws IOException {
        if (length <= 0)
            return 0;
        while (index >= count)
            receive();
        if (count - index < length)
            length = count - index;
        System.arraycopy(buffer, index, data, offset, length);
        index += length;
        return length;
    }

    void receive() throws IOException {
        packet.setLength(buffer.length);
        socket.receive(packet);
        index = 0;
        count = packet.getLength();
    }
}