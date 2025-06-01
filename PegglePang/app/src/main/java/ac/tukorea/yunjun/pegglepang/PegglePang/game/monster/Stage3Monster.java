package ac.tukorea.yunjun.pegglepang.PegglePang.game.monster;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;

public class Stage3Monster {
    private static final int IDLE_FRAME_COUNT = 5;
    private static final int ATTACK1_FRAME_COUNT = 4;
    private static final int ATTACK2_FRAME_COUNT = 3;
    private static final int DIE_FRAME_COUNT = 6;
    private static final float FRAME_DURATION = 0.15f;
    private static final float DIE_FRAME_DURATION = 0.2f;
    private static final int IDLE_MONSTER_WIDTH = 284;
    private static final int IDLE_MONSTER_HEIGHT = 68;
    private static final int ATTACK1_MONSTER_WIDTH = 272;
    private static final int ATTACK1_MONSTER_HEIGHT = 81;
    private static final int ATTACK2_MONSTER_WIDTH = 284;
    private static final int ATTACK2_MONSTER_HEIGHT = 68;
    private static final int DIE_MONSTER_WIDTH = 120;
    private static final int DIE_MONSTER_HEIGHT = 68;

    private Bitmap idleSheet;
    private Bitmap attack1Sheet;
    private Bitmap attack2Sheet;
    private Bitmap dieSheet;
    private int frame = 0;
    private int attackFrame = 0;
    private int dieFrame = 0;
    private float animTimer = 0f;
    private float attackAnimTimer = 0f;
    private float dieAnimTimer = 0f;
    private float x, y, width, height;
    private Context context;

    private int maxHp = 30;
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
    private boolean isMagicAttack = false;

    public interface AttackCallback {
        void onAttackComplete();
    }

    private static float[] calculateMonsterDimensions(float battleHeight) {
        float monsterDrawHeight = battleHeight * 0.6f;
        float monsterDrawWidth = monsterDrawHeight * (56.8f / 68f); 
        float monsterLeft = Metrics.width - monsterDrawWidth - (Metrics.width * 0.05f);
        float monsterTop = battleHeight - monsterDrawHeight - (battleHeight * 0.05f);
        return new float[]{monsterLeft, monsterTop, monsterDrawWidth, monsterDrawHeight};
    }

