bash ./login.sh -u frodo -p baggins -c
bash ./login.sh -u samwise -p gamgee -c
bash ./login.sh -u meriadoc -p brandybuck -c
bash ./login.sh -u smeagol -p gollum -c
bash ./login.sh -u peregrin -p took -c
bash ./sendRequest.sh -c /api/group/create?groupname=hobbits
bash ./login.sh -u peregrin -p took
bash ./sendRequest.sh -c /api/user/ -g
bash ./sendRequest.sh -c /api/group/add?username=frodo
bash ./sendRequest.sh -c /api/group/add?username=samwise
bash ./sendRequest.sh -c /api/group/add?username=meriadoc
bash ./sendRequest.sh -c /api/group/add?username=smeagol
bash ./sendRequest.sh -c /api/group/remove?username=smeagol
bash ./sendRequest.sh -c /api/group/changeAdmin?username=samwise
bash ./login.sh -u samwise -p gamgee
bash ./sendRequest.sh -c /api/bill/create -j breakfast.json
bash ./sendRequest.sh -c /api/bill/create -j breakfast2.json
bash ./sendRequest.sh -c /api/bill/create -j pipeweed.json
bash ./sendRequest.sh -c "/api/bill/createUserBill?billId=5&username=frodo&percentage=0.25"
bash ./sendRequest.sh -c "/api/bill/createUserBill?billId=5&username=samwise&percentage=0.25"
bash ./sendRequest.sh -c "/api/bill/createUserBill?billId=5&username=meriadoc&percentage=0.25"
bash ./sendRequest.sh -c "/api/bill/createUserBill?billId=5&username=peregrin&percentage=0.25"

