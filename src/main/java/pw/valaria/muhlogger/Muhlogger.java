package pw.valaria.muhlogger;

import com.destroystokyo.paper.utils.PaperPluginLogger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class Muhlogger extends JavaPlugin {

    List<String> loggers;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        // Plugin startup logic
        loggers.forEach((loggerName) -> {
            // Faux a PDF, the logic this calls literally doesn't care
            final Logger logger = PaperPluginLogger.getLogger(new PluginDescriptionFile(loggerName, "unknown", "unknown"));

            final Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                if (handler.getClass().getClassLoader() instanceof PluginClassLoader classLoader) {
                    if (classLoader.getPlugin().getName().equals(this.getName())) {
                        getLogger().removeHandler(handler);
                    }
                }
            }
            logger.addHandler(new Handler() {
                                       @Override
                                       public void publish(LogRecord record) {
                                           final TextComponent deserialize = LegacyComponentSerializer.legacySection().deserialize(record.getMessage());
                                           Bukkit.getConsoleSender().sendMessage(Component.text("[" + record.getLoggerName() + "] ").append(deserialize));
                                       }

                                       @Override
                                       public void flush() {

                                       }

                                       @Override
                                       public void close() throws SecurityException {

                                       }
                                   }
            );
            logger.setUseParentHandlers(false);
        });

    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        loggers = this.getConfig().getStringList("loggers");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
