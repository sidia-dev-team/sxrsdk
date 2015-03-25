/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.gearvrf;

import java.util.Iterator;

import org.gearvrf.GVRMaterial.GVRShaderType;
import org.gearvrf.GVRMaterial.GVRShaderType.Unlit;

/**
 * One of the key GVRF classes: a scene object.
 * 
 * Every scene object has a {@linkplain #getTransform() location}, and can have
 * {@linkplain #children() children}. An invisible scene object can be used to
 * move a set of scene as a unit, preserving their relative geometry. Invisible
 * scene objects don't need any {@linkplain GVRSceneObject#getRenderData()
 * render data.}
 * 
 * <p>
 * Visible scene objects must have render data
 * {@linkplain GVRSceneObject#attachRenderData(GVRRenderData) attached.} Each
 * {@link GVRRenderData} has a {@link GVRMesh GL mesh} that defines its
 * geometry, and a {@link GVRMaterial} that defines its surface.
 */
public class GVRSceneObject extends GVRHybridObject {
    /**
     * Constructs an empty scene object with a default {@link GVRTransform
     * transform}.
     * 
     * @param gvrContext
     *            current {@link GVRContext}
     */
    public GVRSceneObject(GVRContext gvrContext) {
        super(gvrContext, NativeSceneObject.ctor());
        attachTransform(new GVRTransform(getGVRContext()));
    }

    /**
     * Constructs a scene object with an arbitrarily complex mesh.
     * 
     * @param gvrContext
     *            current {@link GVRContext}
     * @param mesh
     *            a {@link GVRMesh} - usually generated by one of the
     *            {@link GVRContext#loadMesh(String)} methods, or
     *            {@link GVRContext#createQuad(float, float)}
     */
    public GVRSceneObject(GVRContext gvrContext, GVRMesh mesh) {
        this(gvrContext);
        GVRRenderData renderData = new GVRRenderData(gvrContext);
        attachRenderData(renderData);
        renderData.setMesh(mesh);
    }

    /**
     * Constructs a rectangular scene object, whose geometry is completely
     * specified by the width and height.
     * 
     * @param gvrContext
     *            current {@link GVRContext}
     * @param width
     *            the scene object's width
     * @param height
     *            the scene object's height
     */
    public GVRSceneObject(GVRContext gvrContext, float width, float height) {
        this(gvrContext, gvrContext.createQuad(width, height));
    }

    /**
     * The base texture constructor: Constructs a scene object with
     * {@linkplain GVRMesh an arbitrarily complex geometry} that uses a specific
     * shader to display a {@linkplain GVRTexture texture.}
     * 
     * @param gvrContext
     *            current {@link GVRContext}
     * @param mesh
     *            a {@link GVRMesh} - usually generated by one of the
     *            {@link GVRContext#loadMesh(String)} methods, or
     *            {@link GVRContext#createQuad(float, float)}
     * @param texture
     *            a {@link GVRTexture}
     * @param shaderId
     *            a specific shader Id - see {@link GVRShaderType} and
     *            {@link GVRMaterialShaderManager}
     * 
     */
    public GVRSceneObject(GVRContext gvrContext, GVRMesh mesh,
            GVRTexture texture, GVRMaterialShaderId shaderId) {
        this(gvrContext, mesh);

        GVRMaterial material = new GVRMaterial(gvrContext, shaderId);
        material.setMainTexture(texture);
        getRenderData().setMaterial(material);
    }

    private static final GVRMaterialShaderId STANDARD_SHADER = GVRShaderType.Unlit.ID;

    /**
     * Constructs a scene object with {@linkplain GVRMesh an arbitrarily complex
     * geometry} that uses the standard {@linkplain Unlit 'unlit shader'} to
     * display a {@linkplain GVRTexture texture.}
     * 
     * 
     * @param gvrContext
     *            current {@link GVRContext}
     * @param mesh
     *            a {@link GVRMesh} - usually generated by one of the
     *            {@link GVRContext#loadMesh(String)} methods, or
     *            {@link GVRContext#createQuad(float, float)}
     * @param texture
     *            a {@link GVRTexture}
     */
    public GVRSceneObject(GVRContext gvrContext, GVRMesh mesh,
            GVRTexture texture) {
        this(gvrContext, mesh, texture, STANDARD_SHADER);
    }

