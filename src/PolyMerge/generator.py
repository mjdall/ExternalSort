rand_gen = 0xD5
def next_rand():
    global rand_gen
    rand_gen = (rand_gen * 1103515245 + 12345) & 0xFFFFFFFF
    return rand_gen

num_runs = 100000000
for i in range(0, num_runs):
    run = []
    if next_rand() & 0x7F == 0:
        run.append('')
        continue
    record = ""
    for k in range(0, 16):
        record += chr(next_rand() % 26 + ord('A'))
    print(record)
