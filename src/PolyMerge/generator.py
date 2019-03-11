import random
import string

num_runs = 10000
for i in range(0, num_runs):
    run = []
    for j in range(0, random.randint(100, 500)):
        record = ""
        for k in range(0, 16):
            record += random.choice(string.ascii_letters)
        run.append(record)

    run.sort()
    for line in run:
        print(line)
    print()