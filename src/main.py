import jpype.imports
import os
from jpype import JArray, JChar
from maze_dataset import MazeDataset, MazeDatasetConfig
from maze_dataset.generation import LatticeMazeGenerators

def create_maze(grid_n, n_mazes):
	cfg = MazeDatasetConfig(
		name="test",
		grid_n=grid_n,
		n_mazes=n_mazes,
		maze_ctor=LatticeMazeGenerators.gen_dfs,
	)
	dataset = MazeDataset.from_config(cfg)

	grids = []
	for maze in dataset:
		maze = maze.as_ascii().replace('X', ' ')
		grids.append([[ch for ch in line] for line in maze.splitlines()])
	return grids

def print_maze(grid):
	for row in grid:
		print(row)
	print()

def find_in_maze(grid, x):
    for row_idx, row in enumerate(grid):
        if x in row:
            col_idx = row.index(x)
            return row_idx, col_idx

current_dir = os.path.dirname(os.path.abspath(__file__))

jvm_path = "/Users/ijiho/Library/Java/JavaVirtualMachines/openjdk-26.0.1/Contents/Home/lib/server/libjvm.dylib"

jpype.startJVM(jvm_path, classpath=[current_dir])

VBTStar = jpype.JClass("VBTStar")

Point = jpype.JClass("java.awt.Point")

grid = create_maze(grid_n=10, n_mazes=1)[0]

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

for row in java_grid:
    for element in row:
        print(f"[{element}]", end="")
    print()

jpype.shutdownJVM()