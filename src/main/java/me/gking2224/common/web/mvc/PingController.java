package me.gking2224.common.web.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(PingController.class);


    @RequestMapping(value="/ping", method=RequestMethod.GET)
    public ResponseEntity<Void> ping() {
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
