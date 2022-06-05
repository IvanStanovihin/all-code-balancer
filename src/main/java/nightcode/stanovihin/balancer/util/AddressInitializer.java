package nightcode.stanovihin.balancer.util;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AddressInitializer {

    public static int countInstance;

    public Set<String> initAddress() {
        InputStream is = getClass().getResourceAsStream("/instance.properties");
        Properties props = new Properties();
        try {
            props.load(is);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        Set<String>instanceAddresses = new HashSet<>();
        for (Object key : props.keySet()){
            String address = props.getProperty((String)key);
            instanceAddresses.add(address);
            System.out.println("Addresses: " + address);
        }
        countInstance = instanceAddresses.size();
        System.out.println("Count instances: " + countInstance);
        return instanceAddresses;
    }
}
