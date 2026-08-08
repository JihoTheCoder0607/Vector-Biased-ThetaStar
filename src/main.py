import jpype.imports
import os
from jpype import JArray, JChar
import matplotlib.pyplot as plt
import copy
import random
import itertools
import time

def rectangle(grid, x, y, n):
    xspan = (0, len(grid[0]) - x)
    yspan = (0, len(grid) - y)
    for i in range(n):
        x_point = random.choice(range(*xspan))
        y_point = random.choice(range(*yspan))
        for j in range(y):
            for k in range(x):
                if grid[y_point+j][x_point+k] != "S" and grid[y_point+j][x_point+k] != "E":
                    grid[y_point+j][x_point+k] = "#"
    return grid

def create_maze(grid_size, n):
    grids = []
    for i in range(n):
        grid = [[" " for _ in range(grid_size[1])] for _ in range(grid_size[0])]

        grid = rectangle(grid, 5, 2, 800)
        grid = rectangle(grid, 7, 6, 600)
        grid = rectangle(grid, 1, 1, 800)

        sr = random.choice(range(grid_size[0]))
        sc = random.choice(range(grid_size[1]))

        while grid[sr][sc] == "#":
            sr = random.choice(range(grid_size[0]))
            sc = random.choice(range(grid_size[1]))
        grid[sr][sc] = "S"

        er = random.choice(range(grid_size[0]))
        ec = random.choice(range(grid_size[1]))
        while (er == sr and ec == sc) or grid[er][ec] == "#":
            er = random.choice(range(grid_size[0]))
            ec = random.choice(range(grid_size[1]))
        grid[er][ec] = "E"
        grids.append(grid)
    return grids

def find_in_maze(grid, x):
    for row_idx, row in enumerate(grid):
        if x in row:
            col_idx = row.index(x)
            return row_idx, col_idx

def visualize_maze(grid, path):
    plt.figure(figsize=(10, 10))
    plot = copy.deepcopy(grid)
    for i in range(len(plot)):
        for j in range(len(plot[i])):
            if plot[i][j] == " ":
                plot[i][j] = 0.0
            elif plot[i][j] == "S":
                plot[i][j] = 1.0
            elif plot[i][j] == "E":
                plot[i][j] = 2.0
            elif plot[i][j] == "P":
                plot[i][j] = 3.0
            elif plot[i][j] == "#":
                plot[i][j] = 4.0
    for i in range(1, len(path)):
        node1 = path[i-1]
        node2 = path[i]
        plt.annotate('', xy=(node2.position.x, node2.position.y), xytext=(node1.position.x, node1.position.y),
                     arrowprops=dict(facecolor='red', lw=0.1))
    plt.imshow(plot)
    plt.show()


current_dir = os.path.dirname(os.path.abspath(__file__))

jvm_path = "/Users/ijiho/Library/Java/JavaVirtualMachines/openjdk-26.0.1/Contents/Home/lib/server/libjvm.dylib"

jpype.startJVM(jvm_path, classpath=[current_dir])

VBTStar = jpype.JClass("VBTStar")
ThetaStar = jpype.JClass("ThetaStar")
AStar = jpype.JClass("AStar")

Point = jpype.JClass("java.awt.Point")