    public Stage3Monster(Context context, int resId, int hp, float battleHeight, float attackPower) {
        float[] dimensions = calculateMonsterDimensions(battleHeight);
        this.context = context;
        this.idleSheet = BitmapFactory.decodeResource(context.getResources(), resId);
        this.attack1Sheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.redman_attack1);
        this.attack2Sheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.redman_attack2);
        this.dieSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.redman_die);
        this.x = dimensions[0];
        this.y = dimensions[1];
        this.width = dimensions[2];
        this.height = dimensions[3];
        this.maxHp = hp;
        this.currentHp = hp;
        this.attackPower = attackPower;

        hpPaint = new Paint();
        hpPaint.setColor(Color.WHITE);
        hpPaint.setTextSize(30);
        hpPaint.setAntiAlias(true);
        hpPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void update(float dt) {
        if (isDying) {
            dieAnimTimer += dt;
            if (dieAnimTimer >= DIE_FRAME_DURATION) {
                dieAnimTimer -= DIE_FRAME_DURATION;
                dieFrame++;
                if (dieFrame >= DIE_FRAME_COUNT) {
                    isDying = false;
                    isAlive = false;
                }
            }
            return;
        }

        if (isAttacking) {
            attackTimer += dt;
            attackAnimTimer += dt;
            if (attackAnimTimer >= FRAME_DURATION) {
                attackAnimTimer -= FRAME_DURATION;
                if (isMagicAttack) {
                    attackFrame = (attackFrame + 1) % ATTACK1_FRAME_COUNT;
                } else {
                    attackFrame = (attackFrame + 1) % ATTACK2_FRAME_COUNT;
                }
            }
            if (attackTimer >= ATTACK_DURATION) {
                isAttacking = false;
                attackTimer = 0f;
                attackFrame = 0;
                if (attackCallback != null) {
                    attackCallback.onAttackComplete();
                }
            }
        } else {
            animTimer += dt;
            if (animTimer >= FRAME_DURATION) {
                animTimer -= FRAME_DURATION;
                frame = (frame + 1) % IDLE_FRAME_COUNT;
            }
        }

        if (isBlinking) {
            blinkTimer += dt;
            if (blinkTimer >= 0.1f) {
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
            int frameW = dieSheet.getWidth() / DIE_FRAME_COUNT;
            int frameH = dieSheet.getHeight();
            int left = frameW * dieFrame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            float dieWidth = width * (DIE_MONSTER_WIDTH / (float)IDLE_MONSTER_WIDTH);
            float dieHeight = height * (DIE_MONSTER_HEIGHT / (float)IDLE_MONSTER_HEIGHT);
            RectF dest = new RectF(x, y, x + dieWidth, y + dieHeight);
            canvas.drawBitmap(dieSheet, src, dest, null);
        } else if (isAttacking) {
            if (isMagicAttack && attack1Sheet != null) {
                int frameW = attack1Sheet.getWidth() / ATTACK1_FRAME_COUNT;
                int frameH = attack1Sheet.getHeight();
                int left = frameW * attackFrame;
                int right = left + frameW;
                Rect src = new Rect(left, 0, right, frameH);
                float attackWidth = width * (ATTACK1_MONSTER_WIDTH / (float)IDLE_MONSTER_WIDTH);
                float attackHeight = height * (ATTACK1_MONSTER_HEIGHT / (float)IDLE_MONSTER_HEIGHT);
                RectF dest = new RectF(x, y, x + attackWidth, y + attackHeight);
                canvas.drawBitmap(attack1Sheet, src, dest, null);
            } else if (!isMagicAttack && attack2Sheet != null) {
                int frameW = attack2Sheet.getWidth() / ATTACK2_FRAME_COUNT;
                int frameH = attack2Sheet.getHeight();
                int left = frameW * attackFrame;
                int right = left + frameW;
                Rect src = new Rect(left, 0, right, frameH);
                float attackWidth = width * (ATTACK2_MONSTER_WIDTH / (float)IDLE_MONSTER_WIDTH);
                float attackHeight = height * (ATTACK2_MONSTER_HEIGHT / (float)IDLE_MONSTER_HEIGHT);
                RectF dest = new RectF(x, y, x + attackWidth, y + attackHeight);
                canvas.drawBitmap(attack2Sheet, src, dest, null);
            }
        } else if (idleSheet != null) {
            int frameW = idleSheet.getWidth() / IDLE_FRAME_COUNT;
            int frameH = idleSheet.getHeight();
            int left = frameW * frame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            if (isBlinking) {
                Paint blinkPaint = new Paint();
                blinkPaint.setAlpha(blinkCount % 2 == 0 ? 255 : 80);
                canvas.drawBitmap(idleSheet, src, dest, blinkPaint);
            } else {
                canvas.drawBitmap(idleSheet, src, dest, null);
            }
        }

        if (!isDying) {
            float hpX = x + width / 2;
            float hpY = y - 10;
            hpPaint.setColor(Color.WHITE);
            canvas.drawText(currentHp + "/" + maxHp, hpX, hpY, hpPaint);
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

    public float getAttackPower() {
        return attackPower;
    }

    public void startBlinking(int damage) {
        isBlinking = true;
        blinkCount = 0;
        blinkTimer = 0f;
        pendingDamage = damage;
        takeDamage(damage);
    }

    public boolean isBlinking() {
        return isBlinking;
    }

    public void attack(AttackCallback callback, boolean isMagicAttack) {
        if (!isAlive) return;
        isAttacking = true;
        attackTimer = 0f;
        this.isMagicAttack = isMagicAttack;
        this.attackCallback = callback;
    }

    public boolean isDying() {
        return isDying;
    }

    public float getX() {
        return x;
    }
} 