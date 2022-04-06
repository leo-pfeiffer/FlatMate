## Getting started
Make sure you have run `db_setup.sh` in the backend. You can then run `demo.sh`. The demo won't work if you have made  a bill after running the setup.

## Making login requests
`login.sh` can be used to log in and add users. The argument -u is the username and -p is the password. For a new user set the -c flag to create and then log in.

## Other requests
`sendRequest.sh` is used for all other requests. The -c flag is followed by the chosen api call. The -j flag is followed by the path of a json file that is to be sent in the body. The -g flag will change the request from a GET to a POST.