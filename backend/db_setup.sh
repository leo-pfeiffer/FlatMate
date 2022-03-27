#!/bin/bash

DB_PATH="src/main/resources/db"
DB_NAME="db.sqlite"

# create db file and schema
sqlite3 "$DB_PATH/$DB_NAME" < "$DB_PATH/schema.sql"

# insert demo data
sqlite3 "$DB_PATH/$DB_NAME" < "$DB_PATH/demo_data.sql"