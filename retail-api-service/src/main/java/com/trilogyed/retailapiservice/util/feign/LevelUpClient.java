package com.trilogyed.retailapiservice.util.feign;

import com.trilogyed.retailapiservice.model.LevelUp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "level-up-service")
public interface LevelUpClient {

    @GetMapping(value = "/levelups/{id}")
    LevelUp getLevelup(@PathVariable("id") int id);

    @GetMapping(value = "/levelups/customerId/{customerId}")
    LevelUp getLevelUpByCustomerId(@PathVariable("customerId") int customerId);

}
