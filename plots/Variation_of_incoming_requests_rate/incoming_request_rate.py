import os
import math
import numpy as np
from matplotlib import legend, pyplot as plt


parameters = np.array([[50, 2000, 6], # nb_clients, requests/s, nb_threads
                       [50, 5000, 6],
                       [50, 10000, 6],
                       [50, 20000, 6],
                       [50, 30000, 6],
                       [50, 40000, 6],
                       [50, 50000, 6],
                       [50, 100000, 6]])


def queuing_time_requests_rate():

    plt.figure()
    plt.xlabel("Requests rate [1/sec]")
    plt.ylabel("Queuing time [s]")

    average = np.array([])
    variation = np.array([])

    with open("easy_queuing_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.flip(np.array([float(i) for i in lines[0].strip().split(" ")]))
        variation = np.flip(np.array([float(i) for i in lines[1].strip().split(" ")]))

        key = np.argsort(parameters[:, 1])
        plt.plot(parameters[key, 1], average[key], label="Easy", color='y', marker='.')
        plt.fill_between(parameters[key, 1], average[key]-variation[key], average[key]+variation[key], color='y', alpha=0.1)
        plt.legend()

    with open("cpu_intensive_queuing_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.flip(np.array([float(i) for i in lines[0].strip().split(" ")]))
        variation = np.flip(np.array([float(i) for i in lines[1].strip().split(" ")]))

        key = np.argsort(parameters[:, 1])
        plt.plot(parameters[key, 1], average[key], label="CPU-intensive", color='r', marker='.')
        plt.fill_between(parameters[key, 1], average[key]-variation[key], average[key]+variation[key], color='r', alpha=0.05)
        plt.legend()

    with open("network_intensive_queuing_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.flip(np.array([float(i) for i in lines[0].strip().split(" ")]))
        variation = np.flip(np.array([float(i) for i in lines[1].strip().split(" ")]))

        key = np.argsort(parameters[:, 1])
        plt.plot(parameters[key, 1], average[key], label="Network-intensive", color='g', marker='.')
        plt.fill_between(parameters[key, 1], average[key]-variation[key], average[key]+variation[key], color='g', alpha=0.05)
        plt.legend()

    plt.savefig("Requests_rate_queuing_time")


def service_time_requests_rate():

    plt.figure()
    plt.xlabel("Requests rate [1/sec]")
    plt.ylabel("Service time [s]")

    average = np.array([])
    variation = np.array([])

    with open("easy_service_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.flip(np.array([float(i) for i in lines[0].strip().split(" ")]))
        variation = np.flip(np.array([float(i) for i in lines[1].strip().split(" ")]))

        key = np.argsort(parameters[:, 1])
        plt.plot(parameters[key, 1], average[key], label="Easy", color='y', marker='.')
        plt.fill_between(parameters[key, 1], average[key]-variation[key], average[key]+variation[key], color='y', alpha=0.1)
        plt.legend()

    with open("cpu_intensive_service_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.flip(np.array([float(i) for i in lines[0].strip().split(" ")]))
        variation = np.flip(np.array([float(i) for i in lines[1].strip().split(" ")]))

        key = np.argsort(parameters[:, 1])
        plt.plot(parameters[key, 1], average[key], label="CPU-intensive", color='r', marker='.')
        plt.fill_between(parameters[key, 1], average[key]-variation[key], average[key]+variation[key], color='r', alpha=0.05)
        plt.legend()

    with open("network_intensive_service_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.flip(np.array([float(i) for i in lines[0].strip().split(" ")]))
        variation = np.flip(np.array([float(i) for i in lines[1].strip().split(" ")]))

        key = np.argsort(parameters[:, 1])
        plt.plot(parameters[key, 1], average[key], label="Network-intensive", color='g', marker='.')
        plt.fill_between(parameters[key, 1], average[key]-variation[key], average[key]+variation[key], color='g', alpha=0.05)
        plt.legend()

    plt.savefig("Requests_rate_service_time")


def response_time_requests_rate():

    plt.figure()
    plt.xlabel("Requests rate [1/sec]")
    plt.ylabel("Response time [s]")

    average = np.array([])
    variation = np.array([])

    with open("easy_response_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.flip(np.array([float(i) for i in lines[0].strip().split(" ")]))
        variation = np.flip(np.array([float(i) for i in lines[1].strip().split(" ")]))

        key = np.argsort(parameters[:, 1])
        plt.plot(parameters[key, 1], average[key], label="Easy", color='y', marker='.')
        plt.fill_between(parameters[key, 1], average[key]-variation[key], average[key]+variation[key], color='y', alpha=0.1)
        plt.legend()

    with open("cpu_intensive_response_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.flip(np.array([float(i) for i in lines[0].strip().split(" ")]))
        variation = np.flip(np.array([float(i) for i in lines[1].strip().split(" ")]))

        key = np.argsort(parameters[:, 1])
        plt.plot(parameters[key, 1], average[key], label="CPU-intensive", color='r', marker='.')
        plt.fill_between(parameters[key, 1], average[key]-variation[key], average[key]+variation[key], color='r', alpha=0.05)
        plt.legend()

    with open("network_intensive_response_time.txt", 'r') as f:
        lines = f.readlines()
        average = np.flip(np.array([float(i) for i in lines[0].strip().split(" ")]))
        variation = np.flip(np.array([float(i) for i in lines[1].strip().split(" ")]))

        key = np.argsort(parameters[:, 1])
        plt.plot(parameters[key, 1], average[key], label="Network-intensive", color='g', marker='.')
        plt.fill_between(parameters[key, 1], average[key]-variation[key], average[key]+variation[key], color='g', alpha=0.05)
        plt.legend()

    plt.savefig("Requests_rate_response_time")


if __name__ == "__main__":
    queuing_time_requests_rate()
    service_time_requests_rate()
    response_time_requests_rate()
