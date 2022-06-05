package nightcode.stanovihin.balancer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:instance.properties")
public class InstanceConfig {

    @Value("${host1.address}")
    private String host1Address;

    @Value("${host2.address}")
    private String host2Address;

    @Value("${host3.address}")
    private String host3Address;

    public String getHost1Address() {
        return host1Address;
    }

    public void setHost1Address(String host1Address) {
        this.host1Address = host1Address;
    }

    public String getHost2Address() {
        return host2Address;
    }

    public void setHost2Address(String host2Address) {
        this.host2Address = host2Address;
    }

    public String getHost3Address() {
        return host3Address;
    }

    public void setHost3Address(String host3Address) {
        this.host3Address = host3Address;
    }
}
