# FTC-6837
Robot code for FTC team 6837 for the 2020/2021 season - Java, made in Android Studio

Hey Alyssa, got the code for gifjif right here. Really sorry I couldn't be there in person, thanks for picking up the slack. If they ask any questions about "main loops" the 
robot runs through, just mention how it does the math for driving, by adding the x and y variables for each joystick each frame. (should be about halfway down either 
meccDrive) 


If they ask about struggles we had, say the autonomous code was tough based on battery life, since the power of our robot was pretty much dependent on our 
battery levels, and anything less than 80% would completely change how far the robot went. 

If they ask about how gifjif "sees", (vision standard) mention how we use a mix of Vuforia and TensorFlow, with Vuforia being the "camera" for lack of a better word, and TensorFlow stating how many there are. Vuforia was built to track objects in a camera, so with a mix of that, and the pre-written TensorFlow parameters we got from the 
FTC github (thanks FTC ^-^) we were just able to plug it in. It took us a while to figure out the proper syntax for it, and it took even longer to add it to our existing Op Modes but we eventually got it. Anyways, we take the label assigned to the stack, and run it through a switch case, choosing a seperate function for each setup. 

If they ask about functions in the code, just say we mainly use them in our autonomous mode, since simplifying our move code caused problems with variable scope, and gave a bit of lag too. In the autonomous code, instead of fitting all 250 something lines of code in the switch case, we were able to put it in easily-collapsible functions, to edit the code a bit easier. 

On that same note, if they ask about things we wished we did differently, say that we wished we could do more with vision, especially for autonomous navigation, and that simplifying the code was the next thing on our list, but we were pressed for time.



Hope this stuff helps, if you've got any more questions, just send me a text or something. If I'm in my school of rock practice, I probably won't be able to respond 
right away, so ask your questions before the interview. Plus, the comments I wrote were pretty descriptive too, so that might help a bit.

Once again, thanks a bunch! We couldn't do this without you!

^-^

Head Coding Department,
-Tyson
