package ac.tukorea.yunjun.pegglepang.PegglePang.game.monster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import ac.tukorea.yunjun.pegglepang.R;

public class Stage2Monster {
    private Bitmap idleSheet;
    private Bitmap attackSheet;
    private Bitmap iceBallSheet;
    private Bitmap dieSheet;
    private int frame = 0;
    private int frameCount;
    private int attackFrameCount = 4; // 기본값, 생성자에서 설정됨
    private int dieFrameCount = 6;
    private int attackFrame = 0;
    private int dieFrame = 0;
    private int iceBallFrame = 0;
    private float animTimer = 0f;
    private float attackAnimTimer = 0f;
    private float dieAnimTimer = 0f;
    private float iceBallAnimTimer = 0f;
    private static final float FRAME_DURATION = 0.3f;
    private static final float ATTACK_FRAME_DURATION = 0.15f;
    private static final float DIE_FRAME_DURATION = 0.2f;
    private static final float ICE_BALL_FRAME_DURATION = 0.2f;
    private static final float ICE_BALL_SPEED = 2000f;
    private float x, y, width, height;
    private Context context;

    private int maxHp = 1;
    private int currentHp;
    private float attackPower = 15f;
    private boolean isAlive = true;
    private boolean isDying = false;
    private Paint hpPaint;

    private boolean isBlinking = false;
    private float blinkTimer = 0f;
    private int blinkCount = 0;
    private static final float blinkDuration = 0.5f;
    private static final int maxBlinkCount = 5;
    private int pendingDamage = 0;

    private boolean isAttacking = false;
    private float attackTimer = 0f;
    private static final float ATTACK_DURATION = 0.6f;
    private AttackCallback attackCallback;
    private boolean shouldShootIceBall = false;

    private boolean isShootingIceBall = false;
    private float iceBallX, iceBallY;
    private float iceBallWidth = 57f;
    private float iceBallHeight = 54f;
    private float targetX, targetY;
    private float iceBallAngle;
    private boolean isIceBallActive = false;

    private float magicDamageThreshold;  // 마법 공격력 기준값

    // 마비 상태 관련
    private boolean isStunned = false;
    private int stunTurnsRemaining = 0;

    // 공포 효과 관련 (고스트 전용)
    private boolean canCauseFear = false;
    private float fearChance = 0.2f; // 20% 확률

    public interface AttackCallback {
        void onAttackComplete();
    }

