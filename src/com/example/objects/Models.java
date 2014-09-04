package com.example.objects;

import java.util.Random;

import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.DelayModifier;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.MoveByModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.IModifier.IModifierListener;

import com.example.andenginerunner2.MainActivity;

import android.content.Context;

public class Models 
{

	public Context context;

	public Models(Context con) 
	{
		this.context = con;
	}

	/** adds a target at a random location and let it move along the x-axis */
	public static void addTarget() 
	{
		Random rand = new Random();

		int x = (int) MainActivity.mCamera.getWidth()
				+ MainActivity.mTargetTextureRegion.getWidth();
		int minY = MainActivity.mTargetTextureRegion.getHeight();
		int maxY = (int) (MainActivity.mCamera.getHeight() - MainActivity.mTargetTextureRegion
				.getHeight());
		int rangeY = maxY - minY;
		int y = rand.nextInt(rangeY) + minY;

		AnimatedSprite target;
		target = MainActivity.tPool.obtainPoolItem();
		target.setPosition(x, y);
		target.animate(300);
		MainActivity.mMainScene.attachChild(target);

		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		MoveXModifier mod = new MoveXModifier(actualDuration, target.getX(),
				-target.getWidth());
		target.registerEntityModifier(mod.deepCopy());

		MainActivity.TargetsToBeAdded.add(target);

	}

	/** shoots a projectile from the player's position along the touched area */
	public static void shootProjectile(final float pX, final float pY) 
	{
		if (!CoolDown.sharedCoolDown().checkValidity()) 
		{
			return;
		}

		int offX = (int) (pX - MainActivity.hero.getX());
		int offY = (int) (pY - MainActivity.hero.getY());
		if (offX <= 0)
			return;

		final Sprite projectile;
		// position the projectile on the player
		projectile = MainActivity.pPool.obtainPoolItem();
		projectile.setPosition(
				MainActivity.hero.getX() + MainActivity.hero.getWidth(),
				MainActivity.hero.getY());

		int realX = (int) (MainActivity.mCamera.getWidth() + projectile
				.getWidth() / 2.0f);
		float ratio = (float) offY / (float) offX;
		int realY = (int) ((realX * ratio) + projectile.getY());

		int offRealX = (int) (realX - projectile.getX());
		int offRealY = (int) (realY - projectile.getY());
		float length = (float) Math.sqrt((offRealX * offRealX)
				+ (offRealY * offRealY));
		float velocity = 480.0f / 1.0f; // 480 pixels / 1 sec
		float realMoveDuration = length / velocity;

		// defining a moveBymodifier from the projectile's position to the
		// calculated one
		MoveByModifier movMByod = new MoveByModifier(realMoveDuration, realX, realY);
		LoopEntityModifier loopMod = new LoopEntityModifier( new RotationModifier(0.5f, 0, -360));

		final ParallelEntityModifier par = new ParallelEntityModifier(movMByod, loopMod);

		DelayModifier dMod = new DelayModifier(0.55f);
		dMod.addModifierListener(new IModifierListener<IEntity>()
		{

			@Override
			public void onModifierStarted(IModifier<IEntity> arg0, IEntity arg1) 
			{
				
			}
			@Override
			public void onModifierFinished(IModifier<IEntity> arg0, IEntity arg1) 
			{
				// TODO Auto-generated method stub
				MainActivity.shootingSound.play();
				projectile.setVisible(true);
				projectile.setPosition(
						MainActivity.hero.getX() + MainActivity.hero.getWidth(),
						MainActivity.hero.getY()
								+ MainActivity.hero.getHeight() / 3);
				MainActivity.projectilesToBeAdded.add(projectile);
			}
		});

		SequenceEntityModifier seq = new SequenceEntityModifier(dMod, par);
		projectile.registerEntityModifier(seq);
		projectile.setVisible(false);
		MainActivity.mMainScene.attachChild(projectile, 1);

		MainActivity.hero.animate(50, false);
	}

	public static void createHero()
	{
		// TODO Auto-generated method stub
		// set coordinates for the player
		final int PlayerX = (MainActivity.mHeroTextureRegion.getWidth() / 20);
		final int PlayerY = (int) ((MainActivity.mCamera.getHeight() - MainActivity.mHeroTextureRegion
				.getHeight()) / 2);

		// set the player on the scene
		MainActivity.hero = new AnimatedSprite(PlayerX, PlayerY, MainActivity.mHeroTextureRegion) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				this.setPosition(this.getX(),
						pSceneTouchEvent.getY() - this.getHeight() / 2);

				return true;
			}
		};

		MainActivity.mMainScene.registerTouchArea(MainActivity.hero);
	}

	public static void addObjects() 
	{
		Random rand = new Random();

		int x = (int) MainActivity.mCamera.getWidth()
				+ MainActivity.mTargetTextureRegion.getWidth();
		int minY = MainActivity.mTargetTextureRegion.getHeight();
		int maxY = (int) (MainActivity.mCamera.getHeight() - MainActivity.mTargetTextureRegion
				.getHeight());
		int rangeY = maxY - minY;
		int y = rand.nextInt(rangeY) + minY;

		Sprite Object;
		Object = MainActivity.oPool.obtainPoolItem();
		Object.setPosition(x, y);
//		Object.animate(300);
		MainActivity.mMainScene.attachChild(Object);

		int minDuration = 2;
		int maxDuration = 4;
		int rangeDuration = maxDuration - minDuration;
		int actualDuration = rand.nextInt(rangeDuration) + minDuration;

		MoveXModifier mod = new MoveXModifier(actualDuration, Object.getX(),
				-Object.getWidth());
		Object.registerEntityModifier(mod.deepCopy());

		MainActivity.objectsToBeAdded.add(Object);

	}

}
