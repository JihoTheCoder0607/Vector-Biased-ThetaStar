import jpype.imports
import os
from jpype import JArray, JChar
import matplotlib.pyplot as plt
import copy
import random

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

def create_maze(grid_size):
    grid = [[" " for _ in range(grid_size[1])] for _ in range(grid_size[0])]

    grid = rectangle(grid, 2, 1, 130)
    grid = rectangle(grid, 3, 2, 70)
    grid = rectangle(grid, 1, 1, 300)

    sr = random.choice(range(grid_size[0]))
    sc = random.choice(range(grid_size[1]))

    while grid[sr][sc] == "#":
        sr = random.choice(range(grid_size[0]))
        sc = random.choice(range(grid_size[1]))
    grid[sr][sc] = "S"

    er = random.choice(range(grid_size[0]))
    ec = random.choice(range(grid_size[1]))
    while (er == sr and ec == ec) or grid[er][ec] == "#":
        er = random.choice(range(grid_size[0]))
        ec = random.choice(range(grid_size[1]))
    grid[er][ec] = "E"
    return grid

def print_maze(grid):
	for row in grid:
		print(row)
	print()

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

Point = jpype.JClass("java.awt.Point")

grid = create_maze((60, 50))


JavaGrid = JArray(JArray(JChar))

java_grid = JavaGrid([
    JArray(JChar)(row)
    for row in grid
])

startr, startc = find_in_maze(grid, "S")
endr, endc = find_in_maze(grid, "E")

start = Point(startc, startr)
goal = Point(endc, endr)

vbtstar = VBTStar(java_grid, start, goal)
result = vbtstar.search()

path = vbtstar.reconstructPath(result)

for node in path:
    if node.position.y == startr and node.position.x == startc:
        java_grid[node.position.y][node.position.x] = 'S'
    elif node.position.y == endr and node.position.x == endc:
        java_grid[node.position.y][node.position.x] = 'E'
    else:
        java_grid[node.position.y][node.position.x] = 'P'

visualize_maze([list(row) for row in java_grid], path)

for row in java_grid:
    for element in row:
        print(f"[{element}]", end="")
    print()

jpype.shutdownJVM()