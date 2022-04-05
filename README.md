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
sh ./db_setup.sh

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
TODO

The server exposes its REST API on port 8080, while the web client runs on port 3000.
