package nightcode.stanovihin.balancer.controller;

import nightcode.stanovihin.balancer.exception.ArtistNotFoundException;
import nightcode.stanovihin.balancer.model.Vote;
import nightcode.stanovihin.balancer.service.LoadBalancerService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("votes")
public class BalancerController {

    @Autowired
    private LoadBalancerService loadBalancerService;

    @PostMapping
    public ResponseEntity<?> vote(@RequestBody Vote vote){
        int responseCode = loadBalancerService.postVote(vote);
        if (responseCode == 201){
        return ResponseEntity.ok(responseCode);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getVotes(){
        return null;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStatistic(@RequestParam Long from,
                                          @RequestParam Long to,
                                          @RequestParam Long intervals,
                                          @RequestParam String artists){
        System.out.printf("Received request: from=%s, to=%s, interval=%s, artists=%s%n", from, to, intervals, artists);
        String response;
        try {
            response = loadBalancerService.getStatistic(from, to, intervals, artists);
        }catch(ArtistNotFoundException ex){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
