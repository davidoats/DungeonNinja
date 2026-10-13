
package combat;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Records and draws the recent world positions
 * of a weapon's blade.
 */
public class SwordTrail {

    private static class TrailPoint {
        double x, y;
        long time;

        TrailPoint(double x, double y, long time) {
            this.x = x;
            this.y = y;
            this.time = time;
        }
    }

    private final Deque<TrailPoint> points = new ArrayDeque<>();

    private static final long TRAIL_DURATION = 120;

    public void clear() {
        points.clear();
    }

    public void addPoint(double x, double y, long time) {
        points.addLast(new TrailPoint(x, y, time));
        while (!points.isEmpty() &&
                time - points.peekFirst().time > TRAIL_DURATION) {
            points.removeFirst();
        }
    }

    public void draw(Graphics2D g2, double cameraX, double cameraY,
            long time) {

        Composite oldComposite = g2.getComposite();
        Stroke oldStroke = g2.getStroke();
        Color oldColor = g2.getColor();

        TrailPoint previous = null;

        try {
            for (TrailPoint point : points) {
                if (previous != null) {

                    float opacity = (float) Math.max(0,
                            0.6 * (1.0 -
                                    (double) (time - point.time) / TRAIL_DURATION));

                    g2.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, opacity));

                    g2.setColor(new Color(145, 210, 255));
                    g2.setStroke(new BasicStroke(8,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND));

                    g2.drawLine(
                            (int) (previous.x - cameraX),
                            (int) (previous.y - cameraY),
                            (int) (point.x - cameraX),
                            (int) (point.y - cameraY));
                }

                previous = point;
            }
        } finally {
            g2.setComposite(oldComposite);
            g2.setStroke(oldStroke);
            g2.setColor(oldColor);
        }
    }
}
