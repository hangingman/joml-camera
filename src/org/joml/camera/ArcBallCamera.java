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

public class ArcBallCamera {

	public Vector3Mover centerMover = new Vector3Mover();
	{
		centerMover.maxDirectAcceleration = 5.0f;
		centerMover.maxDirectDeceleration = 5.0f;
	}

	public final ScalarMover alphaMover = new ScalarMover();
	public final ScalarMover betaMover = new ScalarMover();
	public final ScalarMover zoomMover = new ScalarMover();
	{
		zoomMover.current = 10.0f;
		zoomMover.target = 10.0f;
		zoomMover.maxAcceleration = 10.0f;
		zoomMover.maxDeceleration = 15.0f;
	}

	public Matrix4f viewMatrix(Matrix4f mat) {
		mat.translate(0, 0, -zoomMover.current)
				.rotateX(betaMover.current).rotateY(alphaMover.current)
				.translate(-centerMover.current.x, -centerMover.current.y, -centerMover.current.z);
		return mat;
	}

	public void alpha(float alpha) {
		alphaMover.target = alpha;
	}

	public void beta(float beta) {
		betaMover.target = beta;
	}

	public void zoom(float zoom) {
		zoomMover.target = zoom;
	}

	public void center(float x, float y, float z) {
		centerMover.target.set(x, y, z);
	}

	public void update(float elapsedTimeInSeconds) {
		alphaMover.update(elapsedTimeInSeconds);
		betaMover.update(elapsedTimeInSeconds);
		zoomMover.update(elapsedTimeInSeconds);
		centerMover.update(elapsedTimeInSeconds);
	}

}
