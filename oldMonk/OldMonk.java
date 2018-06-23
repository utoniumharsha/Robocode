package oldMonk;

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

// GuessFactor targeting and Wave Surfing

public class OldMonk extends Robot {

    List<WaveBullet> waves = new ArrayList<WaveBullet>();
    static int[] stats = new int[31]; // odd so that the 0 comes in the middle
    int direction = 1;

    public void run(){

    }


    @Override
    public void onScannedRobot(ScannedRobotEvent e) {

        // Movement

        // Enemy's absolute bearing
        double absBearing = getHeading() + e.getBearingRadians();

        // Find opponent's location:
        double ex = getX() + Math.sin(absBearing) * e.getDistance();
        double ey = getY() + Math.cos(absBearing) * e.getDistance();

        // Wave Processing
        for( int i=0; i < waves.size(); i++ ){
            WaveBullet currentWave = (WaveBullet)waves.get(i);
            if( currentWave.checkHit(ex, ey, getTime())){
                waves.remove(currentWave);
                i--;
            }
        }
        
        double power = Math.min(3, Math.max(0.1, bulletPower(ex, ey, getX(), getY())));

        if( e.getVelocity() != 0 ){
            if( Math.sin( e.getHeading() - absBearing ) * e.getVelocity() < 0 ){
                direction = -1;
            } else{
                direction = 1;
            }
        }

        int[] currentStats = stats;
        WaveBullet newWave = new WaveBullet(getX(), getY(), absBearing, power, getTime(), direction, currentStats );

        int bestIndex = 15; // guessFactor = 0
        for( int i=0; i < 31; i++ ){
            if( currentStats[ bestIndex ] < currentStats[ i ]){
                bestIndex = i;
            }
        }

        // this should be doing opposite of the Math in the WaveBullet:
        double guessFactor = (double)(bestIndex - (stats.length - 1)/2)/((stats.length - 1)/2);
        double angleOffSet = direction * guessFactor * newWave.maxEscapeAngle();
        double gunAdjust = Utils.normalRelativeAngle(absBearing - getGunHeading() + angleOffSet );
        turnGunRight(gunAdjust);


    }

    private double bulletPower(double ex, double ey, double x, double y) {
        double euclideanDist = Point2D.distance(ex, ey, x, y);
        long normalisedDist = (long)(euclideanDist * 10000);
        return ( (double)(normalisedDist % 300 )/100 );
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnLeft(90 - e.getBearing());
    }


}
