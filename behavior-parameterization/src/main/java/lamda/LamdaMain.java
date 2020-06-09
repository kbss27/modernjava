package lamda;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LamdaMain {
    public static void main(String[] args) throws IOException {
        processFile(b -> b.readLine() + b.readLine());
    }

    public static String processFile(BufferedReaderProcessor p) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
            return p.process(br);
        }
    }
}
