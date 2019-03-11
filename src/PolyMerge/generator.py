import random

num_runs = 10000
for i in range(0, num_runs):
    string = ""
    for j in range(0, random.randint(1, 1000)):
        string += str(j)
        print(string + " " +str(i))

    print()