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

1. This is a variation of the standard Theta*.
2. In $f(n) = g(n) + h(n)$, $g(n) = cost + \alpha * cross track (normalized) + \beta * AngleDeviationToGlobalVector + \gamma * AngleTurn$
3. This will make the algorithm be biased to the global vector between start to goal, while ensuring a smooth path
4. 
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