nbClients=$1
#size=$2
#. ~/.bash_aliases

for i in `seq 1 $nbClients`
do
   curl -X POST http://localhost:8000/send &>/dev/null
done

