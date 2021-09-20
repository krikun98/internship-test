#include "counter.hpp"
#include <iostream>
#include <thread>
#include <algorithm>
#include <functional>
#include <cassert>
#include <random>
#include <chrono>

const size_t n = 10000;
std::mt19937_64 rng(std::chrono::high_resolution_clock::now().time_since_epoch().count());
std::uniform_int_distribution<std::mt19937_64::result_type> dist(
        std::uniform_int_distribution<std::mt19937_64::result_type>(0, 1)); // global for simplicity
const bool chat = false;

void runner(counter* c) {
    for (std::size_t i = 0; i < n; ++i) {
        c->inc();
        if constexpr (chat) {
            std::cout << std::this_thread::get_id() << ": " << i << " " << c->get() << std::endl;
        }
    }
}

void sleepy_runner(counter* c) {
    for (std::size_t i = 0; i < n; ++i) {
        if (dist(rng)) {
            if constexpr (chat) {
                std::cout << std::this_thread::get_id() << ": sleeping" << std::endl;
            }
            std::this_thread::sleep_for(std::chrono::milliseconds(1));
        }
        c->inc();
        if constexpr (chat) {
            std::cout << std::this_thread::get_id() << ": " << i << " " << c->get() << std::endl;
        }
    }
}

template <typename Fun>
void test_single_thread(Fun runner, const std::string& runner_name) {
    std::cout << "Single thread test, runner: " << runner_name << std::endl;
    counter c = counter();
    runner(&c);
    assert(c.get() == n);
    if constexpr (chat) {
        std::cout << std::this_thread::get_id() << ": " << c.get() << std::endl;
    }
    std::cout << "Passed" << std::endl;
}

template <typename Fun>
void test_multiple_threads(Fun runner, const std::string& runner_name) {
    std::cout << "Multiple thread test, runner: " << runner_name << std::endl;
    counter c = counter();
    const size_t threads =  std::thread::hardware_concurrency();
    std::vector< std::thread > my_threads;
    for (std::size_t i = 0; i < threads; ++i) {
        my_threads.emplace_back(runner, &c);
    }
    std::for_each(my_threads.begin(), my_threads.end(), std::mem_fn(&std::thread::join));
    assert(c.get() == n*threads);
    if constexpr (chat) {
        std::cout << std::this_thread::get_id() << ": " << c.get() << std::endl;
    }
    std::cout << "Passed" << std::endl;
}

int main() {
    test_single_thread(runner, "default");
    test_multiple_threads(runner, "default");
    test_multiple_threads(sleepy_runner, "sleepy");
    return 0;
}