    /**
     * Create a standard, rectangular texture object, using a non-default shader
     * to apply complex visual affects.
     * 
     * @param gvrContext
     *            current {@link GVRContext}
     * @param width
     *            the rectangle's width
     * @param height
     *            the rectangle's height
     * @param texture
     *            a {@link GVRTexture}
     * @param shaderId
     *            a specific shader Id
     */
    public GVRSceneObject(GVRContext gvrContext, float width, float height,
            GVRTexture texture, GVRMaterialShaderId shaderId) {
        this(gvrContext, gvrContext.createQuad(width, height), texture,
                shaderId);
    }

    /**
     * Constructs a 2D, rectangular scene object that uses the standard
     * {@linkplain Unlit 'unlit shader'} to display a {@linkplain GVRTexture
     * texture.}
     * 
     * @param gvrContext
     *            current {@link GVRContext}
     * @param width
     *            the rectangle's width
     * @param height
     *            the rectangle's height
     * @param texture
     *            a {@link GVRTexture}
     */
    public GVRSceneObject(GVRContext gvrContext, float width, float height,
            GVRTexture texture) {
        this(gvrContext, width, height, texture, STANDARD_SHADER);
    }

    private GVRSceneObject(GVRContext gvrContext, long ptr) {
        super(gvrContext, ptr);
    }

    static GVRSceneObject factory(GVRContext gvrContext, long ptr) {
        GVRHybridObject wrapper = wrapper(ptr);
        return wrapper == null ? new GVRSceneObject(gvrContext, ptr)
                : (GVRSceneObject) wrapper;
    }

    @Override
    protected final boolean registerWrapper() {
        return true;
    }

    /**
     * Get the (optional) name of the object.
     * 
     * @return The name of the object. If no name has been assigned, the
     *         returned string will be empty.
     */
    public String getName() {
        return NativeSceneObject.getName(getPtr());
    }

    /**
     * Set the (optional) name of the object.
     * 
     * Scene object names are not needed: they are only for the application's
     * convenience.
     * 
     * @param name
     *            Name of the object.
     */
    public void setName(String name) {
        NativeSceneObject.setName(getPtr(), name);
    }

    /**
     * Replace the current {@link GVRTransform transform}
     * 
     * @param transform
     *            New transform.
     */
    void attachTransform(GVRTransform transform) {
        NativeSceneObject.attachTransform(getPtr(), transform.getPtr());
    }

    /**
     * Remove the object's {@link GVRTransform transform}. After this call, the
     * object will have no transformations associated with it.
     */
    void detachTransform() {
        NativeSceneObject.detachTransform(getPtr());
        ;
    }

    /**
     * Get the {@link GVRTransform}.
     * 
     * A {@link GVRTransform} encapsulates a 4x4 matrix that specifies how to
     * render the {@linkplain GVRMesh GL mesh:} transform methods let you move,
     * rotate, and scale your scene object.
     * 
     * @return The current {@link GVRTransform transform}. If no transform is
     *         currently attached to the object, returns {@code null}.
     */
    public GVRTransform getTransform() {
        long ptr = NativeSceneObject.getTransform(getPtr());
        return ptr == 0 ? null : GVRTransform.factory(getGVRContext(), ptr);
    }

    /**
     * Attach {@linkplain GVRRenderData rendering data} to the object.
     * 
     * If other rendering data is currently attached, it is replaced with the
     * new data. {@link GVRRenderData} contains the GL mesh, the texture, the
     * shader id, and various shader constants.
     * 
     * @param renderData
     *            New rendering data.
     */
    public void attachRenderData(GVRRenderData renderData) {
        NativeSceneObject.attachRenderData(getPtr(), renderData.getPtr());
    }

