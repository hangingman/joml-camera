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

    /** Always rotation about the local XYZ axes of the camera! */
    public Vector3f angularAcc = new Vector3f();
    public Vector3f angularVel = new Vector3f();

    private final Matrix4f view = new Matrix4f();

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
        // update the rotation based on the angular velocity
        view.rotateLocalX(dt*angularVel.x)
            .rotateLocalY(dt*angularVel.y)
            .rotateLocalZ(dt*angularVel.z);
        // update position based on linear velocity
        view.translateLocal(-dt*linearVel.x, -dt*linearVel.y, -dt*linearVel.z);
        return this;
    }

    /**
     * Get the current view matrix.
     * 
     * @return the view matrix
     */
    public Matrix4f view() {
        return view;
    }

}
