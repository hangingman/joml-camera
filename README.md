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
