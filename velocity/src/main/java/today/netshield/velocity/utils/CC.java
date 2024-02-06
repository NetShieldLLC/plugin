package today.netshield.velocity.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CC {
    public static Component colorize(String string) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
    }
}
