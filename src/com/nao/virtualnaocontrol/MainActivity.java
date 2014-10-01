package com.nao.virtualnaocontrol;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import com.nao.opengl.*;

public class MainActivity extends Activity {
	// Attributs
	private GLSurfaceView glView;
	
    protected void onCreate(Bundle savedInstanceState) {  
    	super.onCreate(savedInstanceState);
    	glView = new MyGLSurfaceView(this);
        setContentView(glView);
    }

    // Callback appell� quand l'application est en arri�re plan
    protected void onPause() {
       super.onPause();
       glView.onPause();
    }
    
    // Callback appell� quand l'application revient au premier plan
    protected void onResume() {
       super.onResume();
       glView.onResume();
    }
}
