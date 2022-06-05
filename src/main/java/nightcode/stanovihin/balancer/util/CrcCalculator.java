package nightcode.stanovihin.balancer.util;

import java.nio.charset.StandardCharsets;

public class CrcCalculator {

    public static int getCrc16(String phone){
        byte[] phoneBytes = phone.getBytes(StandardCharsets.UTF_8);
        int i;
        int crc_value = 0;
        for (byte phoneByte : phoneBytes) {
            for (i = 0x80; i != 0; i >>= 1) {
                if ((crc_value & 0x8000) != 0) {
                    crc_value = (crc_value << 1) ^ 0x8005;
                } else {
                    crc_value = crc_value << 1;
                }
                if ((phoneByte & i) != 0) {
                    crc_value ^= 0x8005;
                }
            }
        }
        return crc_value > 0 ? crc_value : crc_value * -1;
    }
}
