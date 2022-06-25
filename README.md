# Racing Kart Game
A multiplayer kart game in which two players compete to see who completes the entire track first  

<pre>
<code> 
<strong>Warning:</strong> There are some flickerings due to the fast frames updates.
I have slowed it down quite a bit as discussed in <a href="#technical-notes">Technical Notes</a>.
</code>
</pre>

<br>
Below are screenshots of the three scenes/pages involved:  

|**Kart selection scene**|**Racing scene**|
|:---:|:---:|
|<img src="https://github.com/A-amon/Racing-Kart-Game/blob/main/select-kart.png" width="400"/>|<img src="https://github.com/A-amon/Racing-Kart-Game/blob/main/race.png" width="400"/>|
|**After race scene**|
|<img src="https://github.com/A-amon/Racing-Kart-Game/blob/main/after-race.PNG" width="400"/>|

## Built using
- Netbeans IDE 8.2
- JDK 1.8

## How to Play
### Controls 🎮
`Note: Player 1 = P1 | Player 2 = P2`
|P1 Keys| P2 Keys| Description|  
|:----------:|:----------:|:----------:| 
|W|⬆|FORWARD|  
|A|⬅|ROTATE LEFT by 22.5°|  
|D|➡|ROTATE RIGHT by 22.5°|  
|S|⬇|BACKWARD|  

### Rules
- Kart will collide or crash when moving outside the race track or into the grassy area
- Kart's speed will decrease or accelerate over time. `Note: The speed-meters will show the current speed of both players`  
It decreases when there is no further forward or backward/reverse motion
- Race ends when both karts are unable to continue (collided/ completed race). The following are such scenarios or conditions:  
&nbsp;- Both karts collided  
&nbsp;- Both karts crossed the starting/finish line  
&nbsp;- One kart crossed the starting/finish line whereas another collided

