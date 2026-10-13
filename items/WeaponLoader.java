
package items;

import combat.AnimationProfiles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Loads weapons and their sprites.
 */

public final class WeaponLoader {

    private WeaponLoader() {
    }

    public static Weapon loadKatana() {
        try {
            BufferedImage sprite = ImageIO.read(
                    WeaponLoader.class.getResourceAsStream(
                            "/res/weapons/katana.png"));

            return new Weapon(
                    "Katana",
                    AnimationProfiles.KATANA,
                    sprite,
                    0.5, 0.0, // Blade tip
                    0.5, 0.65 // Blade base
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not load Katana sprite", e);
        }
    }
}
