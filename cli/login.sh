login() {
  password=$(node ./encrypt.js $2 Zy2GokAWHf5VTA81PKPF)
  if [ ${create} = "true" ]
  then
    curl -i \
    --request POST 'http://localhost:8080/api/user/create' \
    --header "Content-type: application/json" \
    --data  "{\"username\":\"$1\", \"password\":\"${password}\"}"
  fi
  curl -i \
  -c ./SessionID.txt \
  --location --request POST 'http://localhost:8080/login' \
  --form username="$1" \
  --form password="${password}"
}
create="false"
while getopts u:p:c flag
do
    case "${flag}" in
      u) username=${OPTARG};;
      p) password=${OPTARG};;
      c) create="true";;
    esac
done

login "${username}" "${password}"  >>out.txt