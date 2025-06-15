package ac.tukorea.yunjun.pegglepang.PegglePang.game.monster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.StageManager;
import ac.tukorea.yunjun.pegglepang.R;

public class Stage1Monster {
    private Bitmap idleSheet;
    private Bitmap attackSheet;
    private Bitmap deathSheet;
    private MonsterAnimation animation;
    private int frame = 0;
    private int frameCount;
    private float animTimer = 0f;
    private static final float FRAME_DURATION = 0.5f;
    private float x, y, width, height;
    private boolean isSkeleton = false;
    private int idleResource;
    private Context context;

    private int maxHp;
    private int currentHp;
    private float attackPower = 10f;  // 기본 공격력
    private boolean isAlive = true;
    private Paint hpPaint;

    private boolean isBlinking = false;
    private float blinkTimer = 0f;
    private float blinkDuration = 0.5f;
    private int blinkCount = 0;
    private int maxBlinkCount = 4;
    private int pendingDamage = 0;

    private boolean isAttacking = false;
    private float attackTimer = 0f;
    private static final float ATTACK_DURATION = 1.0f;
    private AttackCallback attackCallback;

    private boolean isDying = false;
    private float deathTimer = 0f;
    private static final float DEATH_DURATION = 0.9f;  

    public interface AttackCallback {
        void onAttackComplete();
    }

    public Stage1Monster(Context context, int resId, int frameCount, float x, float y, float width, float height) {
        this.context = context;
        this.idleResource = resId;
        this.idleSheet = BitmapFactory.decodeResource(context.getResources(), resId);
        this.attackSheet = BitmapFactory.decodeResource(context.getResources(), 
            resId == R.mipmap.skeleton_idle ? R.mipmap.skeleton_attack : R.mipmap.slime_attack);
        this.deathSheet = BitmapFactory.decodeResource(context.getResources(),
            resId == R.mipmap.skeleton_idle ? R.mipmap.skeleton_death : R.mipmap.slime_death);
        this.frameCount = frameCount;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animation = new MonsterAnimation(idleSheet, attackSheet, deathSheet, x, y, width, height);
        if (resId == R.mipmap.skeleton_idle) {
            isSkeleton = true;
            this.maxHp = 1;
        } else {
            this.maxHp = 1;
        }
        this.currentHp = this.maxHp;

        // 스테이지가 이미 클리어된 상태라면 몬스터를 즉시 처치 상태로 만듦
        if (StageManager.getInstance().areMonstersDefeated(1, 1)) {
            this.currentHp = 0;
            this.isAlive = false;
            this.isDying = false;
        }

        hpPaint = new Paint();
        hpPaint.setColor(Color.WHITE);
        hpPaint.setTextSize(30);
        hpPaint.setAntiAlias(true);
        hpPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void update(float dt) {
        if (isDying) {
            deathTimer += dt;
            if (deathTimer >= DEATH_DURATION) {
                isDying = false;
                isAlive = false;
            }
            return;
        }

        if (isAttacking) {
            attackTimer += dt;
            if (attackTimer >= ATTACK_DURATION) {
                isAttacking = false;
                attackTimer = 0f;
                if (attackCallback != null) {
                    attackCallback.onAttackComplete();
                }
            }
        } else {
            animTimer += dt;
            if (animTimer >= FRAME_DURATION) {
                animTimer -= FRAME_DURATION;
                frame = (frame + 1) % frameCount;
            }
        }

        if (isBlinking) {
            blinkTimer += dt;
            if (blinkTimer >= blinkDuration / maxBlinkCount) {
                blinkTimer -= blinkDuration / maxBlinkCount;
                blinkCount++;
                if (blinkCount >= maxBlinkCount) {
                    isBlinking = false;
                    blinkCount = 0;
                    if (pendingDamage > 0) {
                        applyPendingDamage();
                    }
                }
            }
        }

        if (!isAlive && !isDying) {
            // StageClearScene.getInstance(context).show(1, 1); // 중복 호출 방지 위해 제거
        }
    }

    public void draw(Canvas canvas) {
        if (!isAlive && !isDying) return;
        
        if (isDying) {
            int frameW = deathSheet.getWidth() / 3;  // 3프레임
            int frameH = deathSheet.getHeight();
            int currentDeathFrame = (int)(deathTimer / (DEATH_DURATION / 3));
            if (currentDeathFrame >= 3) currentDeathFrame = 2;
            
            Rect src = new Rect(frameW * currentDeathFrame, 0, frameW * (currentDeathFrame + 1), frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(deathSheet, src, dest, null);
        } else if (isAttacking && attackSheet != null) {
            int frameW = attackSheet.getWidth() / frameCount;
            int frameH = attackSheet.getHeight();
            int left = frameW * frame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(attackSheet, src, dest, null);
        } else if (idleSheet != null) {
            int frameW = idleSheet.getWidth() / frameCount;
            int frameH = idleSheet.getHeight();
            int left = frameW * frame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            if (isBlinking && blinkCount % 2 == 0) {
                Paint blinkPaint = new Paint();
                blinkPaint.setAlpha(80);
                canvas.drawBitmap(idleSheet, src, dest, blinkPaint);
            } else {
                canvas.drawBitmap(idleSheet, src, dest, null);
            }
        }

        // HP 표시 (몬스터 위에)
        float hpX = x + width / 2;
        float hpY = y - 10; // 몬스터 위 10픽셀 위에 표시
        hpPaint.setColor(Color.WHITE);
        canvas.drawText(currentHp + "/" + maxHp, hpX, hpY, hpPaint);
    }

    public void setPosition(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void takeDamage(float damage) {
        currentHp -= damage;
        if (currentHp <= 0) {
            currentHp = 0;
            die();
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public float getAttackPower() {
        return attackPower;
    }

    public void startBlinking(int damage) {
        isBlinking = true;
        blinkTimer = 0f;
        blinkCount = 0;
        pendingDamage = damage;
    }

    private void applyPendingDamage() {
        currentHp = Math.max(0, currentHp - pendingDamage);
        if (currentHp <= 0) {
            die();
        }
        pendingDamage = 0;
    }

    public boolean isBlinking() {
        return isBlinking;
    }

    public void attack(AttackCallback callback) {
        if (!isAlive || isDying) return;  
        
        isAttacking = true;
        attackTimer = 0f;
        this.attackCallback = callback;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAnimationType(MonsterAnimation.Type type) {
        animation.setType(type);
    }

    public void die() {
        isDying = true;
        deathTimer = 0f;
    }

    public boolean isDying() {
        return isDying;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }
} 