### Getting Started
#### Setting Up
1. Create a project
2. Include the source files inside the `src` directory
4. Start the server first by running [ServerMain](https://github.com/A-amon/Racing-Kart-Game/blob/main/src/kartracinggame/ServerMain.java) ⚙
5. Next, start the client by running [Main](https://github.com/A-amon/Racing-Kart-Game/blob/main/src/kartracinggame/Main.java) 🕹
6. Repeat Step 5 to open another client window (Now, you should have 2 client windows - For player 1 and 2)  

#### Gaming Time
1. Select a kart (Once both players have selected, you will be redirected to the race track) 🏎
2. Race! 🏁🏁
3. When the race ends, you will be redirected to the kart selection screen. You may race again by starting from Step 1! 🥳

```
- Race only starts when both clients have selected their respective karts
- A delay for when the application exits is caused by waiting for server's permission
- Only exit the application by pressing on the X button on the top-right of the application  
  (Doing otherwise will prevent the server from clearing relevant data appropriately)
```

And now, the fun part...🤣
## Technical Notes
### Slowing down the flickering
Flickering is an issue related to animation. One common way of solving this is by applying double buffering. This method eliminates (or at least, in my case, reduces a significant amount of flickers) by drawing all the Animated elements into a buffer before performing only one graphics operation (instead of one for each element) on the screen. [Find out more about Double Buffering](https://docs.microsoft.com/en-us/dotnet/desktop/winforms/advanced/how-to-reduce-graphics-flicker-with-double-buffering-for-forms-and-controls?view=netframeworkdesktop-4.8)

The implementation of this strategy inside the `update()` function found on `line 43` [here](https://github.com/A-amon/Racing-Kart-Game/blob/main/src/kartracinggame/Animator.java):
```
 Graphics gScene = scene.getGraphics();
  if(gScene != null){
      Graphics gBuffer = bufferedImage.createGraphics();

      // Paint into buffer
      for(Animated animated: animatedElements){
          if(animated.getIsPaint()){
              animated.paint(null, gBuffer);  //Painting to buffer
          }
          if(timer.isRunning()){
              animated.updateFrame();
          }
      }     

      gScene.drawImage(bufferedImage, 0, 0, scene); //Painting to screen
      scene.paintComponent(gBuffer);  //Update screen
  }        
```
### Who processes the data: Client or Server?
In my implementation, server handles the tedious task of updating every player's state/kart's data (position, speed, etc.). The idea was taken from [Gabriel's Client-Server Game Architecture](https://www.gabrielgambetta.com/client-server-game-architecture.html) (The explanations in there are amazing!). However, not everything was implemented (such client-side prediction 😞) due to insufficient time assigned for this project.

### Inspired by React
Notice that on [Line 28 in Scene class], the constructor accepts `params` or parameters. As someone who enjoys the amazing development experience provided by React, I thought of bringing `props` to my applications. This allows different `Scenes` to pass data to each other easily. The [SceneManager](https://github.com/A-amon/Racing-Kart-Game/blob/main/src/kartracinggame/SceneManager.java) is also a Singleton as only one `SceneManager` is used to manage the scene/page navigation. When a navigation is detected, `SceneManager` will alert `Main` to replace the current `Scene` with the new `Scene`: [Updating scene in Main](https://github.com/A-amon/Racing-Kart-Game/blob/cd848c51e586b2f1f4865ec625272ece071ffc3c/src/kartracinggame/Main.java#L73)

### Inspired by Unity
I had some beginner's experiences in using Unity. This game engine makes game development extremely convenient with its multitude of features. One of which is `Collider`. Wrapping a component with `Collider` will allow it to collide with other components with a `Collider` wrapper. Similarly, the [Collider](https://github.com/A-amon/Racing-Kart-Game/blob/main/src/kartracinggame/Collider.java) class is used by classes which need a collider such as [Kart](https://github.com/A-amon/Racing-Kart-Game/blob/main/src/kartracinggame/Kart.java) and [sides of the race track](https://github.com/A-amon/Racing-Kart-Game/blob/cd848c51e586b2f1f4865ec625272ece071ffc3c/src/kartracinggame/RaceScene.java#L68):  
![Colliders made visible](https://github.com/A-amon/Racing-Kart-Game/blob/main/colliders.png)  
**Note:** The red rectangles are the colliders (Kart's collider is made smaller to avoid collision due to scratches)

### Message Formatting ✉
The protocol here follows loosely on how API works.  
Hence, you might find some similarities such as the message's purpose and status codes. 

#### Payload Types
There are several payload types. Each indicates a different purpose of a message/response. The `PayloadType` enum can be found [here](https://github.com/A-amon/Racing-Kart-Game/blob/main/src/kartracinggame/PayloadType.java) 
|Type|
|----|
|KART_SELECTED|
|KART_UPDATE|
|KART_EXIT|
|CLIENT_EXIT|

#### Client's Messages
The format of a client's message is:
```java
PAYLOAD TYPE
DATA
```
Here is an example of a `KART_UPDATE` request/message:
```java
KART_UPDATE 
Action:MOVE_FORWARD; Angle:36.0;
```
`Note: The angle value is supplied because the images are loaded on client-side. Next, the number of frames/images are not fixed but are instead determined by the number of images inside the related folder. This allows for fewer hardcoded numbers and a more flexible Animated class.` See [Kart A's frames](https://github.com/A-amon/Racing-Kart-Game/tree/main/src/assets/frames/kartA) and [Animated class](https://github.com/A-amon/Racing-Kart-Game/blob/main/src/kartracinggame/Animated.java)

#### Server's Responses
The client exchanges messages with server in a certain order and format. These status codes returned alongside the server's responses will show if the previous client's messages are valid or not (and due to what reason if they weren't). The `Status` enum can be found [here](https://github.com/A-amon/Racing-Kart-Game/blob/main/src/kartracinggame/Status.java)
|Status|Code|Text|Description|
|------|----|----|-----------|
|OK|200|OK|Request has been successfully processed|
|INVALID_DATA|400|Invalid Data Syntax|Request contains wrongly formatted data|
|INVALID_TYPE|404|Invalid Payload Type|Server does not recognize nor handle this payload type|  

Here is an example of a `KART_UPDATE` response:
```java
200/KART_UPDATE
User:You; Name:kartA; Action:MOVE_FORWARD; X:525.0; Y:500.0; Speed:0.0; 
User:Enemy; Name:kartB; Action:MOVE_BACKWARD; X:800.0; Y:550.0; Speed:0.0;
```
`Note: The information within will be extracted upon arrival at client`  
[Learn more about the extraction](https://github.com/A-amon/Racing-Kart-Game/blob/cd848c51e586b2f1f4865ec625272ece071ffc3c/src/kartracinggame/Response.java#L88)




