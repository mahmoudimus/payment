package org.klose.payment.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by klose on 7/1/16.
 */
@RestController
public class HelloWorldResource {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }
}
