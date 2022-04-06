# CS5031 Pracical 3 - Group C

Refer to the /docs directory for the report, REST API documentation, and Sprint Logs.

## Getting started
The project consists of three parts: backend server (/backend), single page application (/frontend), and command line client (/cli).

You will need to run the database setup script before using the application and running the tests.

From the root directory...

... to start the server, run:
```bash
cd backend

# setup the SQLite schema
sh db_setup.sh

# start the Spring app
gradle bootRun
```

... to start the web client, run:
```bash
cd frontend

# install node dependencies
npm install

# serve the web page
npm run serve
```

... to use the terminal client:
```bash
cd cli
# ... see detailed instructions in cli/README.md
```

The server exposes its REST API on port 8080, while the web client runs on port 3000.

## Demo users

The database setup script (`db_setup.sh`) populates the database with some demo data. 
The following two users are recommended to login to test the app:

| USER        | PASSWORD      | ROLE      | GROUP MEMBER |
| ------      | ------        | ------    | ------       |
| leopold     | pass          | admin     | yes          |   
| lukas       | pass          | user      | yes          |
| anna        | pass          | user      | no           |

All demo users have the same password `pass`.
