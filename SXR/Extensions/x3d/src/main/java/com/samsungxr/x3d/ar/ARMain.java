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

package com.samsungxr.x3d.ar;

import com.samsungxr.SXRAssetLoader;
import com.samsungxr.SXRContext;
import com.samsungxr.SXREventListeners;
import com.samsungxr.SXRMaterial;
import com.samsungxr.SXRPicker;
import com.samsungxr.SXRRenderData;
import com.samsungxr.SXRRenderPass;
import com.samsungxr.SXRScene;
import com.samsungxr.SXRNode;
import com.samsungxr.SXRShaderId;
import com.samsungxr.SXRTexture;
import com.samsungxr.SXRTextureParameters;
import com.samsungxr.SXRTransform;
import com.samsungxr.mixedreality.SXRMixedReality;
import com.samsungxr.mixedreality.SXRAnchor;
import com.samsungxr.mixedreality.SXRHitResult;

import com.samsungxr.mixedreality.SXRPlane;
import com.samsungxr.mixedreality.SXRTrackingState;
import com.samsungxr.mixedreality.IAnchorEventsListener;
import com.samsungxr.mixedreality.IPlaneEventsListener;
import com.samsungxr.nodes.SXRCylinderNode;
import com.samsungxr.x3d.ShaderSettings;
import com.samsungxr.x3d.node.Cone;
import com.samsungxr.x3d.node.Geometry;
import com.samsungxr.x3d.node.ImageTexture;
import com.samsungxr.x3d.node.Material;
import com.samsungxr.x3d.node.MovieTexture;
import com.samsungxr.x3d.node.Proto;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import com.samsungxr.utility.Log;
import com.samsungxr.x3d.node.TextureTransform;


public class ARMain {
    private static String TAG = "ARMain";
    private static int MAX_VIRTUAL_OBJECTS = 20;

    private SXRContext mSXRContext;
    private SXRScene mainScene;
    private SXRMixedReality mixedReality;
    private SampleHelper helper;
    private TouchHandler mTouchHandler;

    private List<SXRAnchor> mVirtualObjects;
    private int mVirtObjCount = 0;
    private ArrayList<Proto> mProtos;
    private ShaderSettings mShaderSettings = null;
    private SXRShaderId mX3DShader;


    //@Override
    public ARMain(SXRContext sxrContext, ArrayList<Proto> protos,
                  ShaderSettings shaderSettings, SXRShaderId x3DShader) {
        Log.e("X3DDBG", "ARMain constructor BGN.");

        mSXRContext = sxrContext;
        mProtos = protos;
        mainScene = mSXRContext.getMainScene();
        helper = new SampleHelper();
        mTouchHandler = new TouchHandler();
        Log.e("X3DDBG", "ARMain constructor 2b.");
        mVirtualObjects = new ArrayList<>() ;
        mVirtObjCount = 0;
        mShaderSettings = shaderSettings;
        mX3DShader = x3DShader;
        Log.e("X3DDBG", "ARMain constructor 2c.");
/*
        helper.initCursorController(gvrContext, mTouchHandler);

        Log.e("X3DDBG", "ARMain constructor 3.");

        try {
            mixedReality = new GVRMixedReality(gvrContext, mainScene);
        }
        catch (Exception e) {
            Log.e("X3DDBG", "new GVRMixedReality() exception: " + e);
        }
        Log.e("X3DDBG", "ARMain constructor 4.");
        mixedReality.registerPlaneListener(planeEventsListener);
        Log.e("X3DDBG", "ARMain constructor 5.");

        mixedReality.registerAnchorListener(anchorEventsListener);
        */
      //  Log.e("X3DDBG", "ARMain constructor 6.");
     //   mixedReality.resume();
        Log.e("X3DDBG", "ARMain constructor END.");

    }

