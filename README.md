# joml-camera
Various kinds of camera controls for your game

Motivation and Goal
-------------------

The goal of this library is to provide camera controls needed by any 3D application, such as a 3D content editor, a simple model viewer or a full-fledged game. In any of these cases the user needs a way to move and look around the scene. Based on the JOML library, joml-camera provides you with simple and easy-to-integrate camera controls to make that possible.

An important design choice of joml-camera is to be independent of the concrete input device you use for your application/game.
You are therefore not limited to using a keyboard and a mouse, but can use any sort of input device you like. Each camera has an interface allowing you to tell the camera control about input value changes.

Different Cameras
-----------------

Different kinds of 3D applications have different needs for their camera controls. So far joml-camera will provide you with the following camera styles:

- Arcball Camera: Lets you move on a spherical surface and view a single point in space
- Free Camera: You can freely look around and move the camera in the scene

Physically Based Behaviour
--------------------------

Typically, camera controls in 3D digital content creation tools, such as Houdini, 3DS Max or Blender perform their movement immediately and abruptly with the user moving the input device (i.e. mouse). For these tools, this is desired, as it gives direct and accurate feedback to the user's interaction.
For certain 3D games or a cinematic demonstration this however creates an unnatural feeling, because camera movements seem choppy. In life however nothing happens instantaneously but always continuously with a given "smoothness."

Cameras in joml-camera therefore make use of physically based movements which comply with Newton's second law, taking mass and inertia into account when moving or rotating a camera.
You can configure the mass and the maximum linear and angular acceleration of a camera. After that, you give a camera its desired target position and target rotation angles and the camera will try to reach that target within physical constraints.

The goal with physically based movement is that a camera will never instantaneously stop or revert movement or rotation. It will always behave in a realistic way giving it a natural feeling as if it was a real camera in your scene.
