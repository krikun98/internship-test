#pragma once

#include <atomic>
#include <thread>
#include <vector>
#include <random>
#include <chrono>

class counter {
private:
    const std::size_t size;
    std::vector<std::atomic_int> counters;

    std::mt19937_64 rng;
    std::uniform_int_distribution<std::mt19937_64::result_type> dist;
public:
    explicit counter(size_t threads) : size(threads),
        counters(std::vector<std::atomic_int>(size)),
        rng(std::chrono::high_resolution_clock::now().time_since_epoch().count()),
        dist(std::uniform_int_distribution<std::mt19937_64::result_type>(0, size-1)){}

    counter() : counter(std::thread::hardware_concurrency()) {}

    size_t get() const {
        return std::accumulate(counters.begin(), counters.end(), 0);
    }

    void inc() {
        size_t i = dist(rng);
        counters[i].fetch_add(1, std::memory_order_seq_cst);
    }
};