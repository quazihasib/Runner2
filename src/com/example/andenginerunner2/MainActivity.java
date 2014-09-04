package com.example.andenginerunner2;

import java.io.IOException;
import java.util.LinkedList;
import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.CameraScene;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.objectpool.ObjectsPool;
import com.example.objectpool.ProjectilesPool;
import com.example.objectpool.TargetsPool;
import com.example.objects.Controller;
import com.example.objects.Models;

public class MainActivity extends BaseGameActivity implements
		IOnSceneTouchListener 
{

	public static Camera mCamera;

	// This one is for the font
	public BitmapTextureAtlas mFontTexture;
	public Font mFont;
	public static ChangeableText score;

	// our object pools
	public static ProjectilesPool pPool;
	public static TargetsPool tPool;
	public static ObjectsPool oPool;

	public static LinkedList<Sprite> projectileLL;
	public static LinkedList<AnimatedSprite> targetLL;
	public static LinkedList<Sprite> projectilesToBeAdded;
	public static LinkedList<AnimatedSprite> TargetsToBeAdded;
	
	public static LinkedList<Sprite> objcetLL;
	public static LinkedList<Sprite> objectsToBeAdded;

	// this one is for all other textures
	public static  BitmapTextureAtlas mBitmapTextureAtlas;
	public static  BitmapTextureAtlas sheetBitmapTextureAtlas;
	public static  TextureRegion mProjectileTextureRegion;
	public static  TextureRegion mPausedTextureRegion;
	public static  TextureRegion mWinTextureRegion;
	public static  TextureRegion mFailTextureRegion;
	public static  TiledTextureRegion mTargetTextureRegion;
	public static  TiledTextureRegion mHeroTextureRegion;

	// the main scene for the game
	public static Scene mMainScene;
	public static AnimatedSprite hero;

	public static BitmapTextureAtlas mAutoParallaxBackgroundTexture;
	public static TextureRegion mParallaxLayer;

	private TextureRegion mParallaxLayerMid;
	
	// win/fail sprites
	public static Sprite winSprite;
	public static Sprite failSprite;

	public static Sound shootingSound;
	public static Music backgroundMusic;
	public boolean runningFlag = false;
	public boolean pauseFlag = false;
	public static CameraScene mPauseScene;
	public static CameraScene mResultScene;
	public static  int hitCount;
	public static  int hitCount1;
	public static final int maxScore = 10;

	public static Engine mEngine;
	
	public Models model;
	public Controller controller;

	@Override
	public Engine onLoadEngine() 
	{
		// TODO Auto-generated method stub
		// getting the device's screen size
		final Display display = getWindowManager().getDefaultDisplay();
		int cameraWidth = display.getWidth();
		int cameraHeight = display.getHeight();

		// setting up the camera [AndEngine's camera , not the one you take
		// pictures with]
		mCamera = new Camera(0, 0, cameraWidth, cameraHeight);
		mEngine = new Engine(new EngineOptions(true,
				ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
						cameraWidth, cameraHeight), mCamera)
				.setNeedsSound(true).setNeedsMusic(true));

		// enabling MultiTouch if available
		try 
		{
			if (MultiTouch.isSupported(this)) 
			{
				mEngine.setTouchController(new MultiTouchController());
			}
			else 
			{
				Toast.makeText(
						this,
						"Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)",
						Toast.LENGTH_LONG).show();
			}
		}
		catch (final MultiTouchException e) 
		{
			Toast.makeText(this,"Sorry your Android Version does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)",
					Toast.LENGTH_LONG).show();
		}

		return mEngine;
	}

	@Override
	public void onLoadResources()
	{
		// TODO Auto-generated method stub
		mAutoParallaxBackgroundTexture = new BitmapTextureAtlas(1024, 1024,
				TextureOptions.DEFAULT);

		// prepare a container for the image
		mBitmapTextureAtlas = new BitmapTextureAtlas(512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		// prepare a container for the font
		mFontTexture = new BitmapTextureAtlas(256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		sheetBitmapTextureAtlas = new BitmapTextureAtlas(2048, 512);

		// setting assets path for easy access
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		// loading the image inside the container

		mParallaxLayer = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mAutoParallaxBackgroundTexture, this,
						"background.png", 0, 0);
		
		mParallaxLayerMid = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
			mAutoParallaxBackgroundTexture, this, "parallax_background_layer_mid.png", 0, 669);

		mProjectileTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas, this,
						"projectile.png", 64, 0);

		mHeroTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(sheetBitmapTextureAtlas, this,
						"hero.png", 0, 212, 11, 1);

		mTargetTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(sheetBitmapTextureAtlas, this,
						"target.png", 0, 0, 3, 1);

		mPausedTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas, this, "paused.png",
						0, 64);
		mWinTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas, this, "win.png", 0,
						128);
		mFailTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(mBitmapTextureAtlas, this, "fail.png", 0,
						256);

		pPool = new ProjectilesPool(mProjectileTextureRegion);
		tPool = new TargetsPool(mTargetTextureRegion);
		oPool = new ObjectsPool(mWinTextureRegion);

		// preparing the font
		mFont = new Font(mFontTexture, Typeface.create(Typeface.DEFAULT,
				Typeface.BOLD), 40, true, Color.BLACK);

		// loading textures in the engine
		mEngine.getTextureManager().loadTexture(mBitmapTextureAtlas);
		mEngine.getTextureManager().loadTexture(mFontTexture);
		mEngine.getTextureManager().loadTexture(sheetBitmapTextureAtlas);
		mEngine.getTextureManager().loadTexture(mAutoParallaxBackgroundTexture);
		mEngine.getFontManager().loadFont(mFont);

		SoundFactory.setAssetBasePath("mfx/");
		try 
		{
			shootingSound = SoundFactory.createSoundFromAsset(
					mEngine.getSoundManager(), this, "pew_pew_lei.wav");
		} 
		catch (IllegalStateException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MusicFactory.setAssetBasePath("mfx/");

		try 
		{
			backgroundMusic = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), 
					this, "background_music.wav");
			backgroundMusic.setLooping(true);
		} 
		catch (IllegalStateException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Scene onLoadScene() 
	{
		// TODO Auto-generated method stub
		mEngine.registerUpdateHandler(new FPSLogger());

		// creating a new scene for the pause menu
		mPauseScene = new CameraScene(mCamera);
		/* Make the label centered on the camera. */
		final int x = (int) (mCamera.getWidth() / 2 - mPausedTextureRegion
				.getWidth() / 2);
		final int y = (int) (mCamera.getHeight() / 2 - mPausedTextureRegion
				.getHeight() / 2);
		final Sprite pausedSprite = new Sprite(x, y, mPausedTextureRegion);
		mPauseScene.attachChild(pausedSprite);
		// makes the scene transparent
		mPauseScene.setBackgroundEnabled(false);

		// the results scene, for win/fail
		mResultScene = new CameraScene(mCamera);
		winSprite = new Sprite(x, y, mWinTextureRegion);
		failSprite = new Sprite(x, y, mFailTextureRegion);
		mResultScene.attachChild(winSprite);
		mResultScene.attachChild(failSprite);
		// makes the scene transparent
		mResultScene.setBackgroundEnabled(false);

		winSprite.setVisible(false);
		failSprite.setVisible(false);

		// set background color
		mMainScene = new Scene();

		model = new Models(this);
		controller = new Controller(this);
		
		// background preperations
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(
				0, 0, 0, 10);

		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-25.0f, new Sprite(0,
						mCamera.getHeight() - mParallaxLayer.getHeight(),
						mParallaxLayer)));
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(-80.0f, new Sprite(0, 80, this.mParallaxLayerMid)));
		mMainScene.setBackground(autoParallaxBackground);
		mMainScene.setOnSceneTouchListener(this);

		Models.createHero();
		mMainScene.setTouchAreaBindingEnabled(true);

		// initializing variables
		projectileLL = new LinkedList<Sprite>();
		targetLL = new LinkedList<AnimatedSprite>();
		projectilesToBeAdded = new LinkedList<Sprite>();
		TargetsToBeAdded = new LinkedList<AnimatedSprite>();
		
		objcetLL = new LinkedList<Sprite>();
		objectsToBeAdded = new LinkedList<Sprite>();

		// settings score to the value of the max score to make sure it appears
		// correctly on the screen
		score = new ChangeableText(0, 0, mFont, String.valueOf(maxScore));
		// repositioning the score later so we can use the score.getWidth()
		score.setPosition(mCamera.getWidth() - score.getWidth() - 5, 5);

		
		controller.createSpriteSpawnTimeHandler();
		controller.checkCollision();
		controller.checkCollision1();

		// starting background music
		backgroundMusic.play();
		// runningFlag = true;

		controller.restart(this);

		return mMainScene;
	}
	

	@Override
	public void onLoadComplete() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		// if the user tapped the screen
		if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) 
		{
			final float touchX = pSceneTouchEvent.getX();
			final float touchY = pSceneTouchEvent.getY();
			
			Models.shootProjectile(touchX, touchY);
			
			return true;
		}
		return false;
	}

	@Override
	// pauses the music and the game when the game goes to the background
	protected void onPause() 
	{
		if (runningFlag) 
		{
			pauseMusic();
			if (mEngine.isRunning()) 
			{
				pauseGame();
				pauseFlag = true;
			}
		}
		super.onPause();
	}

	@Override
	public void onResumeGame() 
	{
		super.onResumeGame();
		// shows this Toast when coming back to the game
		if(runningFlag) 
		{
			if(pauseFlag) 
			{
				pauseFlag = false;
				Toast.makeText(this, "Menu button to resume",
						Toast.LENGTH_SHORT).show();
			} 
			else 
			{
				// in case the user clicks the home button while the game on the
				// resultScene
				resumeMusic();
				mEngine.stop();
			}
		} 
		else 
		{
			runningFlag = true;
		}
	}

	public void pauseMusic()
	{
		if(runningFlag)
			if(backgroundMusic.isPlaying())
				backgroundMusic.pause();
	}

	public void resumeMusic()
	{
		if(runningFlag)
			if(!backgroundMusic.isPlaying())
				backgroundMusic.resume();
	}

	public void pauseGame()
	{
		if (runningFlag) 
		{
			mMainScene.setChildScene(mPauseScene, false, true, true);
			mEngine.stop();
		}
	}

	public void unPauseGame()
	{
		mMainScene.clearChildScene();
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent)
	{
		// if menu button is pressed
		if (pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN)
		{
			if (mEngine.isRunning() && backgroundMusic.isPlaying()) 
			{
				pauseMusic();
				pauseFlag = true;
				pauseGame();
				Toast.makeText(this, "Menu button to resume", Toast.LENGTH_SHORT).show();
			} 
			else 
			{
				if(!backgroundMusic.isPlaying()) 
				{
					unPauseGame();
					pauseFlag = false;
					resumeMusic();
					mEngine.start();
				}
				return true;
			}
			// if back key was pressed
		} 
		else if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) 
		{
			if(!mEngine.isRunning() && backgroundMusic.isPlaying())
			{
				mMainScene.clearChildScene(); 
				mEngine.start();
				controller.restart(this);
				return true;
			}
			return super.onKeyDown(pKeyCode, pEvent);
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}
	
}