    public void resume() {
        Log.e("X3DDBG", "ARMain resume() BGN.");

        helper.initCursorController(mSXRContext, mTouchHandler);

        Log.e("X3DDBG", "ARMain resume() end initCursorController().");

        try {
            mixedReality = new SXRMixedReality(mSXRContext, mainScene);
        }
        catch (Exception e) {
            Log.e("X3DDBG", "new GVRMixedReality() exception: " + e);
        }
        Log.e("X3DDBG", "ARMain resume() go  registerPlaneListener().");
        mixedReality.registerPlaneListener(planeEventsListener);
        Log.e("X3DDBG", "ARMain resume() end registerPlaneListener(), go registerAnchorListener().");

        mixedReality.registerAnchorListener(anchorEventsListener);
        Log.e("X3DDBG", "ARMain resume() end registerAnchorListener(). Go resume()");
        //  Log.e("X3DDBG", "ARMain constructor 6.");
        //   mixedReality.resume();
        //Log.e("X3DDBG", "ARMain constructor END.");
        mixedReality.resume();
        Log.e("X3DDBG", "ARMain resume() END.");
    }

/*
    @Override
    public void onStep() {
        super.onStep();
        for (GVRAnchor anchor: mVirtualObjects) {
            for (GVRSceneObject obj: anchor.getChildren()) {
                ((VirtualObject)obj).reactToLightEnvironment(
                        mixedReality.getLightEstimate().getPixelIntensity());
            }
        }
    }
*/

    private IPlaneEventsListener planeEventsListener = new IPlaneEventsListener() {
        @Override
        public void onPlaneDetection(SXRPlane sxrPlane) {
            //gvrPlane.setSceneObject(helper.createQuadPlane(getGVRContext()));
            Log.e("X3DDBG", "ARMain onPlaneDetection.");
            sxrPlane.setNode(helper.createQuadPlane(mSXRContext));
            mainScene.addNode(sxrPlane);
        }

        @Override
        public void onPlaneStateChange(SXRPlane sxrPlane, SXRTrackingState gvrTrackingState) {
            if (gvrTrackingState != SXRTrackingState.TRACKING) {
                sxrPlane.setEnable(false);
                Log.e("X3DDBG", "ARMain onPlaneStateChange: gvrPlane.setEnable(false).");
            }
            else {
                sxrPlane.setEnable(true);
                Log.e("X3DDBG", "ARMain onPlaneStateChange: gvrPlane.setEnable(true).");
            }
        }

        @Override
        public void onPlaneMerging(SXRPlane gvrPlane, SXRPlane gvrPlane1) {
        }
    };

    private IAnchorEventsListener anchorEventsListener = new IAnchorEventsListener() {
        @Override
        public void onAnchorStateChange(SXRAnchor sxrAnchor, SXRTrackingState sxrTrackingState) {
            Log.e("X3DDBG", "ARMain anchorEventsListener.");
            if (sxrTrackingState != SXRTrackingState.TRACKING) {
                sxrAnchor.setEnable(false);
            }
            else {
                sxrAnchor.setEnable(true);
            }
        }
    };

    public class TouchHandler extends SXREventListeners.TouchEvents {
        private SXRNode mDraggingObject = null;
        private float mHitX;
        private float mHitY;
        private float mYaw;
        private float mScale;

        private int cnt = 0;


        @Override
        public void onEnter(SXRNode sceneObj, SXRPicker.SXRPickedObject pickInfo) {
            super.onEnter(sceneObj, pickInfo);
            Log.e("X3DDBG", "ARMain TouchHandler onEnter()");

            if (sceneObj == mixedReality.getPassThroughObject() || mDraggingObject != null) {
                return;
            }

            ((VirtualObject)sceneObj).onPickEnter();
        }

        @Override
        public void onExit(SXRNode sceneObj, SXRPicker.SXRPickedObject pickInfo) {
            super.onExit(sceneObj, pickInfo);
            Log.e("X3DDBG", "ARMain TouchHandler onExit()");

            if (sceneObj == mixedReality.getPassThroughObject()) {
                if (mDraggingObject != null) {
                    ((VirtualObject) mDraggingObject).onPickExit();
                    mDraggingObject = null;
                }
                return;
            }

            if (mDraggingObject == null) {
                ((VirtualObject) sceneObj).onPickExit();
            }
        }

