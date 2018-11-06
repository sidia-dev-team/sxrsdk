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

package com.samsungxr;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;

import com.samsungxr.SXRShaderTemplate;
import com.samsungxr.SXRContext;
import com.samsungxr.utility.Log;

/**
 * Manages GearVRF shaders for rendering nodes.
 *
 * Get the singleton from {@link SXRContext#getMaterialShaderManager()}.
 */
public class SXRShaderManager extends SXRHybridObject
{
    SXRShaderManager(SXRContext gvrContext)
    {
        this(gvrContext, NativeShaderManager.ctor());
    }

    protected SXRShaderManager(SXRContext gvrContext, long ctor)
    {
        super(gvrContext, ctor);
    }

    public int addShader(String signature, String uniformDescriptor,
                         String textureDescriptor, String vertexDescriptor,
                         String vertexShader, String fragmentShader)
    {
        return NativeShaderManager.addShader(getNative(), signature,
                uniformDescriptor, textureDescriptor, vertexDescriptor,
                vertexShader, fragmentShader);
    }

    /**
     * Get the shader manager ID for a shader based on its signature.
     * <p>
     * The shader signature uniquely defines a native shader in the system.
     * The shader index returned uniquely identifies this shader to GearVRF.
     * </p>
     * @param signature shader signature generated by {@link SXRShaderTemplate}
     * @return integer shader ID
     */
    public int getShader(String signature)
    {
        return NativeShaderManager.getShader(getNative(), signature);
    }

    /**
     * Retrieves the Material Shader ID associated with the
     * given shader template class.
     *
     * A shader template is capable of generating multiple variants
     * from a single shader source. The exact vertex and fragment
     * shaders are generated by GearVRF based on the lights
     * being used and the material attributes. you may subclass
     * SXRShaderTemplate to create your own shader templates.
     *
     * @param shaderClass shader class to find (subclass of SXRShader)
     * @return SXRShaderId associated with that shader template
     * @see SXRShaderTemplate SXRShader
     */
    public SXRShaderId getShaderType(Class<? extends SXRShader> shaderClass)
    {
        SXRShaderId shaderId = mShaderTemplates.get(shaderClass);

        if (shaderId == null)
        {
            SXRContext ctx = getSXRContext();
            shaderId = new SXRShaderId(shaderClass);
            mShaderTemplates.put(shaderClass, shaderId);
            shaderId.getTemplate(ctx);
        }
        return shaderId;
    }

    void addShaderID(SXRShaderId shaderID)
    {
        getShaderType(shaderID.ID);
    }

    /**
     * Make a string with the shader layout for a uniform block
     * with a given descriptor. The format of the descriptor is
     * the same as for a @{link SXRShaderData} - a string of
     * types and names of each field.
     * <p>
     * This function will return a Vulkan shader layout if the
     * Vulkan renderer is being used. Otherwise, it returns
     * an OpenGL layout.
     * @param descriptor string with types and names of each field
     * @param blockName  name of uniform block
     * @param useUBO     true to output uniform buffer layout, false for push constants
     * @return string with shader declaration
     */
    static String makeLayout(String descriptor, String blockName, boolean useUBO)
    {
        return NativeShaderManager.makeLayout(descriptor, blockName, useUBO);
    }

    /**
     * Maps the shader template class to the instance of the template.
     * Only one shader template of each class is necessary since
     * shaders are global.
     */
    protected Map<Class<? extends SXRShader>, SXRShaderId> mShaderTemplates = new HashMap<Class<? extends SXRShader>, SXRShaderId>();
}

class NativeShaderManager {
    static native long ctor();

    static native int addShader(long shaderManager, String signature,
                                String uniformDescriptor, String textureDescriptor, String vertexDescriptor,
                                String vertexShader, String fragmentShader);
    static native void bindCalcMatrix(long shaderManager, int nativeShader, Class<? extends SXRShader> javaShaderClass);
    static native int getShader(long shaderManager, String signature);
    static native String makeLayout(String descriptor, String blockName, boolean useUBO);
}
