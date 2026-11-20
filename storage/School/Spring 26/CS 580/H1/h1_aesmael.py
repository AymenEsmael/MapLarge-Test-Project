from pyamaze import maze, agent, textLabel
from queue import PriorityQueue
import sys 


def g(cell1,cell2): #Euclidean distance calculation
    x1,y1 = cell1 #unpack the first coordinate
    x2,y2 = cell2 #unpack the second coordinate
    return ((x1-x2)**2 + (y1-y2)**2)**(1/2) #Evaluate

def h(cell1,cell2): #Manhattan distance calcauation
    x1,y1 = cell1 #unpack the first and second coordinate
    x2, y2 = cell2
    return abs(x1-x2) + abs(y1-y2) #Evaluate



def Astar(m): #Start A*
    start = (m.rows, m.cols) #starting coorinate always bottom right
    f = {cell:float('infinity') for cell in m.grid} #init f(n) to inf
    g_val = {cell:float('infinity') for cell in m.grid} #init g(n) to inf
    g_val[start] = g(start,start) #the starting points g(n) calc
    h_val = h(start,(1,1)) #The starting point h(n) calc
    f[start] = g_val[start] + h_val #Get f(n) from g(n) + h(n)
    Aspath =  PriorityQueue() #init priority queue
    Aspath.put( (f[start], h_val, start) ) #add (f(n), h(n), starting coordinte)

    visited = set()

    camefrom = {} #tracks where each path came from

    while not Aspath.empty():
        currcell = Aspath.get()[2] #pop an element
        if currcell in visited:
            continue
        visited.add(currcell)
        if currcell == (1,1): #check if it is a goal state
            break

        for d in "NSEW": #visit the neig
            if m.maze_map[currcell][d] == True:
                if d == "N":
                    childcell = (currcell[0]-1,currcell[1])
                elif d == "E":
                    childcell = (currcell[0],currcell[1]+1)
                elif d == "S":
                    childcell = (currcell[0]+1,currcell[1])
                else:
                    childcell = (currcell[0],currcell[1]-1)
                # print(childcell)
                
                #calcuate g(n) and h(n) to get f(n)
                temp_g = g(start,childcell)
                temp_h = h(childcell,(1,1))
                temp_f = temp_g + temp_h
                # print(temp_f, temp_g, temp_h)

                #if we only find a better path we add it to the queue
                if temp_g < g_val[childcell]:
                    camefrom[childcell] = currcell
                    Aspath.put( (temp_f,temp_h, childcell) )
                    # print(list(Aspath.queue))
                    f[childcell] = temp_f
                    g_val[childcell] = temp_g
    #Traverse our path forward
    fwdPath = {}
    cell= (1,1)
    while cell != start:
        fwdPath[camefrom[cell]] = cell
        cell = camefrom[cell]
    return fwdPath


#gets input i.e rows and cols from the user
if len(sys.argv) < 3:
    print("Usage: python h1_aesmael.py <Number or rows> <Number of columns>")
    exit(1)

#rows and cols must are variables, so factual values are needed to avoid errors
rows = int(sys.argv[1])
cols = int(sys.argv[2])
m = maze(rows,cols)
m.CreateMaze() #create a maze with one path
a = agent(m, footprints = True, color="blue")

path = Astar(m)
# print(path) 

m.tracePath({a:path})
l=textLabel(m,'A* Path Length',len(path)+1) #display the cost of the solution

# print("A* Path Length:", len(path))
# print("Maze's Path Length:", len(m.path))


m.run()



