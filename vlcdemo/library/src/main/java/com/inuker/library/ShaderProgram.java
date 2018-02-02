package com.inuker.library;

import android.content.Context;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by liwentian on 17/6/22.
 */

public class ShaderProgram {

    public final String TAG = getClass().getSimpleName();

    protected final int program;

    protected final Context context;

    protected int width, height;

    protected ShaderProgram(Context context, int vertexId, int fragId, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;
        program = ShaderHelper.buildProgram(ResourceUtils.readText(context, vertexId),
                ResourceUtils.readText(context, fragId));
    }

    public void useProgram() {
        glUseProgram(program);
    }
}