    public Stage2Monster(Context context, int resId, int frameCount, float x, float y, float width, float height, float magicDamageThreshold) {
        this.context = context;
        this.idleSheet = BitmapFactory.decodeResource(context.getResources(), resId);
        
        // 고스트인 경우
        if (resId == R.mipmap.ghost_idle) {
            this.attackSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ghost_attack);
            this.dieSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ghost_die);
            this.attackFrameCount = 3; // 고스트 공격은 3프레임
            this.dieFrameCount = 3; // 고스트 죽음은 3프레임
            this.canCauseFear = true; // 고스트는 공포 효과 가능
        }
        // magicDamageThreshold가 0보다 크면 magicman (S1_2), 0이면 axeman (S2_1)
        else if (magicDamageThreshold > 0) {
            this.attackSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.magicman_attack);
            this.dieSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.magicman_die);
            this.attackFrameCount = 4; // magicman은 4프레임
            this.dieFrameCount = 6; // magicman 죽음은 6프레임
        } else {
            this.attackSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.axeman_attack);
            this.dieSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.magicman_die);
            this.attackFrameCount = 3; // axeman은 3프레임
            this.dieFrameCount = 6; // 기본 죽음은 6프레임
        }
        
        this.iceBallSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ice_ball);
        this.frameCount = frameCount;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.currentHp = this.maxHp;
        this.magicDamageThreshold = magicDamageThreshold;

        hpPaint = new Paint();
        hpPaint.setColor(Color.WHITE);
        hpPaint.setTextSize(30);
        hpPaint.setAntiAlias(true);
        hpPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    public void setTargetPosition(float x, float y) {
        this.targetX = x;
        this.targetY = y;
    }

    public void update(float dt) {
        if (isDying) {
            dieAnimTimer += dt;
            if (dieAnimTimer >= DIE_FRAME_DURATION) {
                dieAnimTimer -= DIE_FRAME_DURATION;
                dieFrame++;
                if (dieFrame >= dieFrameCount) {
                    isDying = false;
                    isAlive = false;
                }
            }
            return;
        }

        if (isAttacking) {
            attackTimer += dt;
            attackAnimTimer += dt;
            if (attackAnimTimer >= ATTACK_FRAME_DURATION) {
                attackAnimTimer -= ATTACK_FRAME_DURATION;
                attackFrame = (attackFrame + 1) % attackFrameCount;
                
                if (attackFrame == 2 && !isShootingIceBall && magicDamageThreshold > 0) {
                    shouldShootIceBall = true;
                }
            }
            if (attackTimer >= ATTACK_DURATION) {
                isAttacking = false;
                attackTimer = 0f;
                attackFrame = 0;
                if (shouldShootIceBall && magicDamageThreshold > 0) {
                    isShootingIceBall = true;
                    isIceBallActive = true;
                    iceBallX = x;
                    iceBallY = y + height/2-30f;
                    iceBallAngle = 180f;
                    shouldShootIceBall = false;
                }
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

        if (isIceBallActive && magicDamageThreshold > 0) {
            iceBallAnimTimer += dt;
            if (iceBallAnimTimer >= ICE_BALL_FRAME_DURATION) {
                iceBallAnimTimer -= ICE_BALL_FRAME_DURATION;
                iceBallFrame = (iceBallFrame + 1) % 2;
            }

            iceBallX -= ICE_BALL_SPEED * dt;

            if (iceBallX < -iceBallWidth) {
                isIceBallActive = false;
                isShootingIceBall = false;
            }
        }

        if (isBlinking) {
            blinkTimer += dt;
            if (blinkTimer >= 0.1f) {  // 0.1초마다 깜빡임
                blinkTimer = 0f;
                blinkCount++;
                if (blinkCount >= maxBlinkCount) {
                    isBlinking = false;
                    blinkCount = 0;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        if (!isAlive && !isDying) return;

        if (isDying && dieSheet != null) {
            int frameW = dieSheet.getWidth() / dieFrameCount;
            int frameH = dieSheet.getHeight();
            int left = frameW * dieFrame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(dieSheet, src, dest, null);
        } else if (isAttacking && attackSheet != null) {
            int frameW = attackSheet.getWidth() / attackFrameCount;
            int frameH = attackSheet.getHeight();
            int left = frameW * attackFrame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            
            // 고스트인 경우 가로대칭으로 그리기
            if (canCauseFear) {
                canvas.save();
                canvas.scale(-1f, 1f, x + width/2, y + height/2); // 좌우 반전
                canvas.drawBitmap(attackSheet, src, dest, null);
                canvas.restore();
            } else {
                canvas.drawBitmap(attackSheet, src, dest, null);
            }
        } else if (idleSheet != null) {
            int frameW = idleSheet.getWidth() / frameCount;
            int frameH = idleSheet.getHeight();
            int left = frameW * frame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            
            // 고스트인 경우 가로대칭으로 그리기
            if (canCauseFear) {
                canvas.save();
                canvas.scale(-1f, 1f, x + width/2, y + height/2); // 좌우 반전
                if (isBlinking) {
                    Paint blinkPaint = new Paint();
                    blinkPaint.setAlpha(blinkCount % 2 == 0 ? 255 : 80);  // 80% 투명도로 깜빡임
                    canvas.drawBitmap(idleSheet, src, dest, blinkPaint);
                } else {
                    canvas.drawBitmap(idleSheet, src, dest, null);
                }
                canvas.restore();
            } else {
                if (isBlinking) {
                    Paint blinkPaint = new Paint();
                    blinkPaint.setAlpha(blinkCount % 2 == 0 ? 255 : 80);  // 80% 투명도로 깜빡임
                    canvas.drawBitmap(idleSheet, src, dest, blinkPaint);
                } else {
                    canvas.drawBitmap(idleSheet, src, dest, null);
                }
            }
        }

        if (isIceBallActive && iceBallSheet != null && magicDamageThreshold > 0) {
            int frameW = iceBallSheet.getWidth() / 2;
            int frameH = iceBallSheet.getHeight();
            int left = frameW * iceBallFrame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(iceBallX, iceBallY, iceBallX + iceBallWidth, iceBallY + iceBallHeight);
            canvas.drawBitmap(iceBallSheet, src, dest, null);
        }

        if (!isDying) { 
            float hpX = x + width / 2;
            float hpY = y - 10;
            hpPaint.setColor(Color.WHITE);
            canvas.drawText(currentHp + "/" + maxHp, hpX, hpY, hpPaint);
            
            // 마비 상태 표시
            if (isStunned) {
                hpPaint.setColor(Color.YELLOW);
                canvas.drawText("마비", hpX, hpY - 30, hpPaint);
            }
        }
    }

    public void takeDamage(float damage) {
        if (isDying) return;
        
        currentHp -= damage;
        
        if (currentHp <= 0) {
            currentHp = 0;
            isDying = true;
            dieFrame = 0;
            dieAnimTimer = 0f;
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
        System.out.println("Stage2Monster taking damage: " + damage + ", current HP: " + currentHp);
        isBlinking = true;
        blinkCount = 0;
        blinkTimer = 0f;
        pendingDamage = damage;
        takeDamage(damage);  // 데미지를 즉시 적용
        System.out.println("After damage, HP: " + currentHp);
    }

    public boolean isBlinking() {
        return isBlinking;
    }

    private void applyPendingDamage() {
        if (isDying) return;
        if (currentHp <= 0) {
            currentHp = 0;
            isDying = true;
            dieFrame = 0;
            dieAnimTimer = 0f;
        }
        pendingDamage = 0;
    }

    public void attack(AttackCallback callback) {
        if (!isAlive) return;
        isAttacking = true;
        attackTimer = 0f;
        this.attackCallback = callback;
    }

    public boolean isIceBallActive() {
        return isIceBallActive;
    }

    public RectF getIceBallRect() {
        return new RectF(iceBallX, iceBallY, iceBallX + iceBallWidth, iceBallY + iceBallHeight);
    }

    public void deactivateIceBall() {
        isIceBallActive = false;
        isShootingIceBall = false;
    }

    public boolean isDying() {
        return isDying;
    }

    public void setAttackImage(int resId) {
        this.attackSheet = BitmapFactory.decodeResource(context.getResources(), resId);
    }

    protected void setAttackSheet(int resId) {
        this.attackSheet = BitmapFactory.decodeResource(context.getResources(), resId);
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

    // 마비 관련 메서드들
    public void setStunned(int turns) {
        this.isStunned = true;
        this.stunTurnsRemaining = turns;
    }
    
    public boolean isStunned() {
        return isStunned;
    }
    
    public void reduceStunTurns() {
        if (stunTurnsRemaining > 0) {
            stunTurnsRemaining--;
            if (stunTurnsRemaining <= 0) {
                isStunned = false;
            }
        }
    }
    
    public boolean canAttack() {
        return !isStunned && isAlive && !isDying;
    }

    // 공포 효과 체크 (고스트 전용)
    public boolean checkFearEffect() {
        return canCauseFear && Math.random() < fearChance;
    }
    
    public boolean canCauseFear() {
        return canCauseFear;
    }
} 