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

    protected ShaderProgram(Context context, int vertexId, int fragId) {
        this.context = context;
        program = ShaderHelper.buildProgram(ResourceUtils.readText(context, vertexId),
                ResourceUtils.readText(context, fragId));
    }

    public void useProgram() {
        glUseProgram(program);
    }
}
