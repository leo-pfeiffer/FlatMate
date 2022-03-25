package cs5031.groupc.practical3;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@SpringBootApplication
public class Server {

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/")
    public String serverRunning() {
        return "The server is running.";
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api")
    public String apiDescription() {
        ApiDescriber apiD = new ApiDescriber();
        return apiD.describe();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/test")
    public String apitest() {
       return "in api test";
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/test2")
    public String apitest2() {
        return "in api test2";
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/createUser")
    public String blub() {
        ApiDescriber apiD = new ApiDescriber();
        return apiD.describe();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/user")
    public ResponseEntity validateUsername(@RequestParam String user) {
        boolean validate = false;
        //TODO: validation logic
        if (validate){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
    }
}