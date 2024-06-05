## AI Project-1 

## Optimizing Delivery Truck Routes Using Simulated Annealing

## Description
This project aims to develop a simulated annealing algorithm to optimize delivery truck 
routes with the goal of minimizing total travel distance. The focus is on solving the Vehicle 
Routing Problem (VRP) efficiently.

## Objective
The objective is to implement a simulated annealing algorithm to solve the VRP. This 
involves determining the optimal routes that minimize the total travel distance while 
adhering to truck capacity constraints.

## Methodology:
You need to develop a tool to solve the VRP.
1. Problem Definition
• Delivery Points: Define a set of delivery locations with demands (capacity).
• Trucks: Use a fixed number of trucks, each with an equal capacity.
• Depot: Central starting point 
• Cooling Schedule: Define a cooling schedule for the simulated annealing 
algorithm.
2. Algorithm Design
• Initial Solution: Generate a simple, feasible set of routes randomly.
• Assign delivery tasks to the minimum number of trucks, considering capacity 
constraints.
• Objective Function: Calculate the total travel distance for the routes.
• Neighborhoods Search: Create neighbouring solutions by swapping delivery 
points between routes.