    /**
     * Detach the object's current {@linkplain GVRRenderData rendering data}.
     * 
     * An object with no {@link GVRRenderData} is not visible.
     */
    public void detachRenderData() {
        NativeSceneObject.detachRenderData(getPtr());
    }

    /**
     * Get the current {@link GVRRenderData}.
     * 
     * @return The current {@link GVRRenderData rendering data}. If no rendering
     *         data is currently attached to the object, returns {@code null}.
     */
    public GVRRenderData getRenderData() {
        long ptr = NativeSceneObject.getRenderData(getPtr());
        return ptr == 0 ? null : GVRRenderData.factory(getGVRContext(), ptr);
    }

    /**
     * Attach a new {@link GVRCamera camera} to the object.
     * 
     * If another camera is currently attached, it is replaced with the new one.
     * 
     * @param camera
     *            New camera.
     */
    public void attachCamera(GVRCamera camera) {
        NativeSceneObject.attachCamera(getPtr(), camera.getPtr());
    }

    /**
     * Detach the object's current {@link GVRCamera camera}.
     */
    public void detachCamera() {
        NativeSceneObject.detachCamera(getPtr());
    }

    /**
     * Get the {@link GVRCamera} attached to the object.
     * 
     * @return The {@link GVRCamera camera} attached to the object. If no camera
     *         is currently attached, returns {@code null}.
     */
    public GVRCamera getCamera() {
        long ptr = NativeSceneObject.getCamera(getPtr());
        if (ptr == 0) {
            return null;
        } else {
            return new GVRCamera(getGVRContext(), ptr);
        }
    }

    /**
     * Attach a new {@linkplain GVRCameraRig camera rig.}
     * 
     * If another camera rig is currently attached, it is replaced with the new
     * one.
     * 
     * @param cameraRig
     *            New camera rig.
     */
    public void attachCameraRig(GVRCameraRig cameraRig) {
        NativeSceneObject.attachCameraRig(getPtr(), cameraRig.getPtr());
    }

    /**
     * Detach the object's current {@link GVRCameraRig camera rig}.
     */
    public void detachCameraRig() {
        NativeSceneObject.detachCameraRig(getPtr());
    }

    /**
     * Get the attached {@link GVRCameraRig}
     * 
     * @return The {@link GVRCameraRig camera rig} attached to the object. If no
     *         camera rig is currently attached, returns {@code null}.
     */
    public GVRCameraRig getCameraRig() {
        long ptr = NativeSceneObject.getCameraRig(getPtr());
        return ptr == 0 ? null : GVRCameraRig.factory(getGVRContext(), ptr);
    }

    /**
     * Attach a new {@link GVREyePointeeHolder} to the object.
     * 
     * If another {@link GVREyePointeeHolder} is currently attached, it is
     * replaced with the new one.
     * 
     * @param eyePointeeHolder
     *            New {@link GVREyePointeeHolder}.
     */
    public void attachEyePointeeHolder(GVREyePointeeHolder eyePointeeHolder) {
        NativeSceneObject.attachEyePointeeHolder(getPtr(),
                eyePointeeHolder.getPtr());
    }

    /**
     * Detach the object's current {@link GVREyePointeeHolder}.
     */
    public void detachEyePointeeHolder() {
        NativeSceneObject.detachEyePointeeHolder(getPtr());
    }

    /**
     * Get the attached {@link GVREyePointeeHolder}
     * 
     * @return The {@link GVREyePointeeHolder} attached to the object. If no
     *         {@link GVREyePointeeHolder} is currently attached, returns
     *         {@code null}.
     */
    public GVREyePointeeHolder getEyePointeeHolder() {
        long ptr = NativeSceneObject.getEyePointeeHolder(getPtr());
        return ptr == 0 ? null : GVREyePointeeHolder.factory(getGVRContext(),
                ptr);
    }

