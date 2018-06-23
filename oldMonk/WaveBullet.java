package oldMonk;

import robocode.util.Utils;

import java.awt.geom.Point2D;

public class WaveBullet {
    private double startX, startY, startBearing, power;
    private long fireTime;
    private int direction;
    private int[] returnSegment;

    public WaveBullet(double startX, double startY, double startBearing, double power, long fireTime, int direction, int[] returnSegment) {
        this.startX = startX; // X location we are firing from
        this.startY = startY; // Y location we are firing from
        this.startBearing = startBearing; // clock direction opponent is moving relative to us
        this.power = power; // power of the bullet
        this.fireTime = fireTime;
        this.direction = direction;// direction of fire
        this.returnSegment = returnSegment;
    }

    public double getBulletSpeed(){
        return 20 - power*3;
    }

    public double maxEscapeAngle(){
        return Math.asin(8/getBulletSpeed());
    }

    // Will check if the wave has hit the enemy. If it hasn't, it will return false.
    // If it has found, it will figure out what GuessFactor the enemy is at, find the appropriate index into the returnSegment and increment it.
    // Return true signifies that the wave should no longer be tracked.
    public boolean checkHit(double enemyX, double enemyY, long currentTime){
        // if the distance from the wave origin to opponent has passed
        // the distance the bullet would have travelled
        if(Point2D.distance(startX, startY, enemyX, enemyY ) <= ( currentTime - fireTime ) * getBulletSpeed() ){
            double desiredDirection = Math.atan2( enemyX - startX, enemyY - startY );
            double angleOffSet = Utils.normalRelativeAngle(desiredDirection - startBearing);
            double guessFactor = Math.max(-1, Math.min(1, angleOffSet/maxEscapeAngle())) * direction;
            int index = (int) Math.round( (returnSegment.length - 1)/2 * ( guessFactor+1) );
            returnSegment[ index ]++;
            return true;
        }
        return false;
    }
}
