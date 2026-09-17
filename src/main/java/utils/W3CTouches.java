package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Cross-platform W3C gesture helpers built on {@link PointerInput} / {@link Sequence}.
 */
public final class W3CTouches {

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static final PointerInput FINGER = new PointerInput(PointerInput.Kind.TOUCH, "finger");
    private static final PointerInput FINGER_2 = new PointerInput(PointerInput.Kind.TOUCH, "finger2");

    private W3CTouches() {
    }

    public static void tap(WebDriver driver, By locator) {
        WebElement element = Finder.elementVisibility(locator, driver);
        tap(driver, centerOf(element));
    }

    public static void tap(WebDriver driver, int x, int y) {
        tap(driver, new Point(x, y));
    }

    public static void tap(WebDriver driver, Point point) {
        Sequence tap = new Sequence(FINGER, 1);
        tap.addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), point.getX(), point.getY()));
        tap.addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        tap.addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        perform(driver, tap);
    }

    public static void doubleTap(WebDriver driver, By locator) {
        WebElement element = Finder.elementVisibility(locator, driver);
        doubleTap(driver, centerOf(element));
    }

    public static void doubleTap(WebDriver driver, Point point) {
        Sequence doubleTap = new Sequence(FINGER, 1);
        doubleTap.addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), point.getX(), point.getY()));
        doubleTap.addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(FINGER.createPointerMove(Duration.ofMillis(80), PointerInput.Origin.viewport(), point.getX(), point.getY()));
        doubleTap.addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        doubleTap.addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        perform(driver, doubleTap);
    }

    public static void longPress(WebDriver driver, By locator) {
        longPress(driver, locator, Duration.ofSeconds(2));
    }

    public static void longPress(WebDriver driver, By locator, Duration holdDuration) {
        WebElement element = Finder.elementVisibility(locator, driver);
        longPress(driver, centerOf(element), holdDuration);
    }

    public static void longPress(WebDriver driver, Point point, Duration holdDuration) {
        Sequence hold = new Sequence(FINGER, 1);
        hold.addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), point.getX(), point.getY()));
        hold.addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        hold.addAction(FINGER.createPointerMove(holdDuration, PointerInput.Origin.viewport(), point.getX(), point.getY()));
        hold.addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        perform(driver, hold);
    }

    public static void swipe(WebDriver driver, Direction direction) {
        swipe(driver, direction, 0.75);
    }

    /**
     * @param distanceRatio portion of screen size to travel (0.0–1.0)
     */
    public static void swipe(WebDriver driver, Direction direction, double distanceRatio) {
        Dimension size = driver.manage().window().getSize();
        int midX = size.getWidth() / 2;
        int midY = size.getHeight() / 2;
        int travelX = (int) (size.getWidth() * distanceRatio / 2);
        int travelY = (int) (size.getHeight() * distanceRatio / 2);

        Point start;
        Point end;
        switch (direction) {
            case UP -> {
                start = new Point(midX, midY + travelY);
                end = new Point(midX, midY - travelY);
            }
            case DOWN -> {
                start = new Point(midX, midY - travelY);
                end = new Point(midX, midY + travelY);
            }
            case LEFT -> {
                start = new Point(midX + travelX, midY);
                end = new Point(midX - travelX, midY);
            }
            case RIGHT -> {
                start = new Point(midX - travelX, midY);
                end = new Point(midX + travelX, midY);
            }
            default -> throw new IllegalArgumentException("Unknown direction: " + direction);
        }
        swipe(driver, start, end, Duration.ofMillis(600));
    }

    public static void swipe(WebDriver driver, Point start, Point end, Duration duration) {
        Sequence swipe = new Sequence(FINGER, 1);
        swipe.addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.getX(), start.getY()));
        swipe.addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(FINGER.createPointerMove(duration, PointerInput.Origin.viewport(), end.getX(), end.getY()));
        swipe.addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        perform(driver, swipe);
    }

    public static void dragAndDrop(WebDriver driver, By source, By target) {
        WebElement from = Finder.elementVisibility(source, driver);
        WebElement to = Finder.elementVisibility(target, driver);
        dragAndDrop(driver, centerOf(from), centerOf(to), Duration.ofMillis(800));
    }

    public static void dragAndDrop(WebDriver driver, Point start, Point end, Duration duration) {
        Sequence drag = new Sequence(FINGER, 1);
        drag.addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.getX(), start.getY()));
        drag.addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        drag.addAction(FINGER.createPointerMove(duration, PointerInput.Origin.viewport(), end.getX(), end.getY()));
        drag.addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        perform(driver, drag);
    }

    /**
     * Pinch-zoom gesture centered on the screen (or element if provided).
     *
     * @param scale &gt; 1 zooms in; &lt; 1 zooms out
     */
    public static void zoom(WebDriver driver, double scale) {
        Dimension size = driver.manage().window().getSize();
        Point center = new Point(size.getWidth() / 2, size.getHeight() / 2);
        zoom(driver, center, scale);
    }

    public static void zoom(WebDriver driver, By locator, double scale) {
        zoom(driver, centerOf(Finder.elementVisibility(locator, driver)), scale);
    }

    public static void zoom(WebDriver driver, Point center, double scale) {
        int offset = 120;
        int startOffset = scale >= 1.0 ? offset : (int) (offset * scale);
        int endOffset = scale >= 1.0 ? (int) (offset * scale) : offset;

        // Two fingers move apart (zoom in) or together (zoom out)
        Point finger1Start = new Point(center.getX() - startOffset, center.getY());
        Point finger1End = new Point(center.getX() - endOffset, center.getY());
        Point finger2Start = new Point(center.getX() + startOffset, center.getY());
        Point finger2End = new Point(center.getX() + endOffset, center.getY());

        if (scale < 1.0) {
            // zoom out: start apart, end closer
            finger1Start = new Point(center.getX() - endOffset, center.getY());
            finger1End = new Point(center.getX() - startOffset, center.getY());
            finger2Start = new Point(center.getX() + endOffset, center.getY());
            finger2End = new Point(center.getX() + startOffset, center.getY());
        }

        Duration move = Duration.ofMillis(500);

        Sequence finger1 = new Sequence(FINGER, 1);
        finger1.addAction(FINGER.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),
                finger1Start.getX(), finger1Start.getY()));
        finger1.addAction(FINGER.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        finger1.addAction(FINGER.createPointerMove(move, PointerInput.Origin.viewport(),
                finger1End.getX(), finger1End.getY()));
        finger1.addAction(FINGER.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        Sequence finger2 = new Sequence(FINGER_2, 1);
        finger2.addAction(FINGER_2.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(),
                finger2Start.getX(), finger2Start.getY()));
        finger2.addAction(FINGER_2.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        finger2.addAction(FINGER_2.createPointerMove(move, PointerInput.Origin.viewport(),
                finger2End.getX(), finger2End.getY()));
        finger2.addAction(FINGER_2.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        perform(driver, finger1, finger2);
    }

    private static Point centerOf(WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        return new Point(location.getX() + size.getWidth() / 2, location.getY() + size.getHeight() / 2);
    }

    @SuppressWarnings("unchecked")
    private static void perform(WebDriver driver, Sequence... sequences) {
        Finder.asAppium(driver).perform(List.of(sequences));
    }

    @SuppressWarnings("unchecked")
    private static void perform(WebDriver driver, Sequence sequence) {
        Finder.asAppium(driver).perform(Collections.singletonList(sequence));
    }
}
