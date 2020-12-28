import os
import math
import numpy as np
from matplotlib import legend, pyplot as plt


parameters = np.array([[50, 10000, 2], # nb_clients, requests/s, nb_threads
                       [50, 10000, 4],
                       [50, 10000, 6],
                       [50, 10000, 8],
                       [50, 10000, 10],
                       [50, 10000, 12]])


def queuing_time_basic_vs_optimized():

    plt.figure()
    plt.xlabel("Number of threads")
    plt.ylabel("Queuing time [s]")

    average = np.array([])
    variation = np.array([])

    with open("basic_server_queuing_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.array([float(i) for i in lines[0].strip().split(" ")])
        variation = np.array([float(i) for i in lines[1].strip().split(" ")])

        key = np.argsort(parameters[:, 2])
        plt.plot(parameters[key, 2], average[key], label="Basic", color='y', marker='.')
        plt.fill_between(parameters[key, 2], average[key]-variation[key], average[key]+variation[key], color='y', alpha=0.1)
        plt.legend()

    with open("optimized_server_queuing_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.array([float(i) for i in lines[0].strip().split(" ")])
        variation = np.array([float(i) for i in lines[1].strip().split(" ")])

        key = np.argsort(parameters[:, 2])
        plt.plot(parameters[key, 2], average[key], label="Optimized", color='r', marker='.')
        plt.fill_between(parameters[key, 2], average[key]-variation[key], average[key]+variation[key], color='r', alpha=0.05)
        plt.legend()

    plt.savefig("Basic_vs_optimized_queuing_time")


def service_time_basic_vs_optimized():

    plt.figure()
    plt.xlabel("Number of threads")
    plt.ylabel("Service time [s]")

    average = np.array([])
    variation = np.array([])

    with open("basic_server_service_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.array([float(i) for i in lines[0].strip().split(" ")])
        variation = np.array([float(i) for i in lines[1].strip().split(" ")])

        key = np.argsort(parameters[:, 2])
        plt.plot(parameters[key, 2], average[key], label="Basic", color='y', marker='.')
        plt.fill_between(parameters[key, 2], average[key]-variation[key], average[key]+variation[key], color='y', alpha=0.1)
        plt.legend()

    with open("optimized_server_service_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.array([float(i) for i in lines[0].strip().split(" ")])
        variation = np.array([float(i) for i in lines[1].strip().split(" ")])

        key = np.argsort(parameters[:, 2])
        plt.plot(parameters[key, 2], average[key], label="Optimized", color='r', marker='.')
        plt.fill_between(parameters[key, 2], average[key]-variation[key], average[key]+variation[key], color='r', alpha=0.05)
        plt.legend()

    plt.savefig("Basic_vs_optimized_service_time")


def response_time_basic_vs_optimized():

    plt.figure()
    plt.xlabel("Number of threads")
    plt.ylabel("Response time [s]")

    average = np.array([])
    variation = np.array([])

    with open("basic_server_response_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.array([float(i) for i in lines[0].strip().split(" ")])
        variation = np.array([float(i) for i in lines[1].strip().split(" ")])

        key = np.argsort(parameters[:, 2])
        plt.plot(parameters[key, 2], average[key], label="Basic", color='y', marker='.')
        plt.fill_between(parameters[key, 2], average[key]-variation[key], average[key]+variation[key], color='y', alpha=0.1)
        plt.legend()

    with open("optimized_server_response_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.array([float(i) for i in lines[0].strip().split(" ")])
        variation = np.array([float(i) for i in lines[1].strip().split(" ")])

        key = np.argsort(parameters[:, 2])
        plt.plot(parameters[key, 2], average[key], label="Optimized", color='r', marker='.')
        plt.fill_between(parameters[key, 2], average[key]-variation[key], average[key]+variation[key], color='r', alpha=0.05)
        plt.legend()

    plt.savefig("Basic_vs_optimized_response_time")


if __name__ == "__main__":
    queuing_time_basic_vs_optimized()
    service_time_basic_vs_optimized()
    response_time_basic_vs_optimized()
