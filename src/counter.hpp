#pragma once

#include <atomic>
#include <thread>
#include <vector>
#include <random>
#include <chrono>

class counter {
private:
    int int_rand(const int & min, const int & max) {
        static thread_local std::mt19937 generator(std::chrono::high_resolution_clock::now().time_since_epoch().count());
        std::uniform_int_distribution<int> distribution(min,max);
        return distribution(generator);
    }

    const std::size_t size;
    std::vector<std::atomic_int> counters;

public:
    explicit counter(size_t threads) : size(threads),
        counters(std::vector<std::atomic_int>(size)) {}

    counter() : counter(std::thread::hardware_concurrency()) {}

    size_t get() const {
        return std::accumulate(counters.begin(), counters.end(), 0);
    }

    void inc() {
        size_t i = int_rand(0, size-1);
        counters[i].fetch_add(1, std::memory_order_seq_cst);
    }
};