send() {

  echo "$1"
  echo "$2"
  echo "$3"
  echo "@$3"

  url=http://localhost:8080
  curl -i \
  -b "./SessionID.txt" \
  --request "$2" \
  --header "Content-type: application/json" \
  --data  "@$3" \
  "${url}$1"
}
type=POST
while getopts c:j:g flag
do
    case "${flag}" in
      c) call=${OPTARG};;
      j) json=${OPTARG};;
      g) type=GET;;
    esac
done

send "${call}" "${type}" "${json}"  >out.txt