def simulate(grid_size, model_class, show, num_trials, file_name, hyperparameters=None):
    # Warming up JVM
    print("Warming up the Java Virtual Machine (JVM)...")
    dummy_grid = create_maze(grid_size, 1)
    dummy_startr, dummy_startc = find_in_maze(dummy_grid[0], "S")
    dummy_endr, dummy_endc = find_in_maze(dummy_grid[0], "E")
    dummy_start = Point(dummy_startc, dummy_startr)
    dummy_goal = Point(dummy_endc, dummy_endr)

    for i in range(1000):
        for model_c in model_class:
            JavaGrid = JArray(JArray(JChar))
            java_grid = JavaGrid([JArray(JChar)(row) for row in dummy_grid[0]])
            if model_c == VBTStar:
                model = model_c(java_grid, dummy_start, dummy_goal, 0.0, 0.0, 0.0)
            else:
                model = model_c(java_grid, dummy_start, dummy_goal)
            res = model.search()
            p = model.reconstructPath(res)
            if model_c in (VBTStar, ThetaStar):
                p = model.shortcutRayCasting(p)
    print("JVM Warmed up successfully. Starting actual benchmark...")

    param_list = hyperparameters if hyperparameters is not None else [(None, None, None)]
    grid = create_maze(grid_size, num_trials)

    JavaGrid = JArray(JArray(JChar))

    results = {}


    with open(file_name, "a") as file:
        file.write(f"Grid Size: {grid_size[0]} x {grid_size[1]}\n")
        done = False
        for a, b, g in itertools.product(*param_list):
            total_metrics = {model_c: [0.0, 0.0, 0.0, 0.0] for model_c in model_class}
            if done:
                model_class = [VBTStar]
            for trial in range(num_trials):
                startr, startc = find_in_maze(grid[trial], "S")
                endr, endc = find_in_maze(grid[trial], "E")
                start = Point(startc, startr)
                goal = Point(endc, endr)

                for model_c in model_class:
                    java_grid = JavaGrid([JArray(JChar)(row) for row in grid[trial]])

                    if model_c == VBTStar and hyperparameters is not None:

                        model = model_c(java_grid, start, goal, a, b, g)
                    elif model_c == AStar:
                        model = model_c(java_grid, start, goal)
                    else:
                        model = model_c(java_grid, start, goal)

                    start_time = time.perf_counter()
                    result = model.search()
                    path = model.reconstructPath(result)

                    if model_c in (VBTStar, ThetaStar):
                        path = model.shortcutRayCasting(path)
                    end_time = time.perf_counter()

                    elapsed_time = end_time - start_time

                    if trial in show:
                        for node in path:
                            ny, nx = node.position.y, node.position.x
                            if ny == startr and nx == startc:
                                java_grid[ny][nx] = 'S'
                            elif ny == endr and nx == endc:
                                java_grid[ny][nx] = 'E'
                            else:
                                java_grid[ny][nx] = 'P'
                        visualize_maze([list(row) for row in java_grid], path)

                    m = model.metrics(path)
                    total_metrics[model_c][0] += m[0]
                    total_metrics[model_c][1] += m[1]
                    total_metrics[model_c][2] += elapsed_time
                    total_metrics[model_c][3] += m[2]
            file.write(f"Average Metrics over {num_trials} trials:\n")
            file.write(f"Hyperparameters: {a} {b} {g}\n")
            for model_c, sums in total_metrics.items():
                avg_dist = sums[0] / num_trials
                avg_angle = sums[1] / num_trials
                avg_time = sums[2] / num_trials
                avg_nodes = sums[3] / num_trials
                results[(model_c, a, b, g)] = [avg_dist, avg_angle, avg_time, avg_nodes]
                name = model_c.__name__ if hasattr(model_c, '__name__') else str(model_c)
                file.write(f"{name} - Distance: {avg_dist:.2f}, Avg Angle Turn: {avg_angle:.2f}, Avg Time: {avg_time}, Avg Nodes Expanded: {avg_nodes}\n")
                file.write("\n")
            print(f"Done with Combination: {a}, {b}, {g}")
            done = True
    return results

def epsilon_constraint(results, e):
    theta_d, theta_a, theta_t, theta_n = results[(ThetaStar, 0, 0, 0)]
    theta_d *= (1+e[0])
    theta_a *= (1+e[1])
    theta_t *= (1+e[2])
    theta_n *= (1+e[3])

    results = {k:v for k, v in results.items() if k != (ThetaStar, 0, 0, 0)}
    best = {}
    for key, value in results.items():
        if value[0] < theta_d and value[1] < theta_a and value[2] < theta_t and value[3] < theta_n:
            best[key] = value[1]
    if not best:
        raise ValueError(
            "No hyperparameter combination satisfies the constraints."
        )
    min_key = min(best, key=best.get)
    return min_key



hyperparameters = [[0, 1, 2, 4, 8, 16, 32, 64, 128], [0, 1, 2, 4, 8, 16, 32, 64, 128], [0, 1, 2, 4, 8, 16, 32, 64, 128]]

# 1. Grid Search to find best hyperparameter values
results = simulate((600, 400), [ThetaStar, VBTStar], [], 500, "grid_search.txt", hyperparameters)
best_combination = epsilon_constraint(results, [0.2, -1, 0.2, 0.2])
print(best_combination)
print(results[best_combination])

# 2. Simulation
simulate((600, 400), [AStar, ThetaStar, VBTStar], [], 2000, "result.txt", [[best_combination[1]], [best_combination[2]], [best_combination[3]]])

jpype.shutdownJVM()