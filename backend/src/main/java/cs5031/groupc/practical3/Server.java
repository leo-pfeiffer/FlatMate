package cs5031.groupc.practical3;

import java.util.ArrayList;
import java.util.HashMap;
import cs5031.groupc.practical3.database.DataAccessObject;
import cs5031.groupc.practical3.model.Bill;
import cs5031.groupc.practical3.model.DataProtection;
import cs5031.groupc.practical3.model.Group;
import cs5031.groupc.practical3.model.List;
import cs5031.groupc.practical3.model.ListItem;
import cs5031.groupc.practical3.model.User;
import cs5031.groupc.practical3.model.UserBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@SpringBootApplication
public class Server {

    final DataAccessObject dao;

    @Autowired
    public Server(DataAccessObject dao) {
        this.dao = dao;
    }

    private String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private Bill protect(Bill b) {
        b.protect();
        return b;
    }

    private User protect(User u) {
        u.protect();
        return u;
    }

    private <T> ArrayList<T> protect(ArrayList<T> group) {
        for (T t : group) {
            DataProtection dp = (DataProtection) t;
            dp.protect();
        }
        return group;
    }


    private List protect(List l) {
        l.getOwner().setPassword(null);
        Bill b = l.getBill();
        if (b != null) {
            b.protect();
        }
        return l;
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
     * Get the user object for the currently logged-in user. --> Works!
     * @return User object
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/user")
    public User getCurrentUser() {
        try {
            return protect(dao.getUser(getUser()));
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns true if the username exists, else false. This can be used, for example, for searches.
     * @return Boolean
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/user/exists")
    public boolean getUserExists(@RequestParam String username) {
        try {
            User user = dao.getUser(username);
            return user != null;
        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that creates a new User and stores it in the database. --> Works!
     * @param user The necessary data to create a new user, consisting of the username and password.
     * @return returns a 200 OK response if successful, and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/user/create")
    public ResponseEntity<Void> createUser(@RequestBody final User user) {
        try {
            dao.createUser(user.getUsername(), user.getPassword());
            return ResponseEntity.ok().build();
        } catch (UncategorizedSQLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exists");
        }catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // todo do we still need this?
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/user/validate")
    public ArrayList<User> validateUsername(@RequestParam String username) {
        try {
            return dao.getAllUsers();
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that returns a group object from the group name. --> Works!
     * @param groupname The name of the group to be retrieved.
     * @return Returns a group object.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group")
    public Group getGroup(@RequestParam final String groupname) {
        try {
            return dao.getGroup(groupname);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that creates a group, add the creating user to the group, and makes the creating user
     * to the admin of the group. --> Works!
     * @param groupname the name of the prospective group.
     * @return returns a 200 OK if successful and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/create")
    public ResponseEntity<Void> createGroup(@RequestParam final String groupname) {
        try {
            dao.createGroup(groupname);
            dao.addUserToGroup(getUser(), groupname);
            dao.setRoleToAdmin(getUser());
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            // group or user not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (UncategorizedSQLException e) {
            // group name already exists
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group name already exists.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that adds a user by username to the group of the user adding the new user.  --> Works!
     * @param username The username of the user that will be added to the group.
     * @return Returns a 200 OK if successful and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/add")
    public ResponseEntity<Void> addToGroup(@RequestParam final String username) {
        try {
            User actingUser = dao.getUser(getUser());
            String groupname = actingUser.getGroup().getName();
            System.out.println(username);

            User userToAdd = dao.getUser(username);

            // if user with 'username' is already in a group, user must leave group first
            if (userToAdd.getGroup() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User " + username + " must leave their current group first.");
            }
            dao.addUserToGroup(username, groupname);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * An endpoint that removes a user by username from the group of the user removing the user.  --> Works!
     * @param username the username of the user that is supposed to be removed.
     * @return Returns a 200 OK if successful and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/remove")
    public ResponseEntity<Void> removeFromGroup(@RequestParam final String username) {
        try {
            dao.removeUserFromGroup(username);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Remove the current user from their group.
     * @return Returns a 200 OK if successful and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/removeCurrent")
    public ResponseEntity<Void> removeCurrentUserFromGroup() {
        try {
            dao.removeUserFromGroup(getUser());
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that sets a user' role to admin (by username) and set the
     * role of the current admin to user. --> Works!
     * @param username The username of the user soon to be admin
     * @return Returns a 200 OK if successful and a 404 NOT FOUND if unsuccessful.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/group/changeAdmin")
    public ResponseEntity<Void> changeGroupAdmin(@RequestParam final String username) {
        try {
            dao.setRoleToAdmin(username);
            dao.setRoleToUser(getUser());
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * An endpoint that returns all names of members in the calling user's group. --> Works!
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
            ArrayList<String> groupUsers = new ArrayList<>();
            for (User u : users) {
                Group userGroup = u.getGroup();
                if (userGroup == null) {
                    continue;
                }
                if (userGroup.getGroupId().equals(groupID)) {
                    groupUsers.add(u.getUsername());
                }
            }
            HashMap<String, ArrayList<String>> ret = new HashMap<>();
            ret.put("users", groupUsers);
            return ret;

        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);


    }

    /**
     * An endpoint that returns a JSON with all teh bills in teh calling user's group. --> Works!
     * @return returns a HashMap in the format {"bills":[BILLS]} (will be cast to JSON).
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getAllBills")
    public HashMap<String, ArrayList<Bill>> getAllGroupBills() {
        try {
            User actingUser = dao.getUser(getUser());
            long groupId = actingUser.getGroup().getGroupId();
            ArrayList<Bill> groupBills = protect(dao.getBillsForGroup(groupId));
            HashMap<String, ArrayList<Bill>> ret = new HashMap<>();
            ret.put("bills", groupBills);
            return ret;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that returns an array list with all user bill objects of the group.
     * @return returns an ArrayList in the format (will be cast to JSON).
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getAllUserBills")
    public ArrayList<UserBill> getUserBillsForGroup() {
        try {
            User actingUser = dao.getUser(getUser());
            long groupId = actingUser.getGroup().getGroupId();
            ArrayList<UserBill> groupUserBills = new ArrayList<>();
            ArrayList<Bill> groupBills = protect(dao.getBillsForGroup(groupId));
            for (Bill b : groupBills) {
                ArrayList<UserBill> userBills = protect(dao.getUserBillsForBill(b.getBillId()));
                groupUserBills.addAll(userBills);
            }
            return groupUserBills;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * An endpoint that returns all lists for a group. --> Works!
     * @return returns an ArrayList of all lists.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getAllLists")
    public HashMap<String, ArrayList<List>> getAllGroupLists() {
        try {
            User actingUser = dao.getUser(getUser());
            long groupId = actingUser.getGroup().getGroupId();
            ArrayList<List> groupLists = protect(dao.getListsForGroup(groupId));
            HashMap<String, ArrayList<List>> ret = new HashMap<>();
            ret.put("lists", groupLists);
            return ret;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that returns all list items for a group. --> Works!
     * @return returns an ArrayList of all list items.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getAllListItems")
    public ArrayList<ListItem> getAllGroupListItems() {
        try {
            User actingUser = dao.getUser(getUser());
            long groupId = actingUser.getGroup().getGroupId();
            ArrayList<ListItem> groupListItems = new ArrayList<>();
            ArrayList<List> groupLists = dao.getListsForGroup(groupId);

            for (List l : groupLists) {
                ArrayList<ListItem> listItems = protect(dao.getListItemsForList(l.getListId()));
                groupListItems.addAll(listItems);
            }
            return groupListItems;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
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
            return protect(bill);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * A method that returns a list by its ID. --> Works!
     * @param id The id of the list.
     * @return The list.
     */

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/api/group/getList")
    public List getListByID(@RequestParam long id) {
        try {
            List list = dao.getList(id);
            return protect(list);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that creates a Bill --> Works!
     * @param bill The bill name, description, amount, and payment method.
     * @return The created bill object
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/bill/create")
    public Bill createBill(@RequestBody Bill bill, @RequestParam(required = false) Long listId) {
        try {
            System.out.println(listId);
            bill.setOwner(dao.getUser(getUser()));
            long time = System.currentTimeMillis() / 1000L;
            bill.setCreateTime(time);
            Bill createdBill = dao.createBillAndReturnId(bill);
            createdBill.protect();
            if (listId != null) {
                dao.addBillToList(listId, bill.getBillId());
            }
            return createdBill;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * An endpoint that lets you pay your bill. --> Works!
     * @param billId The id of the userBill object to be paid.
     * @return Returns 200 OK if successful and 404 NOT FOUND if not.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/bill/pay")
    public ResponseEntity<Void> payBill(@RequestParam long billId) {
        try {
            ArrayList<UserBill> userBills = dao.getUserBillsForUser(getUser());
            for (UserBill ub : userBills) {
                if (ub.getBill().getBillId().equals(billId)) {
                    dao.setUserBillToPaid(ub.getUserBillId(), getUser());
                }
            }
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

    }

    /**
     * An endpoint that creates a new list --> Works!
     * @param list A Data object containing the name, description and billID
     * @return The created list item
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/list/create")
    public List createList(@RequestBody List list) {
        try {
            list.setOwner(dao.getUser(getUser()));
            list.setBill(null);
            long time = System.currentTimeMillis() / 1000L;
            list.setCreateTime(time);
            List created = dao.createListAndReturnId(list);
            created.protect();
            return created;
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that creates a new list item
     * @param listItem List Item to create
     * @return 200 OK if successful or 404 NOT FOUND if not.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/list/createItem")
    public ResponseEntity<Void> createListItem(@RequestBody ListItem listItem) {
        try {
            dao.createListItem(listItem);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * An endpoint that creates a new list item
     * @return 200 OK if successful or 404 NOT FOUND if not.
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/api/bill/createUserBill")
    public ResponseEntity<Void> createUserBill(@RequestParam long billId, @RequestParam String username, @RequestParam double percentage) {
        try {
            UserBill userBill = new UserBill();
            userBill.setUser(dao.getUser(username));
            userBill.setBill(dao.getBill(billId));
            userBill.setPercentage(percentage);

            dao.createUserBill(userBill);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}