    /**
     * Get the {@linkplain GVRSceneObject parent object.}
     * 
     * If the object has been {@link #addChildObject(GVRSceneObject) added as a
     * child} to another {@link GVRSceneObject}, returns that object. Otherwise,
     * returns {@code null}.
     * 
     * @return The parent {@link GVRSceneObject} or {@code null}.
     */
    public GVRSceneObject getParent() {
        long ptr = NativeSceneObject.getParent(getPtr());
        return ptr == 0 || NativeHybridObject.getNativePointer(ptr) == 0 ? null
                : GVRSceneObject.factory(getGVRContext(), ptr);
    }

    /**
     * Add {@code child} as a child of this object.
     * 
     * @param child
     *            {@link GVRSceneObject Object} to add as a child of this
     *            object.
     */
    public void addChildObject(GVRSceneObject child) {
        NativeSceneObject.addChildObject(getPtr(), child.getPtr());
    }

    /**
     * Remove {@code child} as a child of this object.
     * 
     * @param child
     *            {@link GVRSceneObject Object} to remove as a child of this
     *            object.
     */
    public void removeChildObject(GVRSceneObject child) {
        NativeSceneObject.removeChildObject(getPtr(), child.getPtr());
    }

    /**
     * Get the number of child objects.
     * 
     * @return Number of {@link GVRSceneObject objects} added as children of
     *         this object.
     */
    public int getChildrenCount() {
        return NativeSceneObject.getChildrenCount(getPtr());
    }

    /**
     * Get the child object at {@code index}.
     * 
     * @param index
     *            Position of the child to get.
     * @return {@link GVRSceneObject Child object}.
     * 
     * @throws {@link java.lang.IndexOutOfBoundsException} if there is no child
     *         at that position.
     */
    public GVRSceneObject getChildByIndex(int index) {
        long ptr = NativeSceneObject.getChildByIndex(getPtr(), index);
        if (ptr == 0) {
            throw new IndexOutOfBoundsException("Index: " + index
                    + " Children count: " + getChildrenCount());
        } else {
            return GVRSceneObject.factory(getGVRContext(), ptr);
        }
    }

    /**
     * As an alternative to calling {@link #getChildrenCount()} then repeatedly
     * calling {@link #getChildByIndex(int)}, you can
     * 
     * <pre>
     * for (GVRSceneObject child : parent.children()) {
     * }
     * </pre>
     * 
     * @return An {@link Iterable}, so you can use Java's enhanced for loop
     */
    public Iterable<GVRSceneObject> children() {
        return new Children(this);
    }

    private static class Children implements Iterable<GVRSceneObject>,
            Iterator<GVRSceneObject> {

        private final GVRSceneObject object;
        private int index;

        private Children(GVRSceneObject object) {
            this.object = object;
            this.index = 0;
        }

        @Override
        public Iterator<GVRSceneObject> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            return index < object.getChildrenCount();
        }

        @Override
        public GVRSceneObject next() {
            return object.getChildByIndex(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}

class NativeSceneObject {
    public static native long ctor();

    public static native String getName(long sceneObject);

    public static native void setName(long sceneObject, String name);

    public static native void attachTransform(long sceneObject, long transform);

    public static native void detachTransform(long sceneObject);

    public static native long getTransform(long sceneObject);

    public static native void attachRenderData(long sceneObject, long renderData);

    public static native void detachRenderData(long sceneObject);

    public static native long getRenderData(long sceneObject);

    public static native void attachCamera(long sceneObject, long camera);

    public static native void detachCamera(long sceneObject);

    public static native long getCamera(long sceneObject);

    public static native void attachCameraRig(long sceneObject, long cameraRig);

    public static native void detachCameraRig(long sceneObject);

    public static native long getCameraRig(long sceneObject);

    public static native void attachEyePointeeHolder(long sceneObject,
            long eyePointeeHolder);

    public static native void detachEyePointeeHolder(long sceneObject);

    public static native long getEyePointeeHolder(long sceneObject);

    public static native long getParent(long sceneObject);

    public static native long setParent(long sceneObject, long parent);

    public static native void addChildObject(long sceneObject, long child);

    public static native void removeChildObject(long sceneObject, long child);

    public static native int getChildrenCount(long sceneObject);

    public static native long getChildByIndex(long sceneObject, int index);
}
