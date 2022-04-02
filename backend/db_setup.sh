#!/bin/bash

TEST_PATH="src/test/resources/db"
DB_PATH="src/main/resources/db"
DB_NAME="db.sqlite"

# create db file and schema
sqlite3 "$DB_PATH/$DB_NAME" < "$DB_PATH/schema.sql"
sqlite3 "$TEST_PATH/$DB_NAME" < "$DB_PATH/schema.sql"

# insert demo data
sqlite3 "$DB_PATH/$DB_NAME" < "$TEST_PATH/demo_data.sql"
sqlite3 "$TEST_PATH/$DB_NAME" < "$TEST_PATH/demo_data.sql"

