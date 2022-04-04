package cs5031.groupc.practical3;

import java.util.HashMap;
import cs5031.groupc.practical3.database.DataAccessObject;
import cs5031.groupc.practical3.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AbTestController {

    final DataAccessObject dao;

    @Autowired
    public AbTestController(DataAccessObject dao) {
        this.dao = dao;
    }

    /**
     * Endpoint for AB test tracking.
     * @param name Name of the experiment
     * @param variant Variant deployed to the current user
     * @param event Logged event
     * @return 200 if ok, else 500
     * */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/_ab_testing/track")
    public ResponseEntity<HashMap<String, Boolean>> createUser(@RequestParam String name, @RequestParam String variant, @RequestParam String event) {
        try {
            dao.insertExperiment(name, variant, event);
            return ResponseEntity.ok(Result.SUCCESS.getResult());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
