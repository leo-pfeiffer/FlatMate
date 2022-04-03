login() {
  password=$(node ./encrypt.js $2 Zy2GokAWHf5VTA81PKPF)

  curl -i \
  -c ./SessionID.txt \
  --location --request POST 'http://localhost:8080/login' \
  --form username="$1" \
  --form password="${password}"
}
while getopts u:p: flag
do
    case "${flag}" in
      u) username=${OPTARG};;
      p) password=${OPTARG};;
    esac
done

login "${username}" "${password}"  >out.txt