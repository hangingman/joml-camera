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

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A very simple but fully functional 6-DOF free/space camera.
 * <p>
 * It allows to set the linear acceleration or velocity in world-space and the angular acceleration or velocity in local camera/eye space.
 * 
 * @author Kai Burjack
 */
public class FreeCamera {
    public Vector3f linearAcc = new Vector3f();
    public Vector3f linearVel = new Vector3f();

    /** ALWAYS rotation about the local XYZ axes of the camera! */
    public Vector3f angularAcc = new Vector3f();
    public Vector3f angularVel = new Vector3f();

    public Vector3f position = new Vector3f(0, 0, 10);
    public Quaternionf rotation = new Quaternionf();
    private Quaternionf rotationInv = new Quaternionf();

    /* Computed world-space vectors */
    public Vector3f forward = new Vector3f(0, 0, -1);
    public Vector3f right = new Vector3f(1, 0, 0);
    public Vector3f up = new Vector3f(0, 1, 0);

    /**
     * Update this {@link FreeCamera} based on the given elapsed time.
     * 
     * @param dt
     *            the elapsed time
     * @return this
     */
    public FreeCamera update(float dt) {
        // update linear velocity based on linear acceleration
        linearVel.fma(dt, linearAcc);
        // update angular velocity based on angular acceleration
        angularVel.fma(dt, angularAcc);
        // update rotation based on angular velocity
        rotation.rotateAxis(angularVel.x * dt, right);
        rotation.rotateAxis(angularVel.y * dt, up);
        rotation.rotateAxis(angularVel.z * dt, forward);
        // update position based on linear velocity
        position.fma(dt, linearVel);
        // compute new world-space forward/up/right vectors
        rotation.conjugate(rotationInv);
        rotationInv.transform(forward.set(0, 0, -1));
        rotationInv.transform(up.set(0, 1, 0));
        rotationInv.transform(right.set(1, 0, 0));
        return this;
    }

    /**
     * Apply the camera/view transformation of this {@link FreeCamera} to the given matrix.
     *
     * @param m
     *            the matrix to apply the view transformation to
     * @return this
     */
    public Matrix4f apply(Matrix4f m) {
        return m.rotate(rotation).translate(-position.x, -position.y, -position.z);
    }
}
