
package accommodation.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Created by uengine on 2018. 11. 21..
 */

@org.springframework.stereotype.Service
@FeignClient(name="deliveryNumber", url="${api.url.delivery}")
public interface DeliveryService {

    @RequestMapping(method= RequestMethod.POST, path="/deliveries", consumes = "application/json")
    public void RequestDelivery(@RequestBody Delivery delivery);
}