package com.nao.virtualnaocontrol;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private static final String TAG = "OCVSample::Activity";

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
				Log.i(TAG, "OpenCV loaded successfully");
				// Load native library after(!) OpenCV initialization
				// System.loadLibrary("detection_based_tracker");
				FeatureDetector detector = FeatureDetector
						.create(FeatureDetector.FAST);

				Mat object = Highgui.imread(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/naoObject.png");
				Log.i(TAG, "Object read successfully");
				Mat scene = Highgui.imread(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/scene.png");
				Log.i(TAG, "Scene read successfully");
				MatOfKeyPoint objectKeyPoint = new MatOfKeyPoint();
				MatOfKeyPoint sceneKeyPoint = new MatOfKeyPoint();

				detector.detect(object, objectKeyPoint);
				detector.detect(scene, sceneKeyPoint);

				Log.i(TAG, "Detection of scene and object done !");

				DescriptorExtractor extractor = DescriptorExtractor
						.create(DescriptorExtractor.BRIEF);
				Mat descriptorObject = new Mat();
				Mat descriptorScene = new Mat();
				extractor.compute(object, objectKeyPoint, descriptorObject);
				extractor.compute(scene, sceneKeyPoint, descriptorScene);

				Log.i(TAG, "Descriptor created ");

				DescriptorMatcher matcher = DescriptorMatcher
						.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

				MatOfDMatch matches = new MatOfDMatch();
				matcher.match(descriptorObject, descriptorScene, matches);
				Log.i(TAG, "Image match done");
				Mat outImg = new Mat();

				Features2d.drawMatches(object, objectKeyPoint, scene,
						sceneKeyPoint, matches, outImg, Scalar.all(-1),
						Scalar.all(-1), new MatOfByte(),
						Features2d.NOT_DRAW_SINGLE_POINTS);

				Log.i(TAG, "matches drawing done");
				boolean res = Highgui.imwrite(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/out.png", outImg);

				if (res == true) {
					System.out.println("image save");
				} else {
					System.out.println("fail to save image");
				}

				// convert to bitmap:
				Bitmap bm = Bitmap.createBitmap(outImg.cols(), outImg.rows(),
						Bitmap.Config.ARGB_8888);
				Utils.matToBitmap(outImg, bm);

				// find the imageview and draw it!
				ImageView iv = (ImageView) findViewById(R.id.imageView1);
				iv.setImageBitmap(bm);

				break;
			default:
				super.onManagerConnected(status);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				mLoaderCallback);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