        @Override
        public void onTouchStart(SXRNode sceneObj, SXRPicker.SXRPickedObject pickInfo) {
            super.onTouchStart(sceneObj, pickInfo);
            Log.e("X3DDBG", "ARMain TouchHandler onTouchStart()");

            if (sceneObj == mixedReality.getPassThroughObject()) {
                return;
            }

            Log.e("X3DDBG", "   ARMain TouchHandler onTouchStart() 2");
            if (mDraggingObject == null) {
                Log.e("X3DDBG", "      ARMain TouchHandler onTouchStart() mDraggingObject == null");
                mDraggingObject = sceneObj;

                mYaw = sceneObj.getTransform().getRotationYaw();
                mScale = sceneObj.getTransform().getScaleX();

                mHitX = pickInfo.motionEvent.getX();
                mHitY = pickInfo.motionEvent.getY();

                Log.d(TAG, "onStartDragging");
                ((VirtualObject)sceneObj).onTouchStart();
            }
        }

        @Override
        public void onTouchEnd(SXRNode sceneObj, SXRPicker.SXRPickedObject pickInfo) {
            super.onTouchEnd(sceneObj, pickInfo);

            Log.e("X3DDBG", "ARMain TouchHandler onTouchEnd()");

            if (mDraggingObject != null) {
                Log.d(TAG, "onStopDragging");

                if (pickSceneObject(mDraggingObject) == null) {
                    ((VirtualObject) mDraggingObject).onPickExit();
                } else {
                    ((VirtualObject)mDraggingObject).onTouchEnd();
                }
                mDraggingObject = null;
            } else if (sceneObj == mixedReality.getPassThroughObject()) {
                onSingleTap(sceneObj, pickInfo);
            }
        }

        @Override
        public void onInside(SXRNode sceneObj, SXRPicker.SXRPickedObject pickInfo) {
            super.onInside(sceneObj, pickInfo);
            if ( (cnt % 10000) == 0) {
                Log.e("X3DDBG", "ARMain TouchHandler onInside(), cnt=" + (cnt / 10000));
            }
            cnt++;

            if (mDraggingObject == null) {
                return;
            } else {
                // get the current x,y hit location
                float hitLocationX = pickInfo.motionEvent.getX();
                float hitLocationY = pickInfo.motionEvent.getY();

                // find the diff from when we first touched down
                float diffX = hitLocationX - mHitX;
                float diffY = (hitLocationY - mHitY) / 100.0f;

                // when we move along X, calculate an angle to rotate the model around the Y axis
                float angle = mYaw + (diffX * 2);

                // when we move along Y, calculate how much to scale the model
                float scale = mScale + (diffY);
                if(scale < 0.1f) {
                    scale = 0.1f;
                }

                // set rotation and scale
                mDraggingObject.getTransform().setRotationByAxis(angle, 0.0f, 1.0f, 0.0f);
                mDraggingObject.getTransform().setScale(scale, scale, scale);
            }


            pickInfo = pickSceneObject(mixedReality.getPassThroughObject());
            if (pickInfo != null) {
                SXRHitResult gvrHitResult = mixedReality.hitTest(
                        mixedReality.getPassThroughObject(), pickInfo);

                if (gvrHitResult != null) {
                    mixedReality.updateAnchorPose((SXRAnchor)mDraggingObject.getParent(),
                            gvrHitResult.getPose());
                }
            }
        }

        private SXRPicker.SXRPickedObject pickSceneObject(SXRNode sceneObject) {
            Vector3f origin = new Vector3f();
            Vector3f direction = new Vector3f();
            Log.e("X3DDBG", "ARMain GVRPicker.GVRPickedObject pickSceneObject()");
            helper.getCursorController().getPicker().getWorldPickRay(origin, direction);

            return SXRPicker.pickNode(sceneObject, origin.x, origin.y, origin.z,
                    direction.x, direction.y, direction.z);
        }

        private void onSingleTap(SXRNode sceneObj, SXRPicker.SXRPickedObject collision) {
            Log.e("X3DDBG", "ARMain onSingleTap()");
            SXRHitResult gvrHitResult = mixedReality.hitTest(sceneObj, collision);
            VirtualObject andy = new VirtualObject(mSXRContext);

            if (gvrHitResult == null) {
                Log.e("X3DDBG", "   gvrHitResult == null");
                return;
            }

            Log.e("X3DDBG", "   addVirtualObject()");
            addVirtualObject(gvrHitResult.getPose(), andy);
        }
    }

