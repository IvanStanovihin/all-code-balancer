package nightcode.stanovihin.balancer.service;

import com.google.gson.Gson;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import nightcode.stanovihin.balancer.config.InstanceConfig;
import nightcode.stanovihin.balancer.exception.ArtistNotFoundException;
import nightcode.stanovihin.balancer.model.*;
import nightcode.stanovihin.balancer.util.AddressInitializer;
import nightcode.stanovihin.balancer.util.CrcCalculator;
import org.apache.commons.codec.StringEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Service
public class LoadBalancerService {

    @Autowired
    private InstanceConfig instanceConfig;
    private Map<Integer, String> instancesAddresses = new HashMap<>();
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    @PostConstruct
    private void initInstances() {
//        instancesAddresses.put(1, instanceConfig.getHost1Address());
//        instancesAddresses.put(2, instanceConfig.getHost2Address());
//        instancesAddresses.put(3, instanceConfig.getHost3Address());
        AddressInitializer addressInitializer = new AddressInitializer();
        Set<String>addresses = addressInitializer.initAddress();
        int countAddresses = 0;
        for (String address : addresses){
            instancesAddresses.put(countAddresses++, address);
        }
        System.out.println("Instances addresses: " + instancesAddresses);
    }



    public int postVote(Vote vote) {
        String result = null;
        HttpPost postRequest = null;
        try {
            String instanceAddress = getAddressForRequest(vote.getPhone());
            postRequest = new HttpPost("http://" + instanceAddress + "/votes");
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            Gson gson = new Gson();
            String jsonBody = gson.toJson(vote);
            StringEntity stringEntity = new StringEntity(jsonBody);
            postRequest.setEntity(stringEntity);
            postRequest.addHeader("content-type", "application/json");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try (CloseableHttpResponse httpResponse = httpClient.execute(postRequest)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            System.out.println("Status code: " + statusCode);
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            System.out.println("Result: " + result);
            return statusCode;
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return -1;
    }


    public String getStatistic(Long from, Long to, Long intervals, String artists) throws ArtistNotFoundException{
        Gson gson = new Gson();

        Map<String, StatisticResponse>instancesResponses = new HashMap<>();
        ArrayList<IntervalStatistic>intervalStatistic = new ArrayList<>();
        for(Map.Entry<Integer, String>address : instancesAddresses.entrySet()){
            String instanceAddress = address.getValue();
            System.out.println("Sending request to instance: " + instanceAddress);
            StatisticRequest request = new StatisticRequest(from ,to, intervals, artists);
            String instanceResponse = getStatisticFromInstance(instanceAddress, request);
            StatisticResponse statisticResponse = gson.fromJson(instanceResponse, StatisticResponse.class);
            if (statisticResponse != null) {
                System.out.println("Statistic response: " + statisticResponse);
                instancesResponses.put(instanceAddress, statisticResponse);
                intervalStatistic.addAll(Arrays.asList(statisticResponse.getData()));
            }
        }
        System.out.println("INTERVAL STATISTIC: " + intervalStatistic);

        //ключ - номер интервала
        Map<Long, IntervalStatistic>allStat = new HashMap<>();
        Long intervalRange = (to - from) / intervals;
        Long leftBorder = from;
        Long rightBorder = from + intervalRange;
        for (int i = 0; i < intervals; i++){
            Long intervalVotes = 0L;
            for (IntervalStatistic intervalStat : intervalStatistic){
                Long statisticVotes = intervalStat.getVotes();
                Long intervalStatFrom = intervalStat.getStart();
                Long intervalStatTo = intervalStat.getEnd();
                if (leftBorder <= intervalStatFrom && intervalStatTo <= rightBorder){
                    intervalVotes += statisticVotes;
                }
            }
            System.out.println("Interval= " + i + " count votes= " + intervalVotes);
            IntervalStatistic finalIntervalStatistic = new IntervalStatistic(leftBorder, rightBorder, intervalVotes);
            allStat.put((long)i, finalIntervalStatistic);
            leftBorder = rightBorder;
            rightBorder = rightBorder + intervalRange;
        }
        System.out.println("Result statistic: " + allStat);
        StatisticResponse response = new StatisticResponse(allStat.values().toArray(new IntervalStatistic[0]));
        return gson.toJson(response);
    }

    private String getStatisticFromInstance(String instanceAddress, StatisticRequest statisticRequest) throws ArtistNotFoundException{
        String result = null;
        HttpGet getRequest = null;
        String paramsUrl = statisticRequest.getPostUrl();
        String requestUrl = "http://" + instanceAddress + "/votes/stats" + paramsUrl;
        System.out.println("Request url: " + requestUrl);
        getRequest = new HttpGet(requestUrl);
        getRequest.addHeader("content-type", "application/json");

        try (CloseableHttpResponse httpResponse = httpClient.execute(getRequest)) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 404){
                throw new ArtistNotFoundException();
            }
            System.out.println("Status code: " + statusCode);
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
            System.out.println("Result: " + result);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return result;
    }


    private String getAddressForRequest(String phone) {
        int phoneCrc = CrcCalculator.getCrc16(phone);
        System.out.printf("Fro phone: %s, CRC number: %s%n", phone, phoneCrc);
        int instanceNumber = phoneCrc % AddressInitializer.countInstance;
        System.out.printf("Instance number: %s%n", instanceNumber);
        String instanceAddress = instancesAddresses.get(instanceNumber + 1);
        System.out.println("Instance address: " + instanceAddress);
        return instanceAddress;
    }
}
