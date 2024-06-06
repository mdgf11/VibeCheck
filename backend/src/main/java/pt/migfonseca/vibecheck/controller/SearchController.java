package pt.migfonseca.vibecheck.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.migfonseca.vibecheck.dto.SearchResponseDTO;
import pt.migfonseca.vibecheck.service.SearchService;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    SearchService service;

    @CrossOrigin(origins = "*")
    @GetMapping
    ResponseEntity<List<SearchResponseDTO>> search(@RequestParam("query") String query) {
        
        return new ResponseEntity<List<SearchResponseDTO>>(service.searchQuery(query), HttpStatus.OK);
    }

}
