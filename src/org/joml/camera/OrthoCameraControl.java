package org.joml.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Allows to control an orthographic camera with the mouse via panning and zooming.
 * <p>
 * Call the following methods:
 * <ul>
 * <li>{@link #setSize(int, int)} when the window/control size changes
 * <li>{@link #onMouseDown()} when further calls to {@link #onMouseMove(int, int)} should pan the view
 * <li>{@link #onMouseMove(int, int)} everytime the mouse moves
 * <li>{@link #onMouseUp()} when panning should stop
 * <li>{@link #zoom(float)} to zoom in/out
 * <li>{@link #viewproj()} to obtain the current view-projection matrix
 * <li>{@link #center(float, float)} to center the view onto the given coordinate
 * </ul>
 * 
 * @author Kai Burjack
 */
public class OrthoCameraControl {

    private Matrix4f view = new Matrix4f();
    private Matrix4f viewproj = new Matrix4f();
    private Matrix4f invviewproj = new Matrix4f();
    private int[] vp = new int[4];
    private float mouseX, mouseY;
    private boolean mouseDown;
    private Vector3f v = new Vector3f();

    /**
     * @param extents the initial extents in world coordinates
     */
    public OrthoCameraControl(float extents) {
        view.setOrtho2D(-extents, extents, -extents, extents);
        update();
    }

    /**
     * @param width the width of the control/window in window coordinates/pixels
     * @param height the height of the control/window in window coordinates/pixels
     */
    public void setSize(int width, int height) {
        vp[0] = 0;
        vp[1] = 0;
        vp[2] = width;
        vp[3] = height;
        update();
    }

    public Matrix4f viewproj() {
        return viewproj;
    }

    private void update() {
        float aspect = (float) vp[2] / vp[3];
        viewproj.setOrtho2D(-aspect, +aspect, -1, +1).mulAffine(view).invert(invviewproj);
    }

    /**
     * @param x the x coordiante of the point to center on in world coordinates
     * @param y the y coordiante of the point to center on in world coordinates
     */
    public void center(float x, float y) {
        view.setTranslation(0, 0, 0).translate(-x, -y, 0);
        update();
    }

    public void onMouseDown() {
        mouseDown = true;
    }

    public void onMouseUp() {
        mouseDown = false;
    }

    /**
     * @param winX the x coordinate in window coordinates/pixels
     * @param winY the y coordinate in window coordinates/pixels
     */
    public void onMouseMove(int winX, int winY) {
        if (mouseDown) {
            invviewproj.unprojectInv(v.set(winX, winY, 0.0f), vp, v);
            float x0 = v.x, y0 = v.y;
            invviewproj.unprojectInv(v.set(mouseX, mouseY, 0.0f), vp, v);
            float x1 = v.x, y1 = v.y;
            view.translate(x0 - x1, y0 - y1, 0.0f);
            update();
        }
        mouseX = winX;
        mouseY = winY;
    }

    /**
     * @param scale the scale factor. &lt; 1.0 to zoom out; &gt; 1.0 to zoom in
     */
    public void zoom(float scale) {
        float x = (mouseX / vp[2] * 2.0f - 1.0f) * (vp[2] / vp[3]);
        float y = mouseY / vp[3] * 2.0f - 1.0f;
        view.scaleAroundLocal(scale, x, y, 0);
        update();
    }

    /**
     * @param dest contains the view rectangle as {x: minX, y: minY, z: width, w: height}
     * @return dest
     */
    public Vector4f viewRect(Vector4f dest) {
        invviewproj.transform(-1, -1, 0, 1, dest);
        float nX = dest.x, nY = dest.y;
        invviewproj.transform(+1, +1, 0, 1, dest);
        float pX = dest.x, pY = dest.y;
        dest.x = nX; // <- the minimum x coordinate
        dest.y = nY; // <- the minimum y coordinate
        dest.z = pX - nX; // <- the visible width
        dest.w = pY - nY; // <- the visible height
        return dest;
    }

}
