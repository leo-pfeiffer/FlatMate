package cs5031.groupc.practical3;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

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
}