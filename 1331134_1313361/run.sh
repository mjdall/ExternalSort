temp_file=$(mktemp)
set -e
if [ "$#" -ne 2 ]; then
    echo "Usage: run.sh <Replacement Selection Heap Size> <Polyphase Merge Sort Temporary Files>"
    exit 1
fi
# Compile
javac *.java
# Run make runs
RUNS=$( java -cp $( pwd ) MakeRuns $1 - $temp_file | grep -o "[0-9]*" )
# echo "MakeRuns produced: $RUNS runs"
# Run polyphase merge
java -cp $( pwd ) PolyMerge $2 $RUNS $temp_file
# Remove temporary file
rm $temp_file