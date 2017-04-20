package org.joml.camera;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Allows to control an orthographic camera with the mouse via panning and zooming.
 * <p>
 * Call the following methods:
 * <ul>
 * <li>{@link #setSize(int, int)} when the window/control size changes
 * <li>{@link #onMouseDown(int)} when further calls to {@link #onMouseMove(int, int)} should pan/rotate the view
 * <li>{@link #onMouseMove(int, int)} everytime the mouse moves
 * <li>{@link #onMouseUp(int)} when panning/rotating should stop
 * <li>{@link #zoom(float)} to zoom in/out
 * <li>{@link #viewproj()} to obtain the current view-projection matrix
 * <li>{@link #center(float, float)} to center the view onto the given coordinate
 * </ul>
 * 
 * @author Kai Burjack
 */
public class OrthoCameraControl {

    public static int MOUSE_LEFT = 0;
    public static int MOUSE_RIGHT = 1;
    public static int MOUSE_CENTER = 2;

    private Matrix4f view = new Matrix4f();
    private Matrix4f viewproj = new Matrix4f();
    private Matrix4f invviewproj = new Matrix4f();
    private int[] vp = new int[4];
    private float mouseX, mouseY;
    private float mouseDownX, mouseDownY;
    private boolean[] mouseDown = new boolean[3];
    private Vector3f v = new Vector3f();

    private float minRotateWinDistance2 = 100.0f * 100.0f;

    /**
     * @param extents
     *            the initial extents in world coordinates
     */
    public OrthoCameraControl(float extents) {
        view.setOrtho2D(-extents, extents, -extents, extents);
        update();
    }

    /**
     * @param minRotateWinDistance
     *            the minimum distance in window coordinates/pixels between
     *            the mouse down position and the current mouse position so
     *            that rotation is allowed
     */
    public void setMinRotateWinDistance(float minRotateWinDistance) {
        this.minRotateWinDistance2 = minRotateWinDistance * minRotateWinDistance;
    }

    /**
     * @param width
     *            the width of the control/window in window coordinates/pixels
     * @param height
     *            the height of the control/window in window coordinates/pixels
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
     * @param x
     *            the x coordiante of the point to center on in world coordinates
     * @param y
     *            the y coordiante of the point to center on in world coordinates
     */
    public void center(float x, float y) {
        view.setTranslation(0, 0, 0).translate(-x, -y, 0);
        update();
    }

    public void onMouseDown(int button) {
        mouseDownX = mouseX;
        mouseDownY = mouseY;
        mouseDown[button] = true;
        if (button == MOUSE_CENTER) {
            /* Reset rotation with mouse position as center */
            view.positiveX(v).negate();
            float ang = (float) Math.atan2(v.y, v.x);
            Vector3f ndc = ndc(mouseDownX, mouseDownY);
            view.translateLocal(-ndc.x, -ndc.y, 0.0f)
                .rotateLocal(ang, 0, 0, 1)
                .translateLocal(ndc.x, ndc.y, 0.0f);
            update();
        }
    }

    public void onMouseUp(int button) {
        mouseDown[button] = false;
    }

    /**
     * @param winX
     *            the x coordinate in window coordinates/pixels
     * @param winY
     *            the y coordinate in window coordinates/pixels
     */
    public void onMouseMove(int winX, int winY) {
        if (mouseDown[MOUSE_LEFT]) {
            /* Move */
            invviewproj.unprojectInv(v.set(winX, winY, 0.0f), vp, v);
            float x0 = v.x, y0 = v.y;
            invviewproj.unprojectInv(v.set(mouseX, mouseY, 0.0f), vp, v);
            float x1 = v.x, y1 = v.y;
            view.translate(x0 - x1, y0 - y1, 0.0f);
            update();
        } else if (mouseDown[MOUSE_RIGHT]) {
            /* Check if rotation is possible */
            float dx = winX - mouseDownX;
            float dy = winY - mouseDownY;
            if (dx * dx + dy * dy > minRotateWinDistance2) {
                /* Rotate */
                float dx0 = winX - mouseDownX, dy0 = winY - mouseDownY;
                float dx1 = mouseX - mouseDownX, dy1 = mouseY - mouseDownY;
                float ang = (float) Math.atan2(dx1 * dy0 - dy1 * dx0, dx1 * dx0 + dy1 * dy0);
                Vector3f ndc = ndc(mouseDownX, mouseDownY);
                view.translateLocal(-ndc.x, -ndc.y, 0.0f)
                    .rotateLocal(ang, 0, 0, 1)
                    .translateLocal(ndc.x, ndc.y, 0.0f);
                update();
            }
        }
        mouseX = winX;
        mouseY = winY;
    }

    private Vector3f ndc(float winX, float winY) {
        float x = (winX / vp[2] * 2.0f - 1.0f) * (vp[2] / vp[3]);
        float y = winY / vp[3] * 2.0f - 1.0f;
        return v.set(x, y, 0.0f);
    }

    /**
     * @param scale
     *            the scale factor. &lt; 1.0 to zoom out; &gt; 1.0 to zoom in
     */
    public void zoom(float scale) {
        Vector3f ndc = ndc(mouseX, mouseY);
        view.scaleAroundLocal(scale, ndc.x, ndc.y, 0);
        update();
    }

    /**
     * @param dest
     *            contains the view rectangle as {x: minX, y: minY, z: maxX, w: maxY}
     * @return dest
     */
    public Vector4f viewRect(Vector4f dest) {
        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < 4; i++) {
            float x = ((i & 1) << 1) - 1.0f;
            float y = (((i >>> 1) & 1) << 1) - 1.0f;
            invviewproj.transformPosition(v.set(x, y, 0.0f));
            minX = minX < v.x ? minX : v.x;
            minY = minY < v.y ? minY : v.y;
            maxX = maxX > v.x ? maxX : v.x;
            maxY = maxY > v.y ? maxY : v.y;
        }
        return dest.set(minX, minY, maxX, maxY);
    }

}
