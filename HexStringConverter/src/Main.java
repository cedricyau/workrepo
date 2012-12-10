import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        try {
        	if (args[0].equals("string")) {
        		String wordAsString = HexStringConverter.getHexStringConverterInstance().hexToString(args[1]);
        		System.out.println(wordAsString);
        	} else if (args[0].equals("hex")) {
        		String helloWorldInHex = HexStringConverter.getHexStringConverterInstance().stringToHex(args[1]);
        		System.out.println("'HELLO WORLD' in HEX : " + helloWorldInHex);
        	}
//          System.out.println("Reconvert to String : " + HexStringConverter.getHexStringConverterInstance().hexToString(helloWorldInHex));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}