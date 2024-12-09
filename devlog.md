# Dec. 3, 2024 12:51 AM
In this session I want to define and implement all of the modules required to create the user interface for the project. 
First, I want to create a menu and file management class that will display the interface and have functions to detect errors when a certain user input is given.
After I want to refactor the B-tree implementation I've created before-hand to work with the specifications for our project (minimal degree = 10).

# Dec. 3, 2024 2:40 AM
I created the basic skeletons of each of the classes required for managing the files and running the menu. 
Next session I want to implement the handle functions in Menu and start creating interactions between Menu and IDXFile. 

# Dec. 3, 2024 1:11 PM
I want to finish implementing the handle functions, create basic functions for create a node in the IDXFile, and refactor my B-tree code to work with a min-deg of 10. 

# Dec. 8, 2024 7:30 PM
I was getting issues with managing the IDXFile, so I moved the initialization of the header to the menu class since managing the header was limited. I created a Btree class with insert but it still has problems when inserting into the file due to an array out of bounds exception. While the BTree implementation does work when debugging, the file does not dynamically update since it is being rewritten at the end of execution. I need to debug the program and check how the insertion happens in the file, and whether the offset is being properly used when the b-tree insertion method is called. 

# Dec. 8, 2024 10:52 PM
I have the basic header initialization and simple insertion done, but the program breaks when handling insertion into nodes that are full--and updating the file accordingly. I believe that this issue is caused by how I am calculating the offset in the file, and not updating all of the nodes properly. Although I cannot finish implementing this, I think moving the block management to the IDXFile might've been the solution since I am manually calculating the offset at each insert/search in the b-tree. 