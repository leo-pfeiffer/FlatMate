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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.HashMap;

@RestController
@SpringBootApplication
public class Server {

    private String getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        return currentPrincipalName;
    }

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
    @GetMapping("/test")
    public HashMap<String,Integer[]> test() {
        HashMap<String,Integer[]> test = new HashMap();
        Integer[] t = {1,2,3,4};
        test.put("bills",t);
        return test;
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
    @PostMapping("/createUser")
    public String createUser(
            @RequestBody final UserCreator data
    ) {
        String ret = "";
        try{
           ret = data.getUsername() + " " + data.getPassword();
        } catch(Exception e){}
        return ret;
        /*
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/

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


    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group")
    public void getGroups(
            @RequestParam final String groupname
    ) {
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/

    }


    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/create")
    public String createGroup(
            @RequestParam final String groupname
    ) {
        String ret = "";
        try{
            ret = groupname;
        } catch(Exception e){}
        return ret;
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/add")
    public String addToGroup(
            @RequestParam final String username
    ) {
        String ret = "";
        try{
            ret = username;
        } catch(Exception e){}
        return ret;
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/remove")
    public String removeFromGroup(
            @RequestParam final String username
    ) {
        String ret = "";
        try{
            ret = username;
        } catch(Exception e){}
        return ret;
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/

    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/changeAdmin")
    public String changeGroupAdmin(
            @RequestParam final String username
    ) {
        String ret = "";
        try{
            ret = username;
        } catch(Exception e){}
        return ret;
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getUsers")
    public void getGroupUsers(){
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getAllBills")
    public void getAllGroupBills(){
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getAllLists")
    public void getAllGroupLists(){
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getBill")
    public int getBillByID(
            @RequestParam int id
    ){
        int ret = -1;
        try{
            ret = id;
        } catch(Exception e){}
        return ret;
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getList")
    public int getListByID(
            @RequestParam int id
    ){
        int ret = -1;
        try{
            ret = id;
        } catch(Exception e){}
        return ret;
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/bill/create")
    public void createBill(
            @RequestBody BillCreator data
    ){
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/bill/pay")
    public void payBill(
            @RequestParam int id
    ){
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/list/create")
    public void createList(
            @RequestBody ListCreator data
    ){
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/list/addBill")
    public void addBillToList(
            @RequestBody BillAdder data
    ){
        /*
        TODO: create creation logic
        boolean successful = true;

        if (successful){
            return ResponseEntity.ok().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
*/
    }



}