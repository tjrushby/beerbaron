import com.sun.xml.internal.bind.api.impl.NameConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BeerBaron {
    public static void main(String[] args) {
        Path filePath = Paths.get("products.txt");

        try(BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line = null;

            while((line = br.readLine()) != null) {
                ProductParser p = new ProductParser(line);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
