/*
 * (C) Copyright 2015 Kai Burjack

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.joml.camera;

/**
 * Rotates a point on a circle/arc to reach a target angle using the rotation
 * direction with the shortest distance on the circle.
 * <p>
 * Angles are specified in degrees.
 * 
 * @author Kai Burjack
 */
public class ArcRotor {

    public float maxAcceleration = 250.0f;
    public float maxDeceleration = 250.0f;
    public float target;
    public float current;
    public float velocity;

    public void update(float elapsedTimeInSeconds) {
        float currentToTarget = 180.0f - Math.abs((Math.abs(current - target) % 360.0f) - 180.0f);
        if ((current - target + 360.0f) % 360.0f < 180.0f) {
            currentToTarget *= -1.0f;
        }

        float directStopDistance = (velocity * velocity) / (2.0f * maxDeceleration);
        float acceleration = 0.0f;
        if (velocity * currentToTarget > 0.0f && directStopDistance >= Math.abs(currentToTarget)) {
            /* Decelerate */
            float directDec = maxDeceleration;
            acceleration = (currentToTarget < 0.0 ? -1 : 1) * -directDec;
        } else {
            /* Accelerate */
            float directAcc = maxAcceleration;
            acceleration = (currentToTarget < 0.0 ? -1 : 1) * directAcc;
        }
        velocity += acceleration * elapsedTimeInSeconds;
        float way = velocity * elapsedTimeInSeconds;
        if (velocity * currentToTarget > 0.0f && Math.abs(way) > Math.abs(currentToTarget)) {
            /* We would move too far */
            velocity = 0.0f;
            way = currentToTarget;
        }
        current = (current + way + 360.0f) % 360.0f;
    }

}
