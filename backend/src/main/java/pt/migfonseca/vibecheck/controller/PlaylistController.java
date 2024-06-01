package pt.migfonseca.vibecheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.migfonseca.vibecheck.service.PlaylistService;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {

    @Autowired
    PlaylistService service;

    @GetMapping("/test")
    ResponseEntity<String> age(@RequestParam("yearOfBirth") int yearOfBirth) {
        /* HttpHeaders httpHeader =  new HttpHeaders();
        httpHeader.set("MyResponseHeader", "MyValue"); */
        return new ResponseEntity<String>("Your age is " + (2024 - yearOfBirth), HttpStatus.OK);
    }

}
