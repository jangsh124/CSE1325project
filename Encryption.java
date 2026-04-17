import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class Encryption {

    public static String hashPin(String pin) {
        try {
            //creates instance of SHA256 hashing algorithim
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); 

            //uses algoritihim to hash the pin into a 32 byte array
            byte[] hashed = digest.digest(pin.getBytes(StandardCharsets.UTF_8)); 

            return Base64.getEncoder().encodeToString(hashed); //encopdes the byte array into a string and returns it
        } catch (Exception e) {
            throw new RuntimeException("Could not hash PIN", e);
        }
    }
}
