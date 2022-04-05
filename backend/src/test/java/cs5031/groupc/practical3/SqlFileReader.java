package cs5031.groupc.practical3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SqlFileReader {

    public static String readFile(String pathToFile) {
        StringBuilder sb = null;

        File file = new File(pathToFile);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        assert sb != null;
        return sb.toString();
    }
}