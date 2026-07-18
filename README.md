# Vector-Biased Theta* 🔍

A Java project exploring autonomous robot navigation through the development and evaluation of geometry-inspired path planning algorithms.

## Overview 👀

Traditional path planning algorithms, such as A*, estimate the cost of reaching a goal using the distance already traveled and a heuristic estimate of the remaining distance.

This project investigates whether incorporating geometric information—specifically
- a robot's distance from the direct line connecting the start and goal & 
- the difference in angle between the direct line and the trajectory vector

can improve navigation efficiency.

The goal is to design, implement, and experimentally evaluate this approach against existing path planning methods.

## Project Goals 🥅

- Build a modular robot navigation framework in Java
- Simulate robots navigating grid-based environments
- Implement a custom geometry-inspired path planning algorithm
- Compare its performance with classical algorithms such as A*
- Evaluate performance by measuring nodes expanded, execution time, etc.

## Proposed Algorithm 🧠

The custom algorithm extends traditional path planning by introducing an additional geometric cost.

Instead of only considering:

- Distance traveled
- Estimated distance to the goal

1. At each iteration, the algorithm first filters the open list to nodes whose Euclidean distance from the current node is less than a configurable radius $r$. These nodes form the candidate set for subsequent evaluation.
2. Theta* is used as the underlying search algorithm to compute the path cost $g(n)$, heuristic $h(n)$, and evaluation function $f(n)=g(n)+h(n)$ for each candidate & make Line Of Sight checks.
3. Nodes where $f(n) < f(n)_{min} * 1.15$ will be selected into a focal list
4. For each node in the focal list, a penalty score will be calculated where

- $p(n) = \alpha d(n) + \beta a(n)$
- $d(n)$: normalized score of the robot's distance from the direct line connecting the start and goal (cross-track score)
- $a(n)$: normalized score of the difference in angle between the direct line and the trajectory vector

5. The penalty will be used to decide which search node to expand next.

The hypothesis is that encouraging the robot to remain close to this line may reduce unnecessary exploration while still allowing detours around obstacles when required.

**Note:** This algorithm is currently experimental and its effectiveness will be evaluated through benchmarking.

## Planned Features ✨

- Implementation of VBT*
- Simulations against large maps
- Benchmarking against traditional pathplanning algorithms

## Evaluation Metrics 📊
- Path length
- Nodes expanded
- Execution time
- Success rate
- Memory usage
- etc.

## Results
- To be added

## Future Directions 🧭
- After benchmarking, the algorithm can be improved
- The algorithm can be tested in real-world pathplanning scenarios