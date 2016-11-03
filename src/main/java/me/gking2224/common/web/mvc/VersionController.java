package me.gking2224.common.web.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(VersionController.class);

    @Autowired Environment environment;

    @RequestMapping(value="/version", method=RequestMethod.GET)
    public ResponseEntity<String> version() {
        return new ResponseEntity<String>(environment.getProperty("VERSION"), HttpStatus.OK);
    }
}
