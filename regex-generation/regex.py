import string
import random
import numpy as np

categories = ["0", "1", "2", "3", "4", "5"]

def generate_requests(filename, nb_lines, nb_types, nb_chars):
    """
    Generate requests with a list of types and a regex and writes it in a file.
    :param filename : the name of file where to write the generated requests
    :param nb_lines : the number of lines of requests to generate
    :param nb_types : the number of types to associate to each request
    :param nb_chars : the number of characters for each generated regex for the requests
    :return None
    """
    requests = []
    used_regexes = []

    while len(requests) < nb_lines:
        if len(requests) > 1 and random.random() > 0.75:
            requests.append(random.choice(requests))
        else:
            regex = list()
            for _ in range(nb_chars):
                regex.append(random.choice(string.ascii_lowercase + string.digits))
            regex = ''.join(regex)

            if regex not in used_regexes:
                types_list = random.sample(categories, k=nb_types)
                requests.append(",".join(types_list) + ";" + "^" + regex + "\n")
                used_regexes.append(regex)

    with open(filename, 'w+') as f:
        f.writelines(requests)


def network_intensive_requests(filename, nb_lines, nb_types):
    """
    Generate requests with a list of types and a regex and writes it in a file. The regexes are specially network intensive because short (1 character) but numerous.
    :param filename : the name of file where to write the generated requests
    :param nb_lines : the number of lines of requests to generate
    :param nb_types : the number of types to associate to each request
    :return None
    """
    start_up = ["B", "C", "D", "F", "G", "H", "L", "M", "N", "P", "R", "S"]
    random.shuffle(start_up)
    end_low = ["a", "e", "i", "o", "u"]
    random.shuffle(end_low)

    requests = []
    for i in range(nb_lines):
        if len(requests) > 1 and random.random() > 0.75:
            requests.append(random.choice(requests))
        else:
            types_list = random.sample(categories, k=nb_types)
            regex = ",".join(types_list) + ";" + "^" + start_up[i % len(start_up)] + end_low[i % len(end_low)] + "\n"
            requests.append(regex)

    with open(filename, 'w+') as f:
        f.writelines(requests)



if __name__ == "__main__":

    """ Simple requests """
    generate_requests("easy-requests.txt",100,2,10)

    """ CPU intensive requests """
    generate_requests("cpu-intensive-requests.txt",100,2,100)

    """ Network intensive requests """
    network_intensive_requests("network_intensive_requests.txt",100,2)