    private void addVirtualObject(float[] pose, VirtualObject andy) {
        SXRAnchor anchor;
/*
        for (Proto proto : mProtos) {
            Log.e("X3DDBG", "ARMain addVirtualObject() proto array");

            Cone cone = proto.getGeometry().getCone();
            if ( cone != null ) {
                Log.e("X3DDBG", "   ARMain addVirtualObject() cone != null");

                Geometry geometryInstance = new Geometry();
                try {
                    Cone cloneCone = (Cone) cone.clone();
                    geometryInstance.setCone( cloneCone );
                }
                catch (CloneNotSupportedException ex) {
                    Log.e(TAG, "Proto <Cone> exception: " + ex);
                }
                catch (Exception ex) {
                    Log.e(TAG, "Proto <Cone> exception: " + ex);
                }

                proto.setGeometryInstance( geometryInstance );

                Log.e("X3DDBG", "   ARMain addVirtualObject() ck proto.getShape()");

                // Set the default material values
                if (proto.getShape() != null ) {
                    Log.e("X3DDBG", "   ARMain addVirtualObject() proto.getShape() != null");
                    //if (gvrRenderData == null) gvrRenderData = new SXRRenderData(gvrContext);
                    SXRRenderData sxrRenderData = new SXRRenderData(mSXRContext);
                    sxrRenderData.setAlphaToCoverage(true);
                    sxrRenderData.setRenderingOrder(SXRRenderData.SXRRenderingOrder.GEOMETRY);
                    sxrRenderData.setCullFace(SXRRenderPass.SXRCullFaceEnum.Back);
                    mShaderSettings.initializeTextureMaterial(new SXRMaterial(mSXRContext, mX3DShader));

                    if (proto.getShape().getAppearance() != null ) {
                        Material material = proto.getAppearance().getMaterial();
                        ImageTexture imageTexture = proto.getAppearance().getTexture();
                        TextureTransform textureTransform = proto.getAppearance().getTextureTransform();
                        MovieTexture movieTexture = proto.getAppearance().getMovieTexture();
                        if (material != null) {
                            mShaderSettings.ambientIntensity = material.getAmbientIntensity();
                            mShaderSettings.diffuseColor = material.getDiffuseColor();
                            mShaderSettings.emissiveColor = material.getEmissiveColor();
                            mShaderSettings.shininess = material.getShininess();
                            mShaderSettings.specularColor = material.getSpecularColor();
                            mShaderSettings.setTransparency(material.getTransparency());
                        }
                        */
                        /*
                        if ( imageTexture != null ) {
                            gvrTextureParameters = new SXRTextureParameters(mSXRContext);
                            gvrTextureParameters.setWrapSType(SXRTextureParameters.TextureWrapType.GL_REPEAT);
                            gvrTextureParameters.setWrapTType(SXRTextureParameters.TextureWrapType.GL_REPEAT);
                            gvrTextureParameters.setMinFilterType(SXRTextureParameters.TextureFilterType.GL_LINEAR_MIPMAP_NEAREST);

                            SXRTexture gvrTexture = new SXRTexture(mSXRContext, gvrTextureParameters);
                            SXRAssetLoader.TextureRequest request = new SXRAssetLoader.TextureRequest(assetRequest, gvrTexture, imageTexture.getUrl()[0]);

                            assetRequest.loadTexture(request);
                            mShaderSettings.setTexture(gvrTexture);
                        }
                        if (textureTransform != null ){
                            shaderSettings.setTextureCenter( textureTransform.getCenter() );
                            shaderSettings.setTextureRotation( textureTransform.getRotation() );
                            shaderSettings.setTextureScale( textureTransform.getScale() );
                            shaderSettings.setTextureTranslation( textureTransform.getTranslation() );
                        }
                        if (movieTexture != null ){
                            Log.e(TAG, "   <Proto> <MovieTexture> not currently supported.");
                            shaderSettings.movieTextures.add(movieTexture.getUrl()[0]);
                        }
                        */
                        /*

                    }
                    else {
                        Log.e(TAG, "Appearance missing from ProtoInstance");
                    }
                }
                else {
                    Log.e(TAG, "Shape missing from ProtoInstance");
                }
                Log.e("X3DDBG", "   ARMain addVirtualObject() Construct CONE BGN");
                SXRCylinderNode.CylinderParams params = new SXRCylinderNode.CylinderParams();
                params.BottomRadius = cone.getBottomRadius();
                params.TopRadius = 0;
                params.Height = cone.getHeight();
                params.HasBottomCap = cone.getBottom();
                params.HasTopCap = false;
                params.FacingOut = cone.getSolid();

                Log.e("X3DDBG", "   ARMain addVirtualObject() Construct CONE set Material");
                params.Material = new SXRMaterial(mSXRContext, mX3DShader);
                params.Material.setDiffuseColor(0, .5f, 1, 1);
                SXRCylinderNode sxrCylinderNode = new SXRCylinderNode(
                        mSXRContext, params);
                //SXRTransform sxrTransform = sxrCylinderNode.getTransform();
                //sxrTransform.setScale(.5f, .5f, .5f);
                //sxrTransform.setPositionZ( sxrTransform.getPositionZ()-1);
                //currentNode.addChildObject(gvrCylinderNode);
                Log.e("X3DDBG", "   ARMain addVirtualObject() Create Anchor BGN");
                for (int i =0; i < 16; i++) {
                    pose[i]=0;
                }
                pose[0] = 1; pose[5] = 1;pose[10] = 1;pose[15] = 1;
                pose[12] = -1.5f; pose[13] = 1.25f;pose[14] = -1.5f;
                Log.e("X3DDBG", "      pose[][]= [" + pose[ 0] + ", " + pose[ 4] + ", " + pose[ 8] + ", " + pose[12] + "]");
                Log.e("X3DDBG", "                [" + pose[ 1] + ", " + pose[ 5] + ", " + pose[ 9] + ", " + pose[13] + "]");
                Log.e("X3DDBG", "                [" + pose[ 2] + ", " + pose[ 6] + ", " + pose[10] + ", " + pose[14] + "]");
                Log.e("X3DDBG", "                [" + pose[ 3] + ", " + pose[ 7] + ", " + pose[11] + ", " + pose[15] + "]");
                //for (int i = 0; i < 4; i++ ) {
                //    Log.e("X3DDBG", "      pose[" + i*4 +"] to ["+(i*4+3) +"]= " + pose[i*4] + ", " + pose[i*4+1] + ", " + pose[i*4+2] + ", " + pose[i*4+3] + ", ");
                //}
                anchor = mixedReality.createAnchor(pose, sxrCylinderNode);
                Log.e("X3DDBG", "   ARMain addVirtualObject() add anchor");
                mVirtualObjects.add(anchor);
                Log.e("X3DDBG", "   ARMain addVirtualObject() anchor ADDED");

            }
        }
        */

        Log.e("X3DDBG", "ARMain addVirtualObject() mVirtObjCount=" + mVirtObjCount);
        Log.e("X3DDBG", "      pose[][]= [" + pose[ 0] + ", " + pose[ 4] + ", " + pose[ 8] + ", " + pose[12] + "]");
        Log.e("X3DDBG", "                [" + pose[ 1] + ", " + pose[ 5] + ", " + pose[ 9] + ", " + pose[13] + "]");
        Log.e("X3DDBG", "                [" + pose[ 2] + ", " + pose[ 6] + ", " + pose[10] + ", " + pose[14] + "]");
        Log.e("X3DDBG", "                [" + pose[ 3] + ", " + pose[ 7] + ", " + pose[11] + ", " + pose[15] + "]");
        if (mVirtObjCount < MAX_VIRTUAL_OBJECTS) {
             anchor = mixedReality.createAnchor(pose, andy);

            mainScene.addNode(anchor);
            Log.e("X3DDBG", "ARMain addVirtualObject() call to mVirtualObjects.add(anchor)");
            mVirtualObjects.add(anchor);
        }
        else {
            anchor = mVirtualObjects.get(mVirtObjCount % mVirtualObjects.size());
            mixedReality.updateAnchorPose(anchor, pose);
        }

        anchor.setName("id: " + mVirtObjCount);
        Log.d(TAG, "New virtual object " + anchor.getName());
        Log.e("X3DDBG", "ARMain addVirtualObject() New virtual object: " + anchor.getName());

        mVirtObjCount++;

    }
}