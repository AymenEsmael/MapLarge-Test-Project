import random
import math

#The goal of this function is to initiallize the matrix
#Values are randomly assigned either 0 or 1
def initM(row,col):
    res = []
    for i in range(row):
        inner = []
        for j in range(col):
            val = random.randrange(2)#random [0,1]
            inner.append(val)
        res.append(inner)
    return res



#Finds the missing or uncovered combinations in the NxT subset
def missing(matrix):
    want = ((0,0),(0,1),(1,0),(1,1))#What are we looking for
    missing = 0

    #Must visit for 5x6
    #col -> (0,1) (0,2) (0,3) (0,4) 
    #       (1,2) (1,3) (1,4) 
    #       (2,3) (2,4)
    #       (3,4) 
    for c1 in range(len(matrix[0])-1): #Start from 0 and go to len(matrix[i])-1
        c2 = c1 + 1
        while c2 < len(matrix[0]): #cs2 always start with one above c1
            seen = set()
            for row in range(len(matrix)):
                pair = ( matrix[row][c1], matrix[row][c2] )
                if pair in want and pair not in seen: #check if our pair is what we looking for and we have not seen it
                    seen.add(pair)
            #Once gathered we check how many we are missing and add it to the missing total      
            missing += 4 - len(seen) 
            # print(missing)
            c2 +=1
    return missing

#given a matrix and the number of col generate neighbors
def neighbor(matrix, k):
    # print(matrix)
    nei = []
    random_col = random.randrange(0,k)#get a random col [0,k-1]
    # random_col = 3
    for i in range(len(matrix)):
        m2 = [row [:] for row in matrix] #copy that row
        m2[i][random_col] ^= 1 #flip the bit 
        nei.append(m2) #add it to the nei matrix
        
    # print(nei)
    return (nei) #return N fliped bit neighbors 


#This is the procedure for SA
def sa(k):
    #We given that v and t are 2
    v = t = 2
    
    #proposed solution
    n = max(4,2*math.ceil(math.log2(k)))
    # print(n)
    
    #init our matrix
    best_M = initM(n,k)

    #get how many combinations are we missing
    best_cost = missing(best_M)

    temp = k
    alpha = 0.99
    frozen_factor = (v**t) * math.comb(k,t)#init our phi
    no_improvement = frozen_factor

    iteration = 0
    while True:

        #The generated matrix has all combination
        if best_cost == 0:
            return best_M,iteration,"Solution"
        #we looped without any improvement
        if no_improvement <= 0:
            return best_M, iteration, "Frozen"
        #We have hit the Final temperature
        if temp <= 0:
            return best_M, iteration, "Final Temperature achieved"
        
        nei = neighbor(best_M, k) #gets N neighbors
        
        best_nei = None
        best_nei_cost = None
        #Finds which Neighbor is the best to move forward with
        for cand in nei:
            c = missing(cand)
            if best_nei_cost is None or c < best_nei_cost:
                best_nei = cand
                best_nei_cost = c
        

        
        # SA acceptance (DeltaE) 
        delta = best_nei_cost - best_cost   

        accept = False
        if delta <= 0:
            accept = True
        else:
            # Probability of accepting a worse move
            p = math.exp(-delta / temp) if temp > 0 else 0.0
            if random.random() < p:
                accept = True

        if accept:
            best_M = [row[:] for row in best_nei]
            best_cost = best_nei_cost

            # reset frozen counter only if we truly improved
            if delta < 0:
                no_improvement = frozen_factor
            else:
                no_improvement -= 1  # accepted but not improved
        else:
            no_improvement -= 1


        temp = alpha * (temp) 
        iteration += 1



if __name__ == "__main__":
    k = [5,6,7]
    for k_vals in k:
        print("**********************************  "+ str(k_vals)+"    ****************************************")
        for i in range(30):
            print(i+1 , ": ", sa(k_vals))

