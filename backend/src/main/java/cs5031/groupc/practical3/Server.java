package cs5031.groupc.practical3;

import java.util.HashMap;
import java.util.ArrayList;

import cs5031.groupc.practical3.database.DataAccessObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import cs5031.groupc.practical3.model.*;


@RestController
@SpringBootApplication
public class Server {

    @Autowired
    DataAccessObject dao;

    private String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private Bill privatize(Bill b) {
        b.getOwner().setPassword(null);
        return b;
    }

    private ArrayList<Bill> privatize(ArrayList<Bill> groupBills) {
        for (Bill b : groupBills) {
            privatize(b);
        }
        return groupBills;
    }

    /**
     * A method that confirms that the server is in fact running. --> Works!
     * @return Returns a string confirming the server is running.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/")
    public String serverRunning() {
        return "The server is running.";
    }


    /**
     * A method that decribes the API. --> Works!
     * @return Returns a string that describes the API.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api")
    public String apiDescription() {
        ApiDescriber apiD = new ApiDescriber();
        return apiD.describe();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/test")
    public HashMap<String, ArrayList<User>> test() {
        HashMap<String, ArrayList<User>> test = new HashMap();
        ArrayList<User> t = dao.getAllUsers();
        test.put("users", t);
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


    /**
     * A Enpoint that creates a new User and stores it in the database. --> Works!
     *
     * @param data The necessary data to create a new user, consisting of the username and password.
     * @return returns a 200 OK response if successfull, and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/user/create")
    public ResponseEntity createUser(@RequestBody final UserCreator data) {

        try {
            dao.createUser(data.getUsername(), data.getPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/user/validate")
    public ArrayList<User> validateUsername(@RequestParam String username) {

        try {
            ArrayList<User> users = dao.getAllUsers();
            return users;
        } catch (Exception e) {

        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
    }


    /**
     * A Endpoint that returns a group object from the group name. --> Works!
     *
     * @param groupname The name of the group to be retrieved.
     * @return Returns a group object.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group")
    public Group getGroup(@RequestParam final String groupname) {
        try {
            Group group = dao.getGroup(groupname);
            return group;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
    }


    /**
     * An endpoint that creates a group, add the creating user to the group, and makes the creating user to the admin of the group. --> Works!
     *
     * @param groupname the name of the prospective group.
     * @return returns a 200 OK if successful and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/create")
    public ResponseEntity createGroup(@RequestParam final String groupname) {
        try {
            dao.createGroup(groupname);
            //Group group = dao.getGroup(groupname);
            dao.addUserToGroup(getUser(), groupname);
            dao.setRoleToAdmin(getUser());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );
    }

    /**
     * A endpoint that adds a user by username to the group of the user adding the new user.  --> Works!
     *
     * @param username The username of the user that will be added to the group.
     * @return Returns a 200 OK if successful and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/add")
    public ResponseEntity addToGroup(@RequestParam final String username) {
        try {
            User actingUser = dao.getUser(getUser());
            String groupname = actingUser.getGroup().getName();
            System.out.println(username);
            dao.addUserToGroup(username, groupname);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );

    }

    /**
     * A endpoit that removes a user by username from the group of the user removing the user.  --> Works!
     *
     * @param username the username of the user that is supposed to be removed.
     * @return Returns a 200 OK if successful and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/remove")
    public ResponseEntity removeFromGroup(@RequestParam final String username) {
        try {
            dao.removeUserFromGroup(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );

    }

    /**
     * A endpoint that sets a user' role to admin (by username) and set the current admin's role to user. --> Works!
     *
     * @param username The username of the user soon to be admin
     * @return Returns a 200 OK if successful and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/changeAdmin")
    public ResponseEntity changeGroupAdmin(@RequestParam final String username) {
        try {
            dao.setRoleToAdmin(username);
            dao.setRoleToUser(getUser());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );

    }


    /**
     * A endpoit that returns all names of members in the calling user's group. --> Works!
     *
     * @return Returns a HashMap in the style {"user": [USERS]}
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getUsers")
    public HashMap<String, ArrayList<String>> getGroupUsers() {
        try {
            User actingUser = dao.getUser(getUser());
            Group group = actingUser.getGroup();
            long groupID = group.getGroupId();
            ArrayList<User> users = dao.getAllUsers();
            ArrayList<String> groupUsers = new ArrayList();
            for (User u : users) {
                Group userGroup = u.getGroup();
                if (userGroup == null) {
                    continue;
                }
                if (userGroup.getGroupId().equals(groupID)) {
                    groupUsers.add(u.getUsername());
                }
            }
            HashMap<String, ArrayList<String>> ret = new HashMap();
            ret.put("users", groupUsers);
            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );


    }


    /**
     * A endpoint that returns a JSON with all teh bills in teh calling user's group. --> Works!
     *
     * @return returns a HashMap in the format {"bills":[BILLS]} (will be cast to JSON).
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getAllBills")
    public HashMap<String, ArrayList<Bill>> getAllGroupBills() {
        try {
            User actingUser = dao.getUser(getUser());
            long groupId = actingUser.getGroup().getGroupId();
            ArrayList<Bill> groupBills = privatize(dao.getBillsForGroup(groupId));
            /*//ArrayList<Bill> dataProtectedBills = new ArrayList();
            for (Bill b : groupBills) {
                privatize(b);
            }*/
            HashMap<String, ArrayList<Bill>> ret = new HashMap();
            ret.put("bills", groupBills);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );


    }


    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getAllLists")
    public void getAllGroupLists() {

    }

    /**
     * A method that returns a bill by its id. --> Works!
     * @param id The id of the bill.
     * @return Returns a bill object.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getBill")
    public Bill getBillByID(@RequestParam long id) {
        try {
            Bill bill = dao.getBill(id);
            Bill dpBill = privatize(bill);
            return dpBill;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "entity not found"
        );

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getList")
    public void getListByID(@RequestParam int id) {

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/bill/create")
    public void createBill(@RequestBody BillCreator data) {

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/bill/pay")
    public void payBill(@RequestParam int id) {

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/list/create")
    public void createList(@RequestBody ListCreator data) {

    }


    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/list/addBill")
    public void addBillToList(@RequestBody BillAdder data) {

